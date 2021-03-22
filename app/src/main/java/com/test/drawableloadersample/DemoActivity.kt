package com.test.drawableloadersample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.drawableloader.DrawableLoader
import com.test.drawableloadersample.databinding.ActivityDemoBinding

class DemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDemoBinding
    private val drawablesMap: HashMap<String, Int> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //init loader
        DrawableLoader.initLoader(this)


        //init recyclerview
        val drawableList: HashMap<String, Int> = loadAllDrawables()
        val drawableAdapter = DrawableAdapter(drawableList, this::onViewImageClick)
        binding.rvDrawableList.apply {
            layoutManager = LinearLayoutManager(this@DemoActivity)
            adapter = drawableAdapter
        }
    }

    private fun onViewImageClick(adapterPosition: Int) {
        val fm: FragmentManager = supportFragmentManager
        val dialogFragment = ImageDialogFragment()
        val args = Bundle()
        args.putInt("drawable", drawablesMap[drawablesMap.keys.toTypedArray()[adapterPosition]]!!)
        args.putString("drawableName",drawablesMap.keys.toTypedArray()[adapterPosition])
        dialogFragment.arguments = args
        dialogFragment.show(fm, "Image Dialog Fragment")
    }

    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun loadAllDrawables(): HashMap<String, Int> {
        drawablesMap["andro1"] = R.drawable.andro1
        drawablesMap["andro2"] = R.drawable.andro2
        drawablesMap["andro3"] = R.drawable.andro3
        drawablesMap["andro_m1"] = R.drawable.andro_m1
        drawablesMap["andro_m2"] = R.drawable.andro_m2
        return drawablesMap
    }
}