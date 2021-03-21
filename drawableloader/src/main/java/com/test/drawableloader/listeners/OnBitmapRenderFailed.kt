package com.test.drawableloader.listeners

/**
 * Listener to get a callback when Bitmap fails to render
 */
interface OnBitmapRenderFailed {
    /**
     * Callback to be invoked when bitmap fails to render
     *
     * @param e [Exception] that caused the render fail
     */
    fun onBitmapRenderFailed(e: Exception?)
}