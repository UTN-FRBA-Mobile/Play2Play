package com.p2p

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before

abstract class BaseTest {

    protected val context: Context get() = ApplicationProvider.getApplicationContext()

    @Before
    fun baseSetup() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @After
    fun baseTearDown() {
        unmockkAll()
    }
}
