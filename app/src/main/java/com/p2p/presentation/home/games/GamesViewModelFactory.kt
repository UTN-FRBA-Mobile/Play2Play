package com.p2p.presentation.home.games

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.p2p.data.UserSession
import com.p2p.framework.SharedPreferencesUserInfoStorage

class GamesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = modelClass
        .getConstructor(UserSession::class.java)
        .newInstance(UserSession(SharedPreferencesUserInfoStorage(context)))
}
