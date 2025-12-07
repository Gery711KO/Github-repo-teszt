package com.gery711k.yettelteszt.di

import com.gery711k.yettelteszt.domain.repository.github.DefaultGitHubRepository
import com.gery711k.yettelteszt.domain.repository.github.GitHubRepository
import org.koin.core.module.dsl.new
import org.koin.dsl.module

val repositoryModule = module {
    single<GitHubRepository> {
        new(::DefaultGitHubRepository)
    }
}