package com.p2p.presentation.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.p2p.BaseTest
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SingleLiveEventTest : BaseTest() {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK
    lateinit var lifecycleOwner: LifecycleOwner

    @Before
    fun setup() {
        every { lifecycleOwner.lifecycle } returns LifecycleRegistry(lifecycleOwner).apply {
            handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    @Test
    fun `given a single live event when observe then it's is invoked just once`() {

        // GIVEN
        val firstObserver = spyk(Observer<Any> {})
        val secondObserver = spyk(Observer<Any> {})
        val singleLiveEvent = SingleLiveEvent<Any>().apply {
            value = Any()
        }

        // WHEN
        singleLiveEvent.observe(lifecycleOwner, firstObserver)
        singleLiveEvent.observe(lifecycleOwner, secondObserver)

        // THEN
        verify(exactly = 1) { firstObserver.onChanged(any()) }
        verify(exactly = 0) { secondObserver.onChanged(any()) }
    }
}
