package com.p2p.framework

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.p2p.BaseTest
import com.p2p.R
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SharedPreferencesUserInfoStorageTest : BaseTest() {

    private lateinit var userInfoStorage: SharedPreferencesUserInfoStorage
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setup() {
        userInfoStorage = SharedPreferencesUserInfoStorage(context)
        val sharedPreferencesKey = context.getString(R.string.shared_preferences)
        sharedPreferences = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
    }

    @Test
    fun `given name when save user name then put it into shared preferences`() {

        // GIVEN
        val name = "some name"

        // WHEN
        userInfoStorage.saveUserName(name)

        // THEN
        assertThat(sharedPreferences.getString(SharedPreferencesUserInfoStorage.USER_NAME_KEY, null), `is`(name))
    }

    @Test
    fun `given saved user name when get user name them returns it`() {

        // GIVEN
        val name = "some name"
        sharedPreferences.edit { putString(SharedPreferencesUserInfoStorage.USER_NAME_KEY, name) }

        // WHEN
        val result = userInfoStorage.getUserName()

        // THEN
        assertThat(result, `is`(name))
    }
}
