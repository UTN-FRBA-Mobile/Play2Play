package com.p2p

import android.app.Application
import android.content.Intent
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

class P2PApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
                val sw = StringWriter()
                exception.printStackTrace(PrintWriter(sw))
                startActivity(Intent(Intent.ACTION_SEND).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Intent.EXTRA_TEXT, sw.toString())
                    type = "text/plain"
                })
                oldHandler?.uncaughtException(thread, exception)?: exitProcess(1)
            }
        }
    }
}
