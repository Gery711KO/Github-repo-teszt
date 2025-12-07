package com.gery711k.yettelteszt.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.gery711k.yettelteszt.data.datasource.github.DefaultGitHubDataSource
import com.gery711k.yettelteszt.data.datasource.github.GitHubDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.new
import org.koin.dsl.module

private const val dataStoreKey = "temp_key"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = dataStoreKey)

val dataSourceModule = module {
    single<CoroutineDispatcher> {
        Dispatchers.IO
    }

    single {
        get<Context>().dataStore
    }

    single<GitHubDataSource> {
        new(::DefaultGitHubDataSource)
    }
}