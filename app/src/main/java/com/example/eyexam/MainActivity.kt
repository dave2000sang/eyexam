package com.example.eyexam

// Imported libraries "Context" and "PackageManager"
import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCaptureSession
import java.io.IOException
import java.io.InputStream
import androidx.core.content.ContextCompat
import android.hardware.camera2.CameraCharacteristics
import androidx.core.app.ActivityCompat
import kotlin.system.exitProcess
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.view.Surface
import android.view.SurfaceHolder
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

// Check if this device has a camera
private fun checkCamera(context: Context): Boolean {
    return (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA))
}

class MainActivity : AppCompatActivity() {

    /** Helper to ask camera permission.  */
    object CameraPermissionHelper {
        const val CAMERA_PERMISSION_CODE = 0
        const val CAMERA_PERMISSION = Manifest.permission.CAMERA

        /** Check to see we have the necessary permissions for this app.  */
        fun hasCameraPermission(activity: Activity): Boolean {
            return ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED
        }

        /** Check to see we have the necessary permissions for this app, and ask for them if we don't.  */
        fun requestCameraPermission(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(CAMERA_PERMISSION), CAMERA_PERMISSION_CODE)
        }

        /** Check to see if we need to show the rationale for this permission.  */
        fun shouldShowRequestPermissionRationale(activity: Activity): Boolean {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION)
        }

        /** Launch Application Setting to grant permission.  */
        fun launchPermissionSettings(activity: Activity) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", activity.packageName, null)
            activity.startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                .show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }

        recreate()
    }

    private fun startCameraSession() {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraIdList = cameraManager.getCameraIdList()
        if (cameraIdList.isEmpty()) {
            // no cameras
            return
        }
        if (ContextCompat.checkSelfPermission(this, CameraPermissionHelper.CAMERA_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            CameraPermissionHelper.requestCameraPermission(this)
            return
        }
        val firstCamera = cameraIdList[0]

        cameraManager.openCamera(firstCamera, object : CameraDevice.StateCallback() {
            override fun onDisconnected(p0: CameraDevice) {}
            override fun onError(p0: CameraDevice, p1: Int) {}

            override fun onOpened(cameraDevice: CameraDevice) {
                // use the camera
                val cameraCharacteristics =
                    cameraManager.getCameraCharacteristics(cameraDevice.id)

                cameraCharacteristics[CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP]?.let { streamConfigurationMap ->
                    streamConfigurationMap.getOutputSizes(ImageFormat.YUV_420_888)
                        ?.let { yuvSizes ->
                            val previewSize = yuvSizes.last()
                            val displayRotation = windowManager.defaultDisplay.rotation
                            val swappedDimensions = areDimensionsSwapped(displayRotation, cameraCharacteristics)
                            // swap width and height if needed
                            val rotatedPreviewWidth = if (swappedDimensions) previewSize.height else previewSize.width
                            val rotatedPreviewHeight = if (swappedDimensions) previewSize.width else previewSize.height

                            // surface view
                            surfaceView.holder.setFixedSize(rotatedPreviewWidth, rotatedPreviewHeight)
                            val previewSurface = surfaceView.holder.surface
                            val captureCallback = object : CameraCaptureSession.StateCallback()
                            {
                                override fun onConfigureFailed(session: CameraCaptureSession) {}

                                override fun onConfigured(session: CameraCaptureSession) {
                                    // session configured
                                    val previewRequestBuilder =   cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                                        .apply {
                                            addTarget(previewSurface)
                                        }
                                    session.setRepeatingRequest(
                                        previewRequestBuilder.build(),
                                        object: CameraCaptureSession.CaptureCallback() {},
                                        Handler { true }
                                    )
                                }
                            }

                            cameraDevice.createCaptureSession(mutableListOf(previewSurface), captureCallback, Handler { true })


                        }
                }
            }
        }, Handler { true })

    }

    val surfaceReadyCallback = object: SurfaceHolder.Callback {
        override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) { }
        override fun surfaceDestroyed(p0: SurfaceHolder?) { }

        override fun surfaceCreated(p0: SurfaceHolder?) {
            startCameraSession()
        }
    }

    private fun areDimensionsSwapped(displayRotation: Int, cameraCharacteristics: CameraCharacteristics): Boolean {
        var swappedDimensions = false
        when (displayRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> {
                if (cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) == 90 || cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) == 270) {
                    swappedDimensions = true
                }
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                if (cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) == 0 || cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) == 180) {
                    swappedDimensions = true
                }
            }
            else -> {
                // invalid display rotation
            }
        }
        return swappedDimensions
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set text
        val examText = findViewById<TextView>(R.id.examText)
        var inputText = "Hello there! To utilize this app, start by holding your phone at full arm's length. Slowly bring your phone closer to your face until you can clearly make out the words in this text area and press the \"CAN SEE\" button."
        examText.setText(inputText).toString()

        // Instances
        var ed = EyeDistance()
        var bm = getBitmapFromAssets("test.jpg")
        var dist = 0f

        val textit = TextIt()
        textit.insert("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam vel vehicula nibh, sed ultrices arcu. In venenatis aliquet efficitur. Ut eget tincidunt orci. Donec tincidunt finibus venenatis. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nullam ut hendrerit metus. Vestibulum sollicitudin augue nec posuere maximus. Morbi tristique dignissim arcu aliquam posuere. Duis id porta dui, sit amet consequat purus. Sed faucibus, enim ac viverra tempus, nibh lorem tempus mauris, at tempus ipsum justo at libero. Nunc sodales rhoncus tristique. Ut tincidunt feugiat eros ac vehicula. ")
        textit.insert("Praesent fringilla diam at mauris euismod tempor. Morbi ac risus quis neque ornare consectetur vel ut turpis. In luctus dignissim venenatis. Nullam malesuada, erat quis pretium scelerisque, leo mi consequat ante, sed porttitor ex velit at mauris. Praesent sit amet ipsum mollis, lobortis ante sed, venenatis mauris. Sed ligula felis, sollicitudin vitae neque at, imperdiet ornare tellus. Mauris sed erat ut eros mattis iaculis. Nulla at turpis cursus, commodo erat ac, gravida tortor. Pellentesque et eros malesuada, elementum erat quis, tempor metus. Nulla aliquet sed turpis nec pharetra. ")
        textit.insert("Nulla facilisi. Vivamus vitae enim vitae nisl tristique condimentum porta ac libero. Praesent a ornare nulla, at rhoncus dui. Morbi non viverra dui, id pellentesque risus. Nam molestie neque leo, ac consectetur neque consequat vitae. Fusce quis augue pharetra, feugiat quam ac, elementum metus. Cras nisi elit, egestas ac semper in, interdum interdum lectus. Praesent vel quam quis nulla blandit venenatis non at mauris. In odio ante, posuere eget convallis a, suscipit quis nisi. Cras ultrices odio elementum venenatis porttitor. Ut efficitur porttitor quam, et ullamcorper enim auctor eu. Donec sodales euismod metus non ultricies. Nulla mollis elit tempus porttitor lacinia. Ut finibus quis tellus id faucibus. Morbi ullamcorper orci lorem, commodo fermentum neque congue in.")

        val textsize = textit.size()
        var textcounter = 0

        // Button behaviour
        val btnClick = findViewById<Button>(R.id.seeButton)
        btnClick.setOnClickListener {
            println("Clicked")
            dist = ed.get_eye_distance(bm)
            textit.store(textcounter, dist)
            print("dist = " + dist)
            // TODO this is the button click call
            if(textcounter >= textsize) {
                val intent = Intent(this, Results::class.java)
                    .putExtra("resulto", isBad(textit.average()))
                println("after intent set")
                startActivity(intent)
            }
            else {
                inputText = textit.at(textcounter)
                examText.setText(inputText).toString()
                ++textcounter
            }

        }
        //examText.setText(dist.toString()).toString()


        // Camera permissions
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this)
            return
        }

        surfaceView.holder.addCallback(surfaceReadyCallback)

    }

    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

//    override fun onRequestPermissionsResult
//                (requestCode: Int, permissions:Array<String>, grantResults: IntArray) {
//        if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_GRANTED)
//            exitProcess(1)
//    }

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