# drawable-loader
Android library to load drawables efficiently to ImageViews.

Drawable Loader offers the following features:
* **Exposes static asynchronous** and synchronous **methods** to decode Bitmap objects from different sources **outside the UI thread**, ensuring that the app runs smoothly.
* **Image auto and manual down sampling.** This library keeps memory usage of your app low by loading images in just the scale and size you need, only specifying Image holder's size (Or a manual downsample rate). If the device is still unable to load image due to low memory available, render methods will automatically recalculate image downsample for it to successfully fit in the device's memory, **avoiding that annoying OutOfMemoryError.**


## How to use Drawable Loader in your app

### 1.- Importing library
First of all, you need to import EpicBitmapRenderer library into your proyect. It is available on Bintray JCenter and Maven Central repositories. There are several ways to do this depending on your IDE and your project configuration.
Add it in your root build.gradle at the end of repositories:
``
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
``
Add the dependency:
```
implementation 'com.github.goonerDroid:drawable-loader:1.01'
```

### 2.- Initializing the library
Once Drawable Loader is successfully imported into your project, you will have access to the DrawableLoader class and its static methods to decode and render Bitmaps in the app.

Drawable Loader library uses a dual memory and disk cache to improve image decoding and rendering processes. Memory cache is automatically initialized when the library is loaded. However, disk cache requires a context to know what your app's own cache folder is and create it, so it needs to be manually initialized.
So the first step is initializing the library is called by calling

```
DrawableLoader.initLoader(this)
``` 

### 3.- Decoding Bitmaps
Drawable Loader is a static class containing only static methods, so you don't need to instantiate it to use the library. Here is an example, extracted from sample app, of calling a method to decode a Bitmap from a resource of your app, and then showing it on an ImageView, or handling the decoding error, if one occurs.

```
/*Sample 1: Decode Bitmap from Resource app icon, downsample if needed 
to fit in 200x200 ImageView, or any other sample size choice of yours. (Async)
 */
DrawableLoader.decodeBitmapFromResource(
					resources, R.drawable.<your_drawable>,DECODE_SAMPLE_SIZE,DECODE_SAMPLE_SIZE,
					object : OnBitmapRendered {
						override fun onBitmapRendered(bitmap: Bitmap?) {
							imageView.setImageBitmap(bitmap)
						}
					},
					object : OnBitmapRenderFailed {
						override fun onBitmapRenderFailed(e: Exception?) {
							Toast.makeText(
									requireContext(),
									"Failed to load Bitmap from Resource: " + e!!.message,
									Toast.LENGTH_SHORT
							).show()
						}
					})
```

That's it! Drawable Loader decodes the Bitmap asynchronously in a worker thread, stores it in memory and disk cache, and, if successful, returns decoded Bitmap at the end of the process on the onBitmapRendered(Bitmap) callback. Further calls to render methods pointing to the same resource, will obtain the decoded Bitmap from memory or disk cache if available, instead of rendering the resource again, thus saving memory usage.

Almost every decoding method has an alternate, overloaded synchronous method (same arguments without callbacks) in case you need them, but it's not recommended as they run on the UI thread and can freeze the app. Here is the same example as before, but calling the synchronous method:

```
val decodedBitmap: Bitmap? = DrawableLoader.decodeBitmapFromResource(resources,
					R.drawable.<your_drawable>, 200, 200)
imageView.setImageBitmap(decodedBitmap);
```