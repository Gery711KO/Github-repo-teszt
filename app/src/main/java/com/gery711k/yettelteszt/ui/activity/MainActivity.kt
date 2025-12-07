package com.gery711k.yettelteszt.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.gery711k.yettelteszt.ui.navigation.Navigator
import com.gery711k.yettelteszt.ui.navigation.destination.GitHubRepositoryListKey
import com.gery711k.yettelteszt.ui.navigation.destination.GitHubRepositoryDetailKey
import com.gery711k.yettelteszt.ui.screen.dashboard.DashboardScreen
import com.gery711k.yettelteszt.ui.screen.repositorydetail.RepositoryDetailScreen
import com.gery711k.yettelteszt.ui.theme.MyApplicationTheme
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.scope.Scope

class MainActivity : ComponentActivity(), AndroidScopeComponent {
    override val scope: Scope by activityRetainedScope()
    val navigator: Navigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NavigationComposable(navigator)
            }
        }
    }
}

@Composable
private fun NavigationComposable(navigator: Navigator) {
    SharedTransitionLayout {
        NavDisplay(
            sharedTransitionScope = this,
            backStack = navigator.backStack,
            onBack = { navigator.navigateBack() },
            entryProvider = entryProvider {
                entry<GitHubRepositoryListKey> {
                    DashboardScreen(
                        navigator = navigator,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }
                entry<GitHubRepositoryDetailKey> { screen ->
                    RepositoryDetailScreen(
                        navigator = navigator,
                        repositoryId = screen.repositoryId,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        )
    }
}