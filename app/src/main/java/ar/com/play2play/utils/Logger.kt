package ar.com.play2play.utils

import android.util.Log
import ar.com.play2play.BuildConfig

object Logger {

    fun v(tag: String, msg: String, tr: Throwable? = null) {
        if (BuildConfig.DEBUG) Log.v(tag, msg, tr)
    }

    fun d(tag: String, msg: String, tr: Throwable? = null) {
        if (BuildConfig.DEBUG) Log.d(tag, msg, tr)
    }

    fun i(tag: String, msg: String, tr: Throwable? = null) {
        if (BuildConfig.DEBUG) Log.i(tag, msg, tr)
    }

    fun w(tag: String, msg: String, tr: Throwable? = null) {
        if (BuildConfig.DEBUG) Log.w(tag, msg, tr)
    }

    fun e(tag: String, msg: String, tr: Throwable? = null) {
        if (BuildConfig.DEBUG) Log.e(tag, msg, tr)
    }
}
