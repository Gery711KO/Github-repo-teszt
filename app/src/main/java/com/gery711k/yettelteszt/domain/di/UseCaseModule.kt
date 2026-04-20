package com.gery711k.yettelteszt.domain.di

import com.gery711k.yettelteszt.domain.usecase.github.GetRepositoryDetailByIdUseCase
import com.gery711k.yettelteszt.domain.usecase.github.GetSearchHistoryUseCase
import com.gery711k.yettelteszt.domain.usecase.github.GetStoredQueryResultsUseCase
import com.gery711k.yettelteszt.domain.usecase.github.LoadMoreUseCase
import com.gery711k.yettelteszt.domain.usecase.github.SearchGitHubRepositoriesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::SearchGitHubRepositoriesUseCase)
    factoryOf(::LoadMoreUseCase)
    factoryOf(::GetStoredQueryResultsUseCase)
    factoryOf(::GetSearchHistoryUseCase)
    factoryOf(::GetRepositoryDetailByIdUseCase)
}