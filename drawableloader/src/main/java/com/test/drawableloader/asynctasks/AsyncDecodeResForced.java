package com.test.drawableloader.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.test.drawableloader.DrawableLoaderBitmapCache;
import com.test.drawableloader.listeners.OnBitmapRenderFailed;
import com.test.drawableloader.listeners.OnBitmapRendered;

/**
 * AsyncTask to decode a Bitmap from resource given its resource ID. InSampleSize parameter is forced to the value specified.
 */
public class AsyncDecodeResForced extends AsyncTask<Void, Void, Bitmap> {
    private final Resources resources;
    private final int resourceId;
    private final int inSampleSize;
    private Exception failException;
    private final DrawableLoaderBitmapCache drawableLoaderBitmapCache;

    private final OnBitmapRendered onBitmapRendered;
    private final OnBitmapRenderFailed onBitmapRenderFailed;


    /**
     * All parameters constructor.
     *
     * @param res                  Resources package. You can get default resources package using {@link Activity#getResources()} inside an activity or {@link Context#getResources()} outside if a {@link Context} is available.
     * @param resId                App resource id. Could be either the pure integer value, or the Android resource name (R.drawable.img_name).
     * @param inSampleSize         Determines how many times image resolution is divided to lower memory usage. Image aspect ratio is not affected by this parameter, just its resolution / quality is lowered.
     * @param onBitmapRendered     Overwrite this callback to retrieve {@link Bitmap} object rendered once it's ready and perform any other actions needed.
     * @param onBitmapRenderFailed Overwrite this callback to perform actions when {@link Bitmap} object fails to render. Can be null.
     * @param drawableLoaderBitmapCache      Cache to check if bitmap has already been rendered.
     */
    public AsyncDecodeResForced(Resources res, int resId,
                                int inSampleSize,
                                OnBitmapRendered onBitmapRendered,
                                OnBitmapRenderFailed onBitmapRenderFailed,
                                DrawableLoaderBitmapCache drawableLoaderBitmapCache) {
        this.resources = res;
        this.resourceId = resId;
        this.inSampleSize = inSampleSize;
        this.onBitmapRendered = onBitmapRendered;
        this.onBitmapRenderFailed = onBitmapRenderFailed;
        this.drawableLoaderBitmapCache = drawableLoaderBitmapCache;
    }

    /**
     * Basic constructor with just the required parameters.
     *
     * @param res              Resources package. You can get default resources package using {@link Activity#getResources()} inside an activity or {@link Context#getResources()} outside if a {@link Context} is available.
     * @param resId            App resource id. Could be either the pure integer value, or the Android resource name (R.drawable.img_name).
     * @param inSampleSize     Determines how many times image resolution is divided to lower memory usage. Image aspect ratio is not affected by this parameter, just its resolution / quality is lowered.
     * @param onBitmapRendered Overwrite this callback to retrieve {@link Bitmap} object rendered once it's ready and perform any other actions needed.
     */
    public AsyncDecodeResForced(Resources res, int resId,
                                int inSampleSize,
                                OnBitmapRendered onBitmapRendered) {
        this.resources = res;
        this.resourceId = resId;
        this.inSampleSize = inSampleSize;
        this.onBitmapRendered = onBitmapRendered;
        this.onBitmapRenderFailed = null;
        this.drawableLoaderBitmapCache = null;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap decodedBitmap = null;

        // Search bitmap on cache first if available
        if (drawableLoaderBitmapCache != null) {
            decodedBitmap = drawableLoaderBitmapCache.getBitmapFromCache(String.valueOf(resourceId));
        }

        //If bitmap not found on cache, render it
        if (decodedBitmap == null) {
            // Decode bitmap with inSampleSize set
            try {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = inSampleSize;
                decodedBitmap = BitmapFactory.decodeResource(resources, resourceId, options);

                //Add bitmap to cache if bitmap was successfully rendered and cache is available
                if (decodedBitmap != null && drawableLoaderBitmapCache != null) {
                    drawableLoaderBitmapCache.put(String.valueOf(resourceId), decodedBitmap, options.outMimeType, 100);
                }
            } catch (Exception e) {
                //Set failException for later launch fail callback on main thread
                failException = e;
            }
        }

        return decodedBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            if (onBitmapRendered != null) {
                //Call listener to return rendered bitmap
                onBitmapRendered.onBitmapRendered(bitmap);
            }
        } else if (onBitmapRenderFailed != null && failException != null) {
            //Call fail listener and send failException triggered
            onBitmapRenderFailed.onBitmapRenderFailed(failException);
        }
    }
}