package sexy.park9eon.example

import android.view.View
import android.widget.TextView
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

/**
 * Created by park9eon on 9/13/16.
 */
// delegate
class Model<T: Any>() {

    constructor(defaultValue: T?) : this() {
        this._value = defaultValue
    }

    private var _setter: (T?)-> Unit = {}
    private var _value: T? = null

    fun getValue(): T? {
        return this._value
    }

    fun setValue(value: T?) {
        this._value = value
        this._setter(this._value)
    }

    operator fun getValue(thisRef: Any, property: KProperty<*>): T? {
        return this.getValue()
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        this.setValue(value)
    }

    fun setter(setter: (T?)->Unit) {
        this._setter = setter
    }

}

private fun View.bind(thisRef: Any, vararg properties: KMutableProperty1<*, *>, bind: (Any?)->Unit) {

    val size = properties.size - 1
    val first = properties.first()
    val model = first.getModel(thisRef)
    val value = model?.getValue()

    if (size == 0) {
        bind(value)
        model?.setter {
            bind(it)
        }
    } else if (size > 0 && value != null) {
        this.bind(value, *properties.slice(1..size).toTypedArray(), bind = bind)
        model?.setter {
            if (it != null) {
                this.bind(it, *properties.slice(1..size).toTypedArray(), bind = bind)
            }
        }
    }
}

fun <T> View.click(thisRef: T, function: KFunction<Unit>) {
    this.setOnClickListener {
        when (function.parameters.size) {
            1 -> function.call(thisRef)
            2 -> function.call(thisRef, it)
        }
    }
}

fun TextView.text(thisRef: Any, vararg properties: KMutableProperty1<*, *>) {
    this.bind(thisRef, *properties) {
        this.text = "$it"
    }
}

fun TextView.visible(thisRef: Any, vararg properties: KMutableProperty1<*, *>) {
    this.bind(thisRef, *properties) {
        this.visible(it)
    }
}

private fun TextView.visible(any: Any?) {
    this.visibility = when (any) {
        View.GONE -> View.GONE
        View.INVISIBLE -> View.INVISIBLE
        false -> View.GONE
        else -> View.VISIBLE
    }
}

fun KMutableProperty1<*, *>.getModel(thisRef: Any): Model<*>? {
    this.javaField?.isAccessible = true
    return this.javaField?.get(thisRef) as? Model<*>
}