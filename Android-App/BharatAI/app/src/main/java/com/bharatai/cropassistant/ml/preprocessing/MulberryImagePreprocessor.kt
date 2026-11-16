
package com.bharatai.cropassistant.ml.preprocessing

import android.graphics.Bitmap
import kotlin.math.pow

object MulberryImagePreprocessor {

    private const val IMAGE_SIZE = 224

    private val mean = floatArrayOf(
        0.485f,
        0.456f,
        0.406f
    )

    private val std = floatArrayOf(
        0.229f,
        0.224f,
        0.225f
    )

    fun preprocess(bitmap: Bitmap): FloatArray {

        // Resize image to 224 × 224
        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            IMAGE_SIZE,
            IMAGE_SIZE,
            true
        )

        val pixels = IntArray(IMAGE_SIZE * IMAGE_SIZE)

        resizedBitmap.getPixels(
            pixels,
            0,
            IMAGE_SIZE,
            0,
            0,
            IMAGE_SIZE,
            IMAGE_SIZE
        )

        // Allocate NCHW tensor data: 3 × 224 × 224
        val tensor = FloatArray(3 * IMAGE_SIZE * IMAGE_SIZE)

        for (y in 0 until IMAGE_SIZE) {
            for (x in 0 until IMAGE_SIZE) {

                val pixel = pixels[y * IMAGE_SIZE + x]

                val red = ((pixel shr 16) and 0xFF) / 255f
                val green = ((pixel shr 8) and 0xFF) / 255f
                val blue = (pixel and 0xFF) / 255f

                val index = y * IMAGE_SIZE + x

                // Normalize RGB channels
                tensor[index] = (red - mean[0]) / std[0]

                tensor[IMAGE_SIZE * IMAGE_SIZE + index] =
                    (green - mean[1]) / std[1]

                tensor[2 * IMAGE_SIZE * IMAGE_SIZE + index] =
                    (blue - mean[2]) / std[2]
            }
        }

        return tensor
    }
}
