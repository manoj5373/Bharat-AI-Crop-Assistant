package com.bharatai.cropassistant

import android.content.Context
import org.pytorch.executorch.extension.llm.LlmCallback
import org.pytorch.executorch.extension.llm.LlmGenerationConfig
import org.pytorch.executorch.extension.llm.LlmModule
import org.pytorch.executorch.extension.llm.LlmModuleConfig
import java.io.File
import java.io.FileOutputStream
import android.util.Log

class LlamaAssistant(private val context: Context) {

    private var module: LlmModule? = null

    companion object {
        private const val MODEL_ASSET =
            "models/bharat_llama_3_2_1b_qc4.pte"

        private const val TOKENIZER_ASSET =
            "models/tokenizer.model"
    }

    private fun copyAsset(assetName: String, outputFile: File): File {
        if (outputFile.exists() && outputFile.length() > 0L) {
            return outputFile
        }

        outputFile.parentFile?.mkdirs()

        val temporaryFile = File(outputFile.parentFile, outputFile.name + ".tmp")

        try {
            context.assets.open(assetName).use { input ->
                FileOutputStream(temporaryFile).use { output ->
                    input.copyTo(output)
                    output.fd.sync()
                }
            }

            check(temporaryFile.length() > 0L) {
                "Empty asset: $assetName"
            }

            check(temporaryFile.renameTo(outputFile)) {
                "Could not save asset: $assetName"
            }

            return outputFile
        } finally {
            if (temporaryFile.exists()) {
                temporaryFile.delete()
            }
        }
    }

    fun load() {
        if (module != null) return

        val modelFile = copyAsset(
            MODEL_ASSET,
            File(context.filesDir, "llama/model.pte")
        )

        val tokenizerFile = copyAsset(
            TOKENIZER_ASSET,
            File(context.filesDir, "llama/tokenizer.model")
        )

        val config = LlmModuleConfig.create()
            .modulePath(modelFile.absolutePath)
            .tokenizerPath(tokenizerFile.absolutePath)
            .temperature(0.2f)
            .modelType(LlmModuleConfig.MODEL_TYPE_TEXT)
            .numBos(1)
            .numEos(0)
            .loadMode(LlmModuleConfig.LOAD_MODE_MMAP)
            .build()

        val newModule = LlmModule(config)

        try {
            newModule.load()
            module = newModule
        } catch (error: Exception) {
            newModule.close()
            throw error
        }
    }

    fun generate(
        prompt: String,
        callback: LlmCallback
    ) {
        val activeModule = module
            ?: throw IllegalStateException("Llama model is not loaded")

        activeModule.resetContext()

        val generationConfig = LlmGenerationConfig.create()
            .seqLen(128)
            .temperature(0.0f)
            .echo(false)
            .numBos(1)
            .build()

        Log.d("LLAMA_DEBUG", "ACTUAL PROMPT: $prompt")
        Log.d("LLAMA_DEBUG", "PROMPT LENGTH: ${prompt.length}")
        Log.d("LLAMA_DEBUG", "PROMPT UTF8 BYTES: ${prompt.toByteArray(Charsets.UTF_8).size}")


        android.util.Log.d("LLAMA_DEBUG", "CONFIG CHECK: seqLen=128")

        Log.d("LLAMA_DEBUG", "Prompt starts with: ${prompt.take(80)}")
        Log.d("LLAMA_DEBUG", "Prompt ends with: ${prompt.takeLast(80)}")
        Log.d("LLAMA_DEBUG", "Generation seqLen: 128")

        activeModule.generate(
            prompt,
            generationConfig,
            object : LlmCallback {

                override fun onResult(result: String) {

                    if (result.contains("<|eot_id|>")) {
                        Log.d("LLAMA_DEBUG", "EOT detected. Stopping generation.")

                        val visibleText = result.substringBefore("<|eot_id|>")

                        if (visibleText.isNotEmpty()) {
                            callback.onResult(visibleText)
                        }

                        activeModule.stop()
                        return
                    }

                    callback.onResult(result)
                }

                override fun onStats(stats: String) {
                    callback.onStats(stats)
                }

                override fun onError(errorCode: Int, message: String) {
                    callback.onError(errorCode, message)
                }
            }
        )

    }

    fun close() {
        module?.close()
        module = null
    }
}