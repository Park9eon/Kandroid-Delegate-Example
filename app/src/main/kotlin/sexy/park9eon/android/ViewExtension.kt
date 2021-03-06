package sexy.park9eon.android

import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty1

/**
 * Created by park9eon on 9/19/16.
 */
fun View.bind(thisRef: Any, vararg properties: KMutableProperty1<*, *>, listener: ((Model<Any>?)->Unit)? = null, binding: (Any?)->Unit) {

    val size = properties.size - 1
    val first = properties.first()
    val model = first.getModel(thisRef)
    val value = model?.getValue()

    if (size == 0) {
        model?.addSetter(this) {
            binding(it) // 값이 변경되었을 경우
        }
        binding(value) // 처음 설정했을 경우 초기값
        listener?.invoke(model as? Model<Any>)
    } else if (size > 0 && value != null) {
        model?.addSetter(this) {
            if (it != null) {
                this.bind(it, *properties.slice(1..size).toTypedArray(), listener = listener, binding = binding) // 값이 변경 되었을 경우!
            }
        }
        this.bind(value, *properties.slice(1..size).toTypedArray(), listener = listener, binding = binding) // 처음 설정했을 경우 초기값
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

fun TextView.html(thisRef: Any, vararg properties: KMutableProperty1<*, *>) {
    this.bind(thisRef, *properties) {
        val spannable = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml("$it", Html.FROM_HTML_MODE_LEGACY) as Spannable
        } else {
            Html.fromHtml("$it") as Spannable
        }
        this.setText(spannable, TextView.BufferType.SPANNABLE)
    }
}

fun EditText.text(thisRef: Any, vararg properties: KMutableProperty1<*, *>) {
    var textChangeListener: BindTextWatcher? = null
    val textWatcher: ((Model<Any>?)->Unit)? = {
        textChangeListener?.let {
            this.removeTextChangedListener(it)
        }
        textChangeListener = BindTextWatcher(it)
        this.addTextChangedListener(textChangeListener)
    }
    this.bind(thisRef, *properties, listener = textWatcher) {
        if (!"${this.text}".equals("$it")) {
            this.setText("$it", TextView.BufferType.EDITABLE)
        }
    }
}

class BindTextWatcher(val model: Model<Any>?) : TextWatcher {

    override fun afterTextChanged(p0: Editable?) { }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (!"${model?.getValue()}".equals("$p0")) {
            model?.setValue("$p0")
        }
    }
}

fun View.visible(thisRef: Any, vararg properties: KMutableProperty1<*, *>) {
    this.bind(thisRef, *properties) {
        this.visibility = when (it) {
            View.GONE -> View.GONE
            View.INVISIBLE -> View.INVISIBLE
            false -> View.GONE
            else -> View.VISIBLE
        }
    }
}

fun CompoundButton.checked(thisRef: Any, vararg properties: KMutableProperty1<*, *>) {
    val onCheckedChange: ((Model<Any>?)->Unit)? = {
        this.setOnCheckedChangeListener { compoundButton, b ->
            if (it?.getValue() is Boolean && it?.getValue() != compoundButton.isChecked) {
                it?.setValue(compoundButton.isChecked)
            }
        }
    }
    this.bind(thisRef, *properties, listener = onCheckedChange) {
        if (it is Boolean && it != this.isChecked) {
            this.isChecked = it
        }
    }
}

fun SeekBar.progress(thisRef: Any, vararg properties: KMutableProperty1<*, *>) {
    var onProgressChanged: ((Model<Any>?)->Unit)? = {
        this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(p0: SeekBar, p1: Int, p2: Boolean) {
                if (it?.getValue() is Int && it?.getValue() != p0.progress) {
                    it?.setValue(p0.progress)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) { }

            override fun onStopTrackingTouch(p0: SeekBar?) { }

        })
    }
    this.bind(thisRef, *properties, listener = onProgressChanged) {
        if (it is Int && it != this.progress) {
            this.progress = it
        }
    }
}
