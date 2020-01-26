package com.example.eyexam

// Imported libraries "Context" and "PackageManager"
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.pm.PackageManager
import java.io.IOException
import java.io.InputStream
import androidx.core.content.ContextCompat
import android.Manifest
import com.example.eyexam.EyeDistance
import android.hardware.camera2.CameraCharacteristics
import androidx.core.app.ActivityCompat
import kotlin.system.exitProcess

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

        // Instances
        var ed = EyeDistance()
        var bm = getBitmapFromAssets("test.jpg")
        var dist = 0f

        // Button behaviour
        val btnClick = findViewById<Button>(R.id.seeButton)
        btnClick.setOnClickListener {
            // TODO this call crashes program
            println("Clicked")
            dist = ed.get_eye_distance(bm)
            print("dist = " + dist)
        }
        examText.setText(dist.toString()).toString()

        // Permission check
        // TODO: No permission request popup!
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // If the app does not have the CAMERA permission, request it
            var requestCamera = 0

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA), requestCamera)
        }
    }

    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    override fun onRequestPermissionsResult
                (requestCode: Int, permissions:Array<String>, grantResults: IntArray) {
        if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_GRANTED)
            exitProcess(1)
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