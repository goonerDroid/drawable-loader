@file:Suppress("SameParameterValue")

package com.test.drawableloader

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.test.drawableloader.asynctasks.AsyncDecodeResForced
import com.test.drawableloader.asynctasks.AsyncDecodeResMeasured
import com.test.drawableloader.listeners.OnBitmapRenderFailed
import com.test.drawableloader.listeners.OnBitmapRendered

/**
 *
 *
 * Decode and render Bitmaps.*
 *
 *
 * This Android Bitmap decoder library follows the Google conventions for displaying bitmaps efficiently
 * (see [Google guide](https://developer.android.com/training/displaying-bitmaps/index.html?hl=es) for more info), offering these features:
 *
 *
 *  *
 * **Exposes static asynchronous** and synchronous **methods** to decode [Bitmap] objects from different sources **out of UI thread**,
 * ensuring that your app runs smoothly.
 *
 *  *
 * **Image auto and manual downsampling.** This library keeps memory usage of your app low by loading images in just the scale and size you need, only
 * specifying Image holder's size (Or a manual downsample rate). If device is still unable to load image due to low memory available, render methods
 * will automatically recalculate image downsample for it to successfully fit on device's memory, **avoiding that annoying [OutOfMemoryError]**
 *
 *  *
 * **Image auto caching.** Rendering methods automatically save rendered Bitmaps in memory and disk caches using dual cache [DrawableLoaderBitmapCache]. If an image
 * is previously rendered, next time it will be extracted from cache if available, and it will be used instead of re-rendering Bitmap from source again. This entire
 * process is automatic, as render methods handle cache themselves, and saves a lot of memory consumption from heavy processes like rendering images from disk or internet.
 *
 *
 */
object DrawableLoader {
    /**
     * Gets the instance of the current [DrawableLoaderBitmapCache] object being used as cache.
     *
     * @return Cache in use.
     */
    /**
     * Sets [DrawableLoaderBitmapCache] object to use as cache.
     *
     * @param cache Cache to use by renderer
     */
    private var cache: DrawableLoaderBitmapCache? = null


