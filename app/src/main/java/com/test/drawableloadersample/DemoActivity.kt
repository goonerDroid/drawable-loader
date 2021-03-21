package com.test.drawableloadersample

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.test.drawableloader.DrawableLoader
import com.test.drawableloader.listeners.OnBitmapRenderFailed
import com.test.drawableloader.listeners.OnBitmapRendered

class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        DrawableLoader.initDiskCache(this)




		DrawableLoader.decodeBitmapFromResource(
			resources, R.drawable.ic_app_shortcuts_write,
			object : OnBitmapRendered {
				override fun onBitmapRendered(bitmap: Bitmap?) {
					(findViewById<ImageView>(R.id.iv)).setImageBitmap(
						bitmap
					)

				}
			},
			object : OnBitmapRenderFailed {
				override fun onBitmapRenderFailed(e: Exception?) {
					Toast.makeText(
						this@DemoActivity,
						"Failed to load Bitmap from Resource: " + e!!.message,
						Toast.LENGTH_SHORT
					).show()
				}
			})
    }
}