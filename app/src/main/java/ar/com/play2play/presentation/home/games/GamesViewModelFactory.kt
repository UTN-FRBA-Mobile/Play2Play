package ar.com.play2play.presentation.home.games

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.com.play2play.data.bluetooth.BluetoothStateProvider
import ar.com.play2play.data.userInfo.UserSession
import ar.com.play2play.framework.SharedPreferencesUserInfoStorage
import ar.com.play2play.framework.bluetooth.BluetoothStateProviderImp

class GamesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = modelClass
        .getConstructor(UserSession::class.java, BluetoothStateProvider::class.java)
        .newInstance(
            UserSession(SharedPreferencesUserInfoStorage(context)),
            BluetoothStateProviderImp()
        )
}
