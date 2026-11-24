
package com.bharatai.cropassistant

import android.graphics.Bitmap
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

object MulberrySeverityPreprocessor {

    private const val INPUT_SIZE = 224
    private const val PIXELS = INPUT_SIZE * INPUT_SIZE
    private const val CHANNELS = 3

    private const val COEFFICIENT_BITS = 22
    private const val COEFFICIENT_SCALE = 1 shl COEFFICIENT_BITS
    private const val ROUNDING = 1 shl (COEFFICIENT_BITS - 1)

    private val MEAN = floatArrayOf(
        0.485f, 0.456f, 0.406f
    )

    private val STD = floatArrayOf(
        0.229f, 0.224f, 0.225f
    )

    private data class Weights(
        val starts: IntArray,
        val coefficients: Array<IntArray>
    )

    private fun createWeights(
        inputLength: Int,
        outputLength: Int
    ): Weights {

        val scale = inputLength.toDouble() / outputLength
        val filterScale = max(1.0, scale)
        val support = filterScale

        val starts = IntArray(outputLength)
        val coefficients = Array(outputLength) { IntArray(0) }

        for (out in 0 until outputLength) {

            val center = (out + 0.5) * scale
            val left = max(0, floor(center - support - 0.5).toInt())
            val right = minOf(
                inputLength,
                ceil(center + support - 0.5).toInt()
            )

            starts[out] = left

            val raw = DoubleArray(right - left)
            var total = 0.0

            for (i in left until right) {
                val distance = (i + 0.5 - center) / filterScale
                val weight = max(0.0, 1.0 - kotlin.math.abs(distance))

                raw[i - left] = weight
                total += weight
            }

            coefficients[out] = IntArray(raw.size) { i ->
                (raw[i] / total * COEFFICIENT_SCALE)
                    .toInt()
            }
        }

        return Weights(starts, coefficients)
    }

    private fun resizeAntialiased(bitmap: Bitmap): IntArray {

        val width = bitmap.width
        val height = bitmap.height

        val horizontalWeights =
            createWeights(width, INPUT_SIZE)

        val verticalWeights =
            createWeights(height, INPUT_SIZE)

        val source = IntArray(width * height)

        bitmap.getPixels(
            source, 0, width,
            0, 0, width, height
        )

        // Horizontal pass: width x height -> 224 x height.
        val horizontal = IntArray(INPUT_SIZE * height)

        for (y in 0 until height) {
            for (x in 0 until INPUT_SIZE) {

                val start = horizontalWeights.starts[x]
                val weights = horizontalWeights.coefficients[x]

                var red = 0L
                var green = 0L
                var blue = 0L

                for (k in weights.indices) {
                    val pixel = source[y * width + start + k]
                    val weight = weights[k].toLong()

                    red += ((pixel shr 16) and 0xFF) * weight
                    green += ((pixel shr 8) and 0xFF) * weight
                    blue += (pixel and 0xFF) * weight
                }

                val r = ((red + ROUNDING) shr COEFFICIENT_BITS)
                    .toInt().coerceIn(0, 255)
                val g = ((green + ROUNDING) shr COEFFICIENT_BITS)
                    .toInt().coerceIn(0, 255)
                val b = ((blue + ROUNDING) shr COEFFICIENT_BITS)
                    .toInt().coerceIn(0, 255)

                horizontal[y * INPUT_SIZE + x] =
                    (r shl 16) or (g shl 8) or b
            }
        }

        // Vertical pass: 224 x height -> 224 x 224.
        val result = IntArray(PIXELS)

        for (y in 0 until INPUT_SIZE) {
            val start = verticalWeights.starts[y]
            val weights = verticalWeights.coefficients[y]

            for (x in 0 until INPUT_SIZE) {

                var red = 0L
                var green = 0L
                var blue = 0L

                for (k in weights.indices) {
                    val pixel =
                        horizontal[(start + k) * INPUT_SIZE + x]

                    val weight = weights[k].toLong()

                    red += ((pixel shr 16) and 0xFF) * weight
                    green += ((pixel shr 8) and 0xFF) * weight
                    blue += (pixel and 0xFF) * weight
                }

                val r = ((red + ROUNDING) shr COEFFICIENT_BITS)
                    .toInt().coerceIn(0, 255)
                val g = ((green + ROUNDING) shr COEFFICIENT_BITS)
                    .toInt().coerceIn(0, 255)
                val b = ((blue + ROUNDING) shr COEFFICIENT_BITS)
                    .toInt().coerceIn(0, 255)

                result[y * INPUT_SIZE + x] =
                    (r shl 16) or (g shl 8) or b
            }
        }

        return result
    }

    fun preprocess(bitmap: Bitmap): FloatArray {

        val resizedPixels = resizeAntialiased(bitmap)
        val output = FloatArray(CHANNELS * PIXELS)

        for (i in resizedPixels.indices) {

            val pixel = resizedPixels[i]

            val red = ((pixel shr 16) and 0xFF) / 255.0f
            val green = ((pixel shr 8) and 0xFF) / 255.0f
            val blue = (pixel and 0xFF) / 255.0f

            output[i] = (red - MEAN[0]) / STD[0]
            output[PIXELS + i] = (green - MEAN[1]) / STD[1]
            output[2 * PIXELS + i] = (blue - MEAN[2]) / STD[2]
        }

        return output
    }
}
