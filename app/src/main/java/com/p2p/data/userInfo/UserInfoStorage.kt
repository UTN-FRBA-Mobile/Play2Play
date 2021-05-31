package com.p2p.data.userInfo

/** This interface brings the capacity to save user info into some storage. */
interface UserInfoStorage {

    /** Save the user [name]. */
    fun saveUserName(name: String)

    /** Returns the saved user name. */
    fun getUserName(): String?

    /** Returns the saved user name or a default value. */
    fun getUserNameOrEmpty(): String
}
