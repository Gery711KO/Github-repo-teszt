package com.gery711k.yettelteszt.ui.di

import com.gery711k.yettelteszt.ui.navigation.Navigator
import com.gery711k.yettelteszt.ui.navigation.destination.GitHubRepositoryDetailScreenDestination
import com.gery711k.yettelteszt.ui.navigation.destination.GitHubRepositoryListScreenDestination
import com.gery711k.yettelteszt.ui.screen.githubrepositorydetail.GitHubRepositoryDetailScreen
import com.gery711k.yettelteszt.ui.screen.githubrepositorylist.GitHubRepositoryListScreen
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {
    activityRetainedScope {
        scoped {
            Navigator.createNavigator(GitHubRepositoryListScreenDestination)
        }

        navigation<GitHubRepositoryListScreenDestination> {
            GitHubRepositoryListScreen(navigator = get())
        }

        navigation<GitHubRepositoryDetailScreenDestination> { screen ->
            GitHubRepositoryDetailScreen(
                repositoryId = screen.repositoryId,
                navigator = get(),
            )
        }
    }
}