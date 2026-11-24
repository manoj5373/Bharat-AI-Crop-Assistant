
package com.bharatai.cropassistant

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File

class AdvisoryDatabaseHelper(
    private val context: Context
) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "bharat_ai_advisory.db"
        private const val DATABASE_VERSION = 1
    }

    private fun copyDatabaseIfNeeded() {
        val dbFile = context.getDatabasePath(DATABASE_NAME)

        if (!dbFile.exists()) {
            dbFile.parentFile?.mkdirs()

            context.assets.open(DATABASE_NAME).use { input ->
                dbFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    fun openAdvisoryDatabase(): SQLiteDatabase {
        copyDatabaseIfNeeded()
        return readableDatabase
    }

    fun getVerifiedAdvisories(
        stateCode: Int? = null,
        districtCode: Int? = null,
        languageCode: String = "en"
    ): List<AdvisoryItem> {

        val database = openAdvisoryDatabase()
        val advisories = mutableListOf<AdvisoryItem>()

        val query = """
    SELECT
        a.advisory_id,
        a.category,
        COALESCE(t.title, a.title),
        COALESCE(t.description, a.description),
        COALESCE(t.symptoms, a.symptoms),
        COALESCE(t.recommendation, a.recommendation),
        s.organization,
        s.source_title
    FROM advisories a
    LEFT JOIN advisory_sources s
        ON a.source_id = s.source_id
    LEFT JOIN advisory_translations t
        ON a.advisory_id = t.advisory_id
        AND t.language_code = ?
    WHERE a.verification_status = 'verified'
    AND (a.state_code IS NULL OR a.state_code = ?)
    AND (a.district_code IS NULL OR a.district_code = ?)
    ORDER BY a.category, a.title
""".trimIndent()

        database.rawQuery(
            query,
            arrayOf(
                languageCode,
                stateCode?.toString(),
                districtCode?.toString()
            )
        ).use { cursor ->

            while (cursor.moveToNext()) {
                advisories.add(
                    AdvisoryItem(
                        id = cursor.getInt(0),
                        category = cursor.getString(1),
                        title = cursor.getString(2),
                        description = cursor.getString(3),
                        symptoms = cursor.getString(4),
                        recommendation = cursor.getString(5),
                        sourceOrganization = cursor.getString(6),
                        sourceTitle = cursor.getString(7)
                    )
                )
            }
        }

        return advisories
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Database is pre-created and copied from assets.
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Database migration will be handled separately.
    }
}

data class AdvisoryItem(
    val id: Int,
    val category: String,
    val title: String,
    val description: String,
    val symptoms: String?,
    val recommendation: String?,
    val sourceOrganization: String?,
    val sourceTitle: String?
)