    //region Rendering Synchronous Methods
    /**
     *
     *
     * Decodes a sampled [Bitmap] object from a given app resource, using the specified measures to calculate image downsample if needed.
     * Downsample rate is auto-increased if bitmap rendering causes an [OutOfMemoryError].
     *
     *
     *
     * **Important Note:** This method is synchronous and can cause UI Thread to freeze,
     * use [.decodeBitmapFromResource] instead for an asynchronous solution.
     *
     *
     * @param res       Resources package. You can get default resources package using [Activity.getResources] inside an activity or [Context.getResources] outside if a [Context] is available.
     * @param resId     App resource id. Could be either the pure integer value, or the Android resource name (R.drawable.img_name).
     * @param reqWidth  Required width of the view where the Bitmap should fit. This parameter doesn't affect image aspect ratio, it's only used to calculate the inSampleSize of the image in case a downsample is required.
     * @param reqHeight Required height of the view where the Bitmap should fit. This parameter doesn't affect image aspect ratio, it's only used to calculate the inSampleSize of the image in case a downsample is required.
     * @return Decoded [Bitmap] object, ready to use on any View or code.
     */
    fun decodeBitmapFromResource(
        res: Resources?,
        resId: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        var decodedBitmap: Bitmap? = null
        var outOfMemoryError = true

        // Search bitmap on cache first if available
        if (cache != null) {
            decodedBitmap = cache!!.getBitmapFromCache(resId.toString())
        }

        //If bitmap not found on cache, render it
        if (decodedBitmap == null) {
            // First decode with inJustDecodeBounds=true (No memory allocation) to check dimensions
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeResource(res, resId, options)

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            while (outOfMemoryError) {
                try {
                    decodedBitmap = BitmapFactory.decodeResource(res, resId, options)

                    //Add bitmap to cache if bitmap was successfully rendered and cache is available
                    if (decodedBitmap != null && cache != null) {
                        cache!!.put(resId.toString(), decodedBitmap, options.outMimeType, 100)
                    }
                    outOfMemoryError = false
                } catch (e: OutOfMemoryError) {
                    //If inSampleSize still not enough to avoid out of memory error, increase it
                    options.inSampleSize *= 2
                    outOfMemoryError = true
                }
                if (options.inSampleSize >= 20) {
                    //Break loop in case of too many loops (something else is happening)
                    outOfMemoryError = false
                }
            }
        }
        return decodedBitmap
    }
    /**
     *
     *
     * Decodes a sampled [Bitmap] object from a given app resource, using the inSampleSize specified.
     *
     *
     *
     * **Important Note:** This method is synchronous and can cause UI Thread to freeze, use
     * [.decodeBitmapFromResource] instead for an asynchronous solution.
     *
     *
     * @param res          Resources package. You can get default resources package using [Activity.getResources] inside an activity or [Context.getResources] outside if a [Context] is available.
     * @param resId        App resource id. Could be either the pure integer value, or the Android resource name (R.drawable.img_name).
     * @param inSampleSize Determines how many times image resolution is divided to lower memory usage. Image aspect ratio is not affected by this parameter, just its resolution / quality is lowered.
     * @return Decoded [Bitmap] object, ready to use on any View or code.
     */
    /**
     *
     *
     * Decodes a [Bitmap] object from a given app resource, at its original dimensions.
     *
     *
     *
     * **Important Note:** This method is synchronous and can cause UI Thread to freeze,
     * use [.decodeBitmapFromResource] instead for an asynchronous solution.
     *
     *
     * @param res   Resources package. You can get default resources package using [Activity.getResources] inside an activity or [Context.getResources] outside if a [Context] is available.
     * @param resId App resource id. Could be either the pure integer value, or the Android resource name (R.drawable.img_name).
     * @return Decoded [Bitmap] object, ready to use on any View or code.
     */
    @JvmOverloads
    fun decodeBitmapFromResource(res: Resources?, resId: Int, inSampleSize: Int = 1): Bitmap? {
        var decodedBitmap: Bitmap? = null

        // Search bitmap on cache first if available
        if (cache != null) {
            decodedBitmap = cache!!.getBitmapFromCache(resId.toString())
        }

        //If bitmap not found on cache, render it
        if (decodedBitmap == null) {
            // Decode bitmap with inSampleSize set
            val options = BitmapFactory.Options()
            options.inSampleSize = inSampleSize
            decodedBitmap = BitmapFactory.decodeResource(res, resId, options)

            //Add bitmap to cache if bitmap was successfully rendered and cache is available
            if (decodedBitmap != null && cache != null) {
                cache!!.put(resId.toString(), decodedBitmap, options.outMimeType, 100)
            }
        }
        return decodedBitmap
    }
    //region Rendering Async methods
    /**
     * Decodes a sampled [Bitmap] object from a given app resource asynchronously, using the specified measures to calculate image downsample if needed.
     * Downsample rate is auto-increased if bitmap rendering causes an [OutOfMemoryError].
     *
     * @param res                  Resources package. You can get default resources package using [Activity.getResources] inside an activity or [Context.getResources] outside if a [Context] is available.
     * @param resId                App resource id. Could be either the pure integer value, or the Android resource name (R.drawable.img_name).
     * @param reqWidth             Required width of the view where the Bitmap should fit. This parameter doesn't affect image aspect ratio, it's only used to calculate the inSampleSize of the image in case a downsample is required.
     * @param reqHeight            Required height of the view where the Bitmap should fit. This parameter doesn't affect image aspect ratio, it's only used to calculate the inSampleSize of the image in case a downsample is required.
     * @param onBitmapRendered     Overwrite this callback to retrieve [Bitmap] object rendered once it's ready and perform any other actions needed.
     * @param onBitmapRenderFailed Overwrite this callback to perform actions when [Bitmap] object fails to render. Can be null.
     */
    fun decodeBitmapFromResource(
        res: Resources?, resId: Int,
        reqWidth: Int, reqHeight: Int,
        onBitmapRendered: OnBitmapRendered?,
        onBitmapRenderFailed: OnBitmapRenderFailed?
    ) {
        //Launch renderer AsyncTask
        AsyncDecodeResMeasured(
            res,
            resId,
            reqWidth,
            reqHeight,
            onBitmapRendered,
            onBitmapRenderFailed,
            cache
        ).execute()
    }

    /**
     * Decodes a sampled [Bitmap] object from a given app resource asynchronously, using the inSampleSize specified.
     * Downsample rate is auto-increased if bitmap rendering causes an [OutOfMemoryError].
     *
     * @param res                  Resources package. You can get default resources package using [Activity.getResources] inside an activity or [Context.getResources] outside if a [Context] is available.
     * @param resId                App resource id. Could be either the pure integer value, or the Android resource name (R.drawable.img_name).
     * @param inSampleSize         Determines how many times image resolution is divided to lower memory usage. Image aspect ratio is not affected by this parameter, just its resolution / quality is lowered.
     * @param onBitmapRendered     Overwrite this callback to retrieve [Bitmap] object rendered once it's ready and perform any other actions needed.
     * @param onBitmapRenderFailed Overwrite this callback to perform actions when [Bitmap] object fails to render. Can be null.
     */
    private fun decodeBitmapFromResource(
        res: Resources?, resId: Int,
        inSampleSize: Int,
        onBitmapRendered: OnBitmapRendered?,
        onBitmapRenderFailed: OnBitmapRenderFailed?
    ) {
        //Launch renderer AsyncTask
        AsyncDecodeResForced(
            res,
            resId,
            inSampleSize,
            onBitmapRendered,
            onBitmapRenderFailed,
            cache
        ).execute()
    }

    /**
     * Decodes a [Bitmap] object from a given app resource asynchronously, at its original dimensions.
     * Downsample rate is auto-increased if bitmap rendering causes an [OutOfMemoryError].
     *
     * @param res                  Resources package. You can get default resources package using [Activity.getResources] inside an activity or [Context.getResources] outside if a [Context] is available.
     * @param resId                App resource id. Could be either the pure integer value, or the Android resource name (R.drawable.img_name).
     * @param onBitmapRendered     Overwrite this callback to retrieve [Bitmap] object rendered once it's ready and perform any other actions needed.
     * @param onBitmapRenderFailed Overwrite this callback to perform actions when [Bitmap] object fails to render. Can be null.
     */
    fun decodeBitmapFromResource(
        res: Resources?, resId: Int,
        onBitmapRendered: OnBitmapRendered?,
        onBitmapRenderFailed: OnBitmapRenderFailed?
    ) {
        decodeBitmapFromResource(res, resId, 1, onBitmapRendered, onBitmapRenderFailed)
    }
    //region Cache methods

    fun initLoader(context: Context?){
        initDiskCache(context)
    }

    /**
     *
     * Initializes disk caches.
     *
     *
     * **Important Note:** Until an alternative is found, this method must be called (Just once for whole app life cycle, and in any time)
     * before using any render method, other way, [Bitmap] objects rendered won't be stored on disk cache, just in memory cache.
     *
     *
     * @param context [Context] from where lib is being called. This is used to get application's cache dir for disk cache.
     */
    private fun initDiskCache(context: Context?) {
        cache!!.initDiskCache(context)
    }

    //endregion Cache methods
    //region Helper methods
    /**
     * Calculates downsample rate, if needed, for an image depending of width and height it should fit on.
     *
     * @param options   [android.graphics.BitmapFactory.Options] object containing image info.
     * @param reqWidth  Required width of the view where the Bitmap should fit.
     * @param reqHeight Required height of the view where the Bitmap should fit.
     * @return int representing inSampleSize, a.k.a. the number of times image resolution is divided to lower memory usage.
     * @see [Google conventions for calculate inSampleSize.](https://developer.android.com/training/displaying-bitmaps/load-bitmap.html.load-bitmap)
     */
    @JvmStatic
    fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if ((reqHeight > 0 || reqWidth > 0) && (height > reqHeight || width > reqWidth)) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight
                && halfWidth / inSampleSize > reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    } //endregion Helper methods
    //endregion Fields
    //region Constructors and initialization
    /**
     * Library initializations. This block is called when library is loaded.
     */
    init {
        cache = DrawableLoaderBitmapCache()
    }
}