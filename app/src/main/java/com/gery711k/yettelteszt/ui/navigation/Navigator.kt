package com.gery711k.yettelteszt.ui.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey

private val LocalSharedTransitionScope =
    compositionLocalWithComputedDefaultOf<SharedTransitionScope> {
        error("No shared transition scope provided")
    }

interface Navigator {
    val backStack: List<NavKey>

    fun navigateTo(navigationKey: NavKey)
    fun navigateBack(): Boolean

    companion object {
        fun createNavigator(startDestination: NavKey): Navigator =
            DefaultNavigator(startDestination = startDestination)
    }
}

@Immutable
private data class DefaultNavigator(val startDestination: NavKey) : Navigator {
    override val backStack = mutableStateListOf<NavKey>(startDestination)

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
fun SharedTransitionScope.provideSharedTransitionScope(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalSharedTransitionScope provides this,
        content = content
    )
}

@Composable
fun getSharedTransitionScope() = LocalSharedTransitionScope.current