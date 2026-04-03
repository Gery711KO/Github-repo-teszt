package com.gery711k.yettelteszt.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DataStoreExtensionsTest {

    private val dataStore: DataStore<Preferences> = mockk()
    private val json = Json { ignoreUnknownKeys = true }
    private val key = stringPreferencesKey("test_key")

    @Test
    fun `saveData updates datastore correctly`() = runTest {
        // given
        val initialValue = "initial"
        val expectedValue = "modified"
        val preferences = mockk<Preferences>()
        val mutablePreferences = mockk<MutablePreferences>(relaxed = true)

        every { mutablePreferences[key] } returns json.encodeToString(initialValue)
        every { preferences.toMutablePreferences() } returns mutablePreferences

        val transformSlot = slot<suspend (Preferences) -> Preferences>()
        coEvery { dataStore.updateData(capture(transformSlot)) } coAnswers {
            transformSlot.captured(preferences)
        }

        assertEquals(mutablePreferences[key], json.encodeToString(initialValue))

        // when
        dataStore.saveData<String>(json, key) { "modified" }

        // then
        verify {
            mutablePreferences[key] = json.encodeToString(expectedValue)
        }
    }
}