package com.bharatai.cropassistant

import android.content.Context
import android.graphics.Bitmap
import org.pytorch.executorch.EValue
import org.pytorch.executorch.Module
import org.pytorch.executorch.Tensor
import java.io.File
import kotlin.math.exp
import android.util.Log

data class MulberryPrediction(
    val classIndex: Int,
    val diseaseName: String,
    val confidence: Float,
    val logits: FloatArray
)

class MulberryClassifier(context: Context) {

    companion object {
        private const val MODEL_FILE = "mulberry_classifier_151.pte"
        private const val IMAGE_SIZE = 224

        private val LABELS = listOf(
            "Disease Free leaves",
            "Leaf Rust",
            "Leaf spot"
        )
    }

    private val module: Module

    init {
        val modelFile = File(context.filesDir, MODEL_FILE)

        if (!modelFile.exists()) {
            context.assets.open(MODEL_FILE).use { input ->
                modelFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        module = Module.load(modelFile.absolutePath)
    }

    fun predict(bitmap: Bitmap): MulberryPrediction {
        android.util.Log.d("Classifier_Debug", "1. Starting image resize")
        val resized = Bitmap.createScaledBitmap(
            bitmap,
            IMAGE_SIZE,
            IMAGE_SIZE,
            true
        )
        android.util.Log.d("Classifier_Debug", "2. Image resize completed")

        val pixels = IntArray(IMAGE_SIZE * IMAGE_SIZE)
        resized.getPixels(
            pixels, 0, IMAGE_SIZE, 0, 0,
            IMAGE_SIZE, IMAGE_SIZE
        )
        android.util.Log.d("Classifier_Debug", "3. Pixel extraction completed")

        val input = FloatArray(1 * 3 * IMAGE_SIZE * IMAGE_SIZE)

        val mean = floatArrayOf(0.485f, 0.456f, 0.406f)
        val std = floatArrayOf(0.229f, 0.224f, 0.225f)
        val planeSize = IMAGE_SIZE * IMAGE_SIZE

        for (i in pixels.indices) {
            val pixel = pixels[i]

            val red = ((pixel shr 16) and 0xFF) / 255f
            val green = ((pixel shr 8) and 0xFF) / 255f
            val blue = (pixel and 0xFF) / 255f

            input[i] = (red - mean[0]) / std[0]
            input[planeSize + i] = (green - mean[1]) / std[1]
            input[2 * planeSize + i] = (blue - mean[2]) / std[2]
        }

        val inputTensor = Tensor.fromBlob(
            input,
            longArrayOf(1, 3, IMAGE_SIZE.toLong(), IMAGE_SIZE.toLong())
        )
        android.util.Log.d("Classifier_Debug", "4. Input tensor created")

        var finalLogits: FloatArray? = null

        for (testRun in 1..5) {
            android.util.Log.d(
                "BharatAI_RepeatTest",
                "Starting inference $testRun"
            )

            android.util.Log.d(
                "BharatAI_RepeatTest",
                "Inference $testRun: entering module.forward()"
            )

            val outputs = module.forward(EValue.from(inputTensor))

            android.util.Log.d(
                "BharatAI_RepeatTest",
                "Inference $testRun: returned from module.forward()"
            )
            val logits = outputs[0].toTensor().dataAsFloatArray

            val allFinite = logits.all { it.isFinite() }

            android.util.Log.d(
                "BharatAI_RepeatTest",
                "Inference $testRun output: ${logits.contentToString()}"
            )

            android.util.Log.d(
                "BharatAI_RepeatTest",
                "Inference $testRun: outputCount=${logits.size}, allFinite=$allFinite"
            )

            check(logits.size == 3)
            check(allFinite)

            finalLogits = logits
        }

        android.util.Log.d(
            "BharatAI_RepeatTest",
            "ALL FIVE INFERENCES COMPLETED"
        )

        val logits = finalLogits!!

        require(logits.size == LABELS.size) {
            "Expected 3 model outputs, received ${logits.size}"
        }

        val maxIndex = logits.indices.maxByOrNull { logits[it] } ?: 0

        val maxLogit = logits[maxIndex]
        val expScores = logits.map { exp((it - maxLogit).toDouble()) }
        val confidence = (
                expScores[maxIndex] / expScores.sum()
                ).toFloat()

        return MulberryPrediction(
            classIndex = maxIndex,
            diseaseName = LABELS[maxIndex],
            confidence = confidence,
            logits = logits
        )
    }
}