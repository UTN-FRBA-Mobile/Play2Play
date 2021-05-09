package com.p2p.presentation.extensions

import android.widget.EditText
import com.p2p.BaseTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditTextExtensionsTest : BaseTest() {

    private lateinit var editText: EditText

    @Before
    fun setup() {
        editText = EditText(context)
    }

    @Test
    fun `given edittext with some text when clear and append new text then the text is only the new`() {

        // GIVEN
        val newText = "some new text"
        editText.setText("some text")

        // WHEN
        editText.clearAndAppend(newText)

        // THEN
        assertThat(editText.text?.toString(), `is`(newText))
    }
}
