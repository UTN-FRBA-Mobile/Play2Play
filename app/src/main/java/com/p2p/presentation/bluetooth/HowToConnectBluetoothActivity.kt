package com.p2p.presentation.bluetooth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.p2p.R
import com.p2p.presentation.base.BaseActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class HowToConnectBluetoothActivity : BaseActivity(R.layout.activity_how_to_connect_bluetooth) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<TextView>(R.id.instructions).text = readRawTextFile(baseContext, R.raw.how_to_connect_bluetooth)
        findViewById<Button>(R.id.understood_button).setOnClickListener { finish() }
    }

    fun readRawTextFile(ctx: Context, resId: Int): String? { // TODO: use what Bren did
        val inputStream = ctx.resources.openRawResource(resId)
        val inputreader = InputStreamReader(inputStream)
        val buffreader = BufferedReader(inputreader)
        var line: String?
        val text = StringBuilder()
        try {
            while (buffreader.readLine().also { line = it } != null) {
                text.append(line)
                text.append('\n')
            }
        } catch (e: IOException) {
            return null
        }
        return text.toString()
    }

    companion object {

        fun start(context: Context) = context.startActivity(Intent(context, HowToConnectBluetoothActivity::class.java))
    }
}
