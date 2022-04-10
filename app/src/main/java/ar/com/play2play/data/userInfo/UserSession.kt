package ar.com.play2play.data.userInfo

/** An user session will save the user preferences on the time. */
class UserSession(private val storage: UserInfoStorage) {

    /** Save the user [name]. */
    fun saveUserName(name: String) = storage.saveUserName(name)

    /** Returns the user saved name. */
    fun getUserName(): String? = storage.getUserName()

    /** Returns the saved user name or a default value. */
    fun getUserNameOrEmpty(): String = storage.getUserNameOrEmpty()
}
