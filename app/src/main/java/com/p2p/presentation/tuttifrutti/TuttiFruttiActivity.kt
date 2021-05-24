package com.p2p.presentation.tuttifrutti

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.p2p.presentation.base.BaseActivity
import com.p2p.presentation.tuttifrutti.create.CreateTuttiFruttiFragment

class TuttiFruttiActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            addFragment(
                CreateTuttiFruttiFragment.newInstance(instructions),
                shouldAddToBackStack = true
            )
        }
    }

    companion object {
        lateinit var instructions: String
        fun start(context: Context, instructions: String) {
            this.instructions = instructions
            context.startActivity(Intent(context, TuttiFruttiActivity::class.java))
        }
    }
}
