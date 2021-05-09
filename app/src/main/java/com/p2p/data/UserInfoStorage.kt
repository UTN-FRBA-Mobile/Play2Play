package com.p2p.data

/** This interface brings the capacity to save valueson same storage. */
interface UserInfoStorage {

    /** Save the user [name]. */
    fun saveUserName(name: String)

    /** Returns the saved user name. */
    fun getUserName(): String?
}
