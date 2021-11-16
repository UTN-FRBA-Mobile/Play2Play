package ar.com.play2play.framework

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.annotation.VisibleForTesting
import androidx.core.content.edit
import ar.com.play2play.R
import ar.com.play2play.data.userInfo.UserInfoStorage

/** A [UserInfoStorage] implementation for shared preferences. */
class SharedPreferencesUserInfoStorage(private val context: Context) : UserInfoStorage {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(context.getString(R.string.shared_preferences), MODE_PRIVATE)
    }

    override fun saveUserName(name: String) = sharedPreferences.edit { putString(USER_NAME_KEY, name) }

    override fun getUserName(): String? = sharedPreferences.getString(USER_NAME_KEY, null)

    override fun getUserNameOrEmpty(): String = getUserName() ?: context.resources.getString(R.string.unknown)

    companion object {

        @VisibleForTesting
        const val USER_NAME_KEY = "P2P_USER_NAME_KEY"
    }
}
