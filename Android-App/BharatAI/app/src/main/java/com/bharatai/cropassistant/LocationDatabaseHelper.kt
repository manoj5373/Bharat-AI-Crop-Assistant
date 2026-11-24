
package com.bharatai.cropassistant

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File

class LocationDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    init {
        copyDatabaseIfNeeded(context)
    }

    companion object {
        private const val DATABASE_NAME = "bharat_ai_locations.db"
        private const val DATABASE_VERSION = 1
    }

    private fun copyDatabaseIfNeeded(context: Context) {
        val databaseFile = context.getDatabasePath(DATABASE_NAME)

        if (!databaseFile.exists()) {
            databaseFile.parentFile?.mkdirs()

            context.assets.open(DATABASE_NAME).use { input ->
                databaseFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Database already contains the preloaded LGD tables.
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Database upgrades will be handled separately.
    }

    fun getStates(): List<Pair<Int, String>> {
        val result = mutableListOf<Pair<Int, String>>()

        readableDatabase.rawQuery(
            "SELECT state_code, state_name_english FROM states ORDER BY state_name_english",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(cursor.getInt(0) to cursor.getString(1))
            }
        }

        return result
    }

    fun getDistricts(stateCode: Int): List<Pair<Int, String>> {
        val result = mutableListOf<Pair<Int, String>>()

        readableDatabase.rawQuery(
            """
            SELECT district_code, district_name_english
            FROM districts
            WHERE state_code = ?
            ORDER BY district_name_english
            """.trimIndent(),
            arrayOf(stateCode.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(cursor.getInt(0) to cursor.getString(1))
            }
        }

        return result
    }

    fun getSubDistricts(
        stateCode: Int,
        districtCode: Int
    ): List<Pair<Int, String>> {
        val result = mutableListOf<Pair<Int, String>>()

        readableDatabase.rawQuery(
            """
            SELECT subdistrict_code, subdistrict_name_english
            FROM sub_districts
            WHERE state_code = ? AND district_code = ?
            ORDER BY subdistrict_name_english
            """.trimIndent(),
            arrayOf(stateCode.toString(), districtCode.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(cursor.getInt(0) to cursor.getString(1))
            }
        }

        return result
    }

    fun getVillages(
        stateCode: Int,
        districtCode: Int,
        subDistrictCode: Int
    ): List<Pair<Int, String>> {
        val result = mutableListOf<Pair<Int, String>>()

        readableDatabase.rawQuery(
            """
            SELECT village_code, village_name_english
            FROM villages
            WHERE state_code = ?
              AND district_code = ?
              AND subdistrict_code = ?
            ORDER BY village_name_english
            """.trimIndent(),
            arrayOf(
                stateCode.toString(),
                districtCode.toString(),
                subDistrictCode.toString()
            )
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(cursor.getInt(0) to cursor.getString(1))
            }
        }

        return result
    }
}
