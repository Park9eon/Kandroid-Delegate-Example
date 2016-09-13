package sexy.park9eon.example

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val viewModel = ViewModel()

        textView.text(viewModel, ViewModel::countStr)
        textView.visibility
        button.text(viewModel, ViewModel::message)
        button.click(viewModel, ViewModel::upCount)
    }
}

class ViewModel() {

    var count = 0

    var message by Model("hello, world") // Int model
    var countStr by Model("$count")
    var visible by Model(View.VISIBLE)

    fun upCount() {
        countStr = "${++count}"
        if (count % 3 == 0) {
            visible = View.GONE
        }
    }
}
