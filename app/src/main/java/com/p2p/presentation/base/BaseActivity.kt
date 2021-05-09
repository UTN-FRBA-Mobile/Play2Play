package com.p2p.presentation.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.p2p.R

/** Base implementation of an [AppCompatActivity] used to simplify boilerplate. */
abstract class BaseActivity : AppCompatActivity(R.layout.activity_base) {

    /** Add the [fragment] to the activity and if [shouldAddToBackStack] it'll added to the fragments stack */
    protected fun addFragment(fragment: BaseFragment<*, *, *>, shouldAddToBackStack: Boolean) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container_view, fragment)
            if (shouldAddToBackStack) addToBackStack(null)
        }
    }
}
