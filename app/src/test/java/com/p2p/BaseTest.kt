package com.p2p

import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

abstract class BaseTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun baseSetup() {
        MockKAnnotations.init(this, relaxed = true)
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun baseTearDown() {
        unmockkAll()
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }
}
