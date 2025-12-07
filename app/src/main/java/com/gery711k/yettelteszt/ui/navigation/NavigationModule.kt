package com.gery711k.yettelteszt.ui.navigation

import com.gery711k.yettelteszt.ui.navigation.destination.GitHubRepositoryDetailKey
import com.gery711k.yettelteszt.ui.navigation.destination.GitHubRepositoryListKey
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
            Navigator.createNavigator(GitHubRepositoryListKey)
        }

        navigation<GitHubRepositoryListKey> {
            GitHubRepositoryListScreen(
                sharedTransitionScope = getSharedTransitionScope(),
                navigator = get()
            )
        }

        navigation<GitHubRepositoryDetailKey> { screen ->
            GitHubRepositoryDetailScreen(
                sharedTransitionScope = getSharedTransitionScope(),
                repositoryId = screen.repositoryId,
                navigator = get(),
            )
        }
    }
}