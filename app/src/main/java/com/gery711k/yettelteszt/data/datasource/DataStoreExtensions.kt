package com.gery711k.yettelteszt.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.serialization.json.Json

suspend inline fun <reified T: Any> DataStore<Preferences>.saveData(
    json: Json,
    key: Preferences.Key<String>,
    crossinline modify: (T?) -> T,
) = edit { preferences ->
    preferences[key]?.let { jsonString ->
        json.decodeFromString<T>(jsonString)
    }.let { storedValue ->
        preferences[key] = json.encodeToString(modify(storedValue))
    }
}