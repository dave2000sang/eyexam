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
import android.content.Intent
import com.example.eyexam.EyeDistance
import android.hardware.camera2.CameraCharacteristics

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
            // GOTO isBad(dist)
            val intent = Intent(this, Results::class.java)
                .putExtra("resulto", isBad(dist))
            startActivity(intent)
        }
        examText.setText(dist.toString()).toString()

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Now it just directly exits if we add System.exit(), need to ask for permission
        }
    }

    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    // TODO Replace this with a CameraDevice usage
    /*
    fun getCameraInstance(): Camera? {
        return try {
            Camera.open() // attempts to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available
            null // Null if camera is unavailable
        }
    }
     **/

    private fun isBad(dist: Float): Boolean {
        return dist > 30
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