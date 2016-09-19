package sexy.park9eon.example

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_test.*
import kotlin.reflect.KClass

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val viewModel = ViewModel()
        textView.text(viewModel, ViewModel::model, SomeModel::count, SonModel::count)
        button.text(viewModel, ViewModel::message)
        button.click(viewModel, ViewModel::upCount)
        textView.visible(viewModel, ViewModel::visible)
    }

    // 이것을 활용해서 바인딩합시다.
    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
    }
}

fun FragmentActivity.attach(id: Int, cls: KClass<Fragment>, tag: String, addToStack: Boolean = false, models: (Bundle)->Unit) {
    val manager = supportFragmentManager
    var _fragment = manager.findFragmentById(id)
    if (_fragment != null && _fragment.tag.equals(tag)) {
        return
    }
    _fragment = manager.findFragmentByTag(tag)
    val transaction = if (_fragment == null) {
        manager.beginTransaction()
               .add(id, cls.objectInstance, tag)
    } else {
        manager.beginTransaction().attach(_fragment)
    }
    if (addToStack == true) {
        transaction.addToBackStack(null)
    }
    transaction.commit()
}

class TestFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_test, container, false)
    }

}

class ViewModel() {

    var count = 0
    var message by Model("hello, world") // Int model
    var visible by Model(true)
    var model by Model(SomeModel())

    fun upCount() {
        this.model?.count?.count = "${++count}"
        visible = count % 3 != 0
        if (count == 10) {
            // this.visible = null
            this.model = SomeModel()
        }
        if (count == 14) {
            this.model?.count = SonModel(1234)
        }
    }
}

class SomeModel() {
    var count by Model(SonModel(123))
}

class SonModel(defaultInt: Int) {
    var count by Model("$defaultInt")
}
