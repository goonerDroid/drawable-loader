package com.test.drawableloader.listeners

import android.graphics.Bitmap

/**
 * Listener to get a callback when Bitmap is successfully rendered
 */
interface OnBitmapRendered {
    /**
     * Callback to be invoked when Bitmap is successfully rendered
     *
     * @param bitmap [Bitmap] object rendered
     */
    fun onBitmapRendered(bitmap: Bitmap?)
}