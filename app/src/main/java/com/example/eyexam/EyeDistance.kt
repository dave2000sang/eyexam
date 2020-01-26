package com.example.eyexam

import android.graphics.Bitmap
import android.media.FaceDetector
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.PointF




class EyeDistance {
    val IMG_WIDTH = 800
    val IMG_HEIGHT = 600
    val MAX_FACES = 1

    fun get_eye_distance(img: Bitmap): Float {
        var faceDetector = FaceDetector(img.width, img.height, MAX_FACES)

        val faces = arrayOfNulls<FaceDetector.Face>(5)
        var count = faceDetector.findFaces(img, faces)
        println("count = " + count)
        val face = faces[0]
        var eyesDistance: Float

        if (face != null) {
            println("break here 1")
            eyesDistance = face.eyesDistance()
            println(eyesDistance)
        } else {
            return -1.0f
        }
        return eyesDistance
    }
}
