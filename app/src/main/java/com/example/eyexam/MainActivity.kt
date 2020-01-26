package com.example.eyexam

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.InputStream
import com.example.eyexam.EyeDistance

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set text
        val examText = findViewById<TextView>(R.id.examText)
        val inputText = "Lorem Ipsum Hello World"
        examText.setText(inputText).toString()

        // Instances
        var ed = EyeDistance()
        var bm = getBitmapFromAssets("test.jpg")
        var dist = 0f

        // Button behaviour
        val btnClick = findViewById<Button>(R.id.seeButton)
        btnClick.setOnClickListener {
            // TODO this call crashes program
            dist = ed.get_eye_distance(bm)
        }
        examText.setText(dist.toString()).toString()
    }

    // put images into the "assets/images/" folder
    private fun getBitmapFromAssets(fileName: String): Bitmap {
        val assetManager = assets
        var inputStream: InputStream? = null
        try {
            inputStream = assetManager.open("images/$fileName")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return BitmapFactory.decodeStream(inputStream)
    }
}