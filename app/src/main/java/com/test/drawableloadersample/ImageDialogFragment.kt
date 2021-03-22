package com.test.drawableloadersample

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.test.drawableloader.DrawableLoader
import com.test.drawableloader.listeners.OnBitmapRenderFailed
import com.test.drawableloader.listeners.OnBitmapRendered

@Suppress("PropertyName", "PrivatePropertyName")
class ImageDialogFragment : DialogFragment() {

	private val DECODE_SAMPLE_SIZE = 200

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_drawable_image, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val imageView: ImageView = view.findViewById(R.id.img)
		val textView: TextView = view.findViewById(R.id.drawableName)
		val drawable = arguments?.getInt("drawable")
		val drawableName = arguments?.getString("drawableName")
		if (drawable != null) {
			DrawableLoader.decodeBitmapFromResource(
					resources, drawable,DECODE_SAMPLE_SIZE,DECODE_SAMPLE_SIZE,
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

			textView.text = drawableName
		}
	}
}