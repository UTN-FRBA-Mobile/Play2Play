package com.p2p.framework

import android.app.Activity
import android.content.Intent
import androidx.core.content.FileProvider
import com.p2p.BuildConfig
import java.io.File

object ApplicationSharer {

    fun share(activity: Activity) {
        val app = activity.applicationContext.applicationInfo
        val filePath = app.sourceDir
        val intent = Intent(Intent.ACTION_SEND).apply {
            // MIME of .apk is "application/vnd.android.package-archive".
            // but Bluetooth does not accept this. Let's use "*/*" instead.
            type = "*/*"
        }
        //Open share dialog
        val uri = FileProvider.getUriForFile(
            activity.baseContext,
            "${BuildConfig.APPLICATION_ID}.provider",
            File(filePath)
        )
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        activity.grantUriPermission(
            activity.packageManager.toString(),
            uri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        activity.startActivity(intent)
    }
}
