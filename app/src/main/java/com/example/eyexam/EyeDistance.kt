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
        val bitmap565 = img.copy(Bitmap.Config.RGB_565, true)
        var face_detector = FaceDetector(img.width, img.height, MAX_FACES)

        val faces = arrayOfNulls<FaceDetector.Face>(1)
        face_detector.findFaces(bitmap565, faces)
        val face = faces[0]
        var eyes_distance: Float

        if (face != null) {
            println("break here 1")
            eyes_distance = face.eyesDistance()
            println(eyes_distance)
        } else {
            return -1.0f
        }
        return eyes_distance
    }
}
