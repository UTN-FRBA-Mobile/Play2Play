package com.p2p.presentation.tuttifrutti

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.framework.InstructionsLocalResourcesSource
import com.p2p.presentation.base.BaseActivity
import com.p2p.presentation.base.IntentKeys
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.tuttifrutti.create.CreateTuttiFruttiFragment

class TuttiFruttiActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            addFragment(
                CreateTuttiFruttiFragment.newInstance(),
                shouldAddToBackStack = true
            )
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TuttiFruttiActivity::class.java)
            intent.putExtras(createBundle(context))
            context.startActivity(intent)
        }

        private fun createBundle(context: Context): Bundle{
            val bundle = Bundle()
            val repository = InstructionsRepository(InstructionsLocalResourcesSource(context))
            bundle.putString( IntentKeys.INSTRUCTIONS.key,
                repository.getInstructions(Game.TUTTI_FRUTTI))
            return bundle
        }
    }
}
