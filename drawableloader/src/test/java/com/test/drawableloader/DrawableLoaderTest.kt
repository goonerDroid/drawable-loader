package com.test.drawableloader

import android.content.Context
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


class DrawableLoaderTest {
    @Mock
    private val contextMock: Context? = null

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
    }


    @Test
    fun isValidContext(){
        `when`(contextMock?.applicationContext).thenReturn(contextMock)
    }
}