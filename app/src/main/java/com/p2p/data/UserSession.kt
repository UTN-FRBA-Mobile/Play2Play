package com.p2p.data

/** An user session will save the user preferences on the time. */
class UserSession(private val storage: UserInfoStorage) {

    /** Save the user [name]. */
    fun saveUserName(name: String) = storage.saveUserName(name)

    /** Returns the user saved name. */
    fun getUserName(): String? = storage.getUserName()
}
