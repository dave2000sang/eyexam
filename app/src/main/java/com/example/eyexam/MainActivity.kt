package com.example.eyexam

// Imported libraries "Context" and "PackageManager"
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.pm.PackageManager

// Check if this device has a camera
private fun checkCamera(context: Context): Boolean {
    return (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA))
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set text
        val examText = findViewById<TextView>(R.id.examText)
        val inputText = "Lorem Ipsum Hello World"
        examText.setText(inputText).toString()

        // Button behaviour
        val btnClick = findViewById<Button>(R.id.seeButton)
        btnClick.setOnClickListener {
            // TODO Call david's eye distance function

        }
    }
}