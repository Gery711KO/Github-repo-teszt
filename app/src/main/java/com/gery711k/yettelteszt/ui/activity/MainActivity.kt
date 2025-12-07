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
import androidx.navigation3.ui.NavDisplay
import com.gery711k.yettelteszt.ui.navigation.Navigator
import com.gery711k.yettelteszt.ui.navigation.provideSharedTransitionScope
import com.gery711k.yettelteszt.ui.theme.MyApplicationTheme
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.compose.navigation3.entryProvider
import org.koin.androidx.scope.activityRetainedScope
import org.koin.compose.navigation3.EntryProvider
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.scope.Scope

@OptIn(KoinExperimentalAPI::class)
class MainActivity : ComponentActivity(), AndroidScopeComponent {
    override val scope: Scope by activityRetainedScope()
    val navigator: Navigator by inject()
    val entryProvider by entryProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NavigationComposable(navigator, entryProvider)
            }
        }
    }
}

@OptIn(KoinExperimentalAPI::class)
@Composable
private fun NavigationComposable(
    navigator: Navigator,
    entryProvider: EntryProvider
) {
    SharedTransitionLayout {
        provideSharedTransitionScope {
            NavDisplay(
                sharedTransitionScope = this,
                backStack = navigator.backStack,
                onBack = { navigator.navigateBack() },
                entryProvider = entryProvider,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            )
        }
    }
}