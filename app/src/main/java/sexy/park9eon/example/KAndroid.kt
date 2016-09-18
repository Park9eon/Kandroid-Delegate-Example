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

fun <T> View.click(thisRef: T, function: KFunction<Unit>) {
    this.setOnClickListener {
        when (function.parameters.size) {
            1 -> function.call(thisRef)
            2 -> function.call(thisRef, it)
        }
    }
}

fun <T, R> TextView.text(thisRef: T, property: KMutableProperty1<T, R>) {
    val model = property.getModel(thisRef)
    this.text = model?.getValue() as? CharSequence
    model?.setter {
        this.text = it as? CharSequence
    }
}

fun <T, R> TextView.visible(thisRef: T, property: KMutableProperty1<T, R>) {
    val model = property.getModel(thisRef)
    this.visibility = when (model?.getValue()) {
        View.GONE -> View.GONE
        View.INVISIBLE -> View.INVISIBLE
        false -> View.GONE
        else -> View.VISIBLE
    }
    model?.setter {
        this.visibility = when (it) {
            View.GONE -> View.GONE
            View.INVISIBLE -> View.INVISIBLE
            false -> View.GONE
            else -> View.VISIBLE
        }
    }
}

fun <T, R> KMutableProperty1<T, R>.getModel(thisRef: T): Model<*>? {
    this.javaField?.isAccessible = true
    return this.javaField?.get(thisRef) as? Model<*>
}