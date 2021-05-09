package com.p2p.data

import com.p2p.BaseTest
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class UserSessionTest : BaseTest() {

    @MockK
    lateinit var userInfoStorage: UserInfoStorage

    private lateinit var userSession: UserSession

    @Before
    fun setup() {
        userSession = UserSession(userInfoStorage)
    }

    @Test
    fun `given name when save user name then save it into storage`() {

        // GIVEN
        val name = "some name"

        // WHEN
        userSession.saveUserName(name)

        // THEN
        verify(exactly = 1) { userInfoStorage.saveUserName(name) }
    }

    @Test
    fun `given a storage with a user name when get user name then returns it`() {

        // GIVEN
        val name = "some name"
        every { userInfoStorage.getUserName() } returns name

        // WHEN
        val result = userSession.getUserName()

        // THEN
        assertThat(result, `is`(name))
    }
}
