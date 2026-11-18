
package com.bharatai.cropassistant

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "farmer_profile"
)

data class FarmerProfile(
    val name: String = "",
    val phone: String = "",
    val village: String = "",
    val district: String = "",
    val state: String = ""
)

class ProfileDataStore(private val context: Context) {

    private object Keys {
        val NAME = stringPreferencesKey("farmer_name")
        val PHONE = stringPreferencesKey("farmer_phone")
        val VILLAGE = stringPreferencesKey("farmer_village")
        val DISTRICT = stringPreferencesKey("farmer_district")
        val STATE = stringPreferencesKey("farmer_state")
    }

    // Read saved profile information
    val profile: Flow<FarmerProfile> = context.dataStore.data.map { preferences ->
        FarmerProfile(
            name = preferences[Keys.NAME] ?: "",
            phone = preferences[Keys.PHONE] ?: "",
            village = preferences[Keys.VILLAGE] ?: "",
            district = preferences[Keys.DISTRICT] ?: "",
            state = preferences[Keys.STATE] ?: ""
        )
    }

    // Save profile information
    suspend fun saveProfile(profile: FarmerProfile) {
        context.dataStore.edit { preferences ->
            preferences[Keys.NAME] = profile.name
            preferences[Keys.PHONE] = profile.phone
            preferences[Keys.VILLAGE] = profile.village
            preferences[Keys.DISTRICT] = profile.district
            preferences[Keys.STATE] = profile.state
        }
    }
}