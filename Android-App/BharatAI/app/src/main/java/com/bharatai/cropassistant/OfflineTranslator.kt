
package com.bharatai.cropassistant

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import android.util.Log

class OfflineTranslator {

    suspend fun translate(
        text: String,
        targetLanguage: String
    ): String {

        if (text.isBlank()) return ""

        val targetCode = when (targetLanguage.trim().lowercase()) {
            "hindi" -> TranslateLanguage.HINDI
            "kannada" -> TranslateLanguage.KANNADA
            "telugu" -> TranslateLanguage.TELUGU
            else -> return text
        }
        Log.d(
            "TRANSLATION_DEBUG",
            "Requested language: $targetLanguage | Target code: $targetCode"
        )

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(targetCode)
            .build()

        val translator = Translation.getClient(options)

        return try {
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()

            suspendCancellableCoroutine { continuation ->

                translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        translator.translate(text)
                            .addOnSuccessListener { translatedText ->
                                if (continuation.isActive) {
                                    continuation.resume(translatedText)
                                    Log.d(
                                        "TRANSLATION_DEBUG",
                                        "Actual translated output: $translatedText"
                                    )
                                }
                            }
                            .addOnFailureListener { exception ->
                                if (continuation.isActive) {
                                    continuation.resumeWithException(exception)
                                }
                            }
                    }
                    .addOnFailureListener { exception ->
                        if (continuation.isActive) {
                            continuation.resumeWithException(exception)
                        }
                    }
            }
        } finally {
            translator.close()
        }
    }
}
