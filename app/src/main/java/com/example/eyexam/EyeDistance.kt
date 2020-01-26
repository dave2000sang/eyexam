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
        var face_detector = FaceDetector(IMG_WIDTH, IMG_HEIGHT, MAX_FACES)

        val faces = arrayOfNulls<FaceDetector.Face>(1)
        face_detector.findFaces(bitmap565, faces)
        val face = faces[0]
        var eyes_distance: Float

        if (face != null) {
            eyes_distance = face.eyesDistance()
        } else {
            return -1.0f
        }
        return eyes_distance
    }
}
