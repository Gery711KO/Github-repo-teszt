package com.gery711k.yettelteszt.di

import com.gery711k.yettelteszt.ui.screen.githubrepositorydetail.GitHubRepositoryDetailScreenViewModel
import com.gery711k.yettelteszt.ui.screen.githubrepositorylist.GitHubRepositoryListScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::GitHubRepositoryListScreenViewModel)
    viewModelOf(::GitHubRepositoryDetailScreenViewModel)
}