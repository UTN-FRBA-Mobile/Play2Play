package com.p2p.presentation.tuttifrutti.create

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.p2p.data.tuttifrutti.TuttiFruttiRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.framework.CategoriesLocalResourcesSource
import com.p2p.framework.SharedPreferencesUserInfoStorage

class CreateTuttiFruttiViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = modelClass
        .getConstructor(TuttiFruttiRepository::class.java)
        .newInstance(TuttiFruttiRepository(CategoriesLocalResourcesSource(listOf())))
}
