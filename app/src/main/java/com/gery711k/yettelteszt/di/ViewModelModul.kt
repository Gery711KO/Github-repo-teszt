package com.gery711k.yettelteszt.di

import com.gery711k.yettelteszt.ui.navigation.Navigator
import com.gery711k.yettelteszt.ui.navigation.destination.GitHubRepositoryListKey
import com.gery711k.yettelteszt.ui.screen.dashboard.DashboardScreenViewModel
import com.gery711k.yettelteszt.ui.screen.repositorydetail.RepositoryDetailScreenViewModel
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::DashboardScreenViewModel)
    viewModelOf(::RepositoryDetailScreenViewModel)

    activityRetainedScope {
        scoped {
            Navigator.createNavigator(GitHubRepositoryListKey)
        }
    }
}