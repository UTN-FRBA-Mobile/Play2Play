package ar.com.play2play.presentation.bluetooth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import ar.com.play2play.R
import ar.com.play2play.presentation.base.BaseActivity
import ar.com.play2play.utils.getString

class HowToConnectBluetoothActivity : BaseActivity(R.layout.activity_how_to_connect_bluetooth) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<TextView>(R.id.instructions).text = resources
            .openRawResource(R.raw.how_to_connect_bluetooth)
            .getString()
        findViewById<Button>(R.id.understood_button).setOnClickListener { finish() }
    }

    companion object {

        fun start(context: Context) = context.startActivity(Intent(context, HowToConnectBluetoothActivity::class.java))
    }
}
