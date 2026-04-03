package com.gery711k.yettelteszt.ui.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.navigation3.EntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

private val LocalSharedTransitionScope =
    compositionLocalWithComputedDefaultOf<SharedTransitionScope> {
        error("No shared transition scope provided")
    }

interface Navigator {
    val backStack: List<NavKey>

    @Composable
    fun getSharedTransitionScope(): SharedTransitionScope

    fun navigateTo(navigationKey: NavKey)
    fun navigateBack(): Boolean

    companion object {
        fun createNavigator(startDestination: NavKey): Navigator =
            DefaultNavigator(startDestination = startDestination)

        @OptIn(KoinExperimentalAPI::class)
        @Composable
        fun createNavDisplay(
            navigator: Navigator,
            entryProvider: EntryProvider<NavKey>,
            modifier: Modifier = Modifier,
        ) {
            SharedTransitionLayout {
                provideSharedTransitionScope {
                    NavDisplay(
                        sharedTransitionScope = this,
                        backStack = navigator.backStack,
                        onBack = { navigator.navigateBack() },
                        entryProvider = entryProvider,
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@Immutable
private data class DefaultNavigator(
    val startDestination: NavKey,
) : Navigator {
    override val backStack = mutableStateListOf<NavKey>(startDestination)

    @Composable
    override fun getSharedTransitionScope() = LocalSharedTransitionScope.current

    override fun navigateTo(navigationKey: NavKey) {
        backStack.add(navigationKey)
    }

    override fun navigateBack(): Boolean {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
            return true
        } else {
            return false
        }
    }
}

@Composable
private fun SharedTransitionScope.provideSharedTransitionScope(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalSharedTransitionScope provides this,
        content = content
    )
}