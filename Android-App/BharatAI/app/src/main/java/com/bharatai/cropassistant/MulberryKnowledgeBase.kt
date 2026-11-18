package com.bharatai.cropassistant

import android.content.Context
import org.json.JSONObject

data class MulberryDisease(
    val id: String,
    val classifierLabel: String,
    val diseaseName: String,
    val symptoms: List<String>,
    val treatmentGuidance: String?,
    val safetyNote: String
)

class MulberryKnowledgeBase(context: Context) {

    private val records: List<MulberryDisease>

    init {
        val json = context.assets.open(
            "knowledge_base/mulberry_diseases.json"
        ).bufferedReader().use { it.readText() }

        val root = JSONObject(json)
        val array = root.getJSONArray("records")

        records = (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            val symptomsJson = item.getJSONArray("symptoms")

            MulberryDisease(
                id = item.getString("id"),
                classifierLabel = item.getString("classifier_label"),
                diseaseName = item.getString("disease_name"),
                symptoms = (0 until symptomsJson.length()).map {
                    symptomsJson.getString(it)
                },
                treatmentGuidance = if (
                    item.isNull("treatment_guidance")
                ) null else item.getString("treatment_guidance"),
                safetyNote = item.getString("safety_note")
            )
        }
    }

    fun findDisease(label: String): MulberryDisease? {
        return records.firstOrNull {
            it.classifierLabel.equals(label, ignoreCase = true)
        }
    }
}