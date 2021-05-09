package com.p2p.presentation.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.p2p.BaseTest
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BaseViewModelTest : BaseTest() {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK
    lateinit var lifecycleOwner: LifecycleOwner

    lateinit var viewModel: BaseViewModelImp

    @Before
    fun setup() {
        viewModel = BaseViewModelImp()
        every { lifecycleOwner.lifecycle } returns LifecycleRegistry(lifecycleOwner).apply {
            handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    @Test
    fun `given observed single time event when dispatch event then observer is invoked`() {

        // GIVEN
        val observer = spyk(Observer<Any> {})
        viewModel.singleTimeEvent.observe(lifecycleOwner, observer)
        val event = "some_event"

        // WHEN
        viewModel.publicDispatchSingleTimeEvent(event)

        // THEN
        verify(exactly = 1) { observer.onChanged(event) }
    }

    @Test
    fun `given observed single time event when co dispatch event then observer is invoked`() {

        // GIVEN
        val observer = spyk(Observer<Any> {})
        viewModel.message.observe(lifecycleOwner, observer)
        val message = mockk<BaseViewModel.MessageData>()

        // WHEN
        viewModel.publicDispatchMessage(message)

        // THEN
        verify(exactly = 1) { observer.onChanged(message) }
    }

    class BaseViewModelImp : BaseViewModel<Any>() {

        fun publicDispatchSingleTimeEvent(event: Any) = dispatchSingleTimeEvent(event)

        fun publicDispatchMessage(message: MessageData) = dispatchMessage(message)
    }
}
