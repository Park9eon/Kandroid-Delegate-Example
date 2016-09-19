package sexy.park9eon.android

import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

/**
 * Created by park9eon on 9/19/16 - @
 */
class Model<T: Any>() {

    constructor(defaultValue: T?) : this() {
        this._value = defaultValue
    }

    private var _setters = mutableMapOf<Any, (T?)-> Unit>()
    private var _value: T? = null

    fun getValue(): T? {
        return this._value
    }

    fun setValue(value: T?) {
        this._value = value
        this._setters.forEach {
            it.value(this._value)
        }
    }

    operator fun getValue(thisRef: Any, property: KProperty<*>): T? {
        return this.getValue()
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        this.setValue(value)
    }

    fun addSetter(key: Any, setter: (T?)->Unit) {
        this._setters[key] = setter
    }

}


fun KMutableProperty1<*, *>.getModel(thisRef: Any): Model<*>? {
    this.javaField?.isAccessible = true
    return this.javaField?.get(thisRef) as? Model<*>
}