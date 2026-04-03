package com.gery711k.yettelteszt.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.serialization.json.Json

suspend inline fun <reified T: Any> DataStore<Preferences>.saveData(
    json: Json,
    key: Preferences.Key<String>,
    crossinline modify: (T?) -> T,
) = updateData { preferences ->
    preferences.toMutablePreferences().apply {
        get(key)?.let { jsonString ->
            json.decodeFromString<T>(jsonString)
        }.let { storedValue ->
            set(key, json.encodeToString(modify(storedValue)))
        }
    }
}