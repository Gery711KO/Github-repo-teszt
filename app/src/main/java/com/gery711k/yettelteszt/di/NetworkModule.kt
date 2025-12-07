package com.gery711k.yettelteszt.di

import com.gery711k.yettelteszt.data.api.github.GitHubApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.create

val networkModule = module {
    single<Json> {
        Json { ignoreUnknownKeys = true }
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(
                get<Json>().asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

    factory {
        get<Retrofit>().create<GitHubApiService>()
    }
}