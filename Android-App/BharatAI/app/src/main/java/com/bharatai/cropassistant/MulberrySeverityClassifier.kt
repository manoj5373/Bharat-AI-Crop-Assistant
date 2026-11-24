
package com.bharatai.cropassistant

import android.content.Context
import org.pytorch.executorch.EValue
import org.pytorch.executorch.Module
import org.pytorch.executorch.Tensor
import java.io.File
import kotlin.math.exp

data class SeverityPrediction(
    val classIndex: Int,
    val severityName: String,
    val confidence: Float,
    val scores: FloatArray,
    val logits: FloatArray
)

class MulberrySeverityClassifier(context: Context) {

    companion object {
        private const val MODEL_FILE = "mulberry_severity_151.pte"
        private const val INPUT_SIZE = 224
        private const val INPUT_ELEMENTS =
            3 * INPUT_SIZE * INPUT_SIZE

        private val LABELS = listOf(
            "Healthy",
            "Mild",
            "Moderate",
            "Severe"
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

    /**
     * Accepts an already-preprocessed RGB float tensor.
     *
     * Required format:
     * NCHW = [1, 3, 224, 224]
     *
     * Preprocessing must match the Python training pipeline.
     */
    fun predictPreprocessed(input: FloatArray): SeverityPrediction {

        require(input.size == INPUT_ELEMENTS) {
            "Expected $INPUT_ELEMENTS input values, received ${input.size}"
        }

        val inputTensor = Tensor.fromBlob(
            input,
            longArrayOf(1, 3, INPUT_SIZE.toLong(), INPUT_SIZE.toLong())
        )

        val outputs = module.forward(EValue.from(inputTensor))
        val logits = outputs[0].toTensor().dataAsFloatArray

        require(logits.size == LABELS.size) {
            "Expected 4 outputs, received ${logits.size}"
        }

        val maxIndex = logits.indices.maxByOrNull {
            logits[it]
        } ?: 0

        val maxLogit = logits[maxIndex]

        val expScores = logits.map {
            exp((it - maxLogit).toDouble())
        }

        val sum = expScores.sum()

        val scores = expScores.map {
            (it / sum).toFloat()
        }.toFloatArray()

        return SeverityPrediction(
            classIndex = maxIndex,
            severityName = LABELS[maxIndex],
            confidence = scores[maxIndex],
            scores = scores,
            logits = logits
        )
    }
}
