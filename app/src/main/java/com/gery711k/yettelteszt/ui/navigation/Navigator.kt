package com.gery711k.yettelteszt.ui.navigation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey

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
private data class DefaultNavigator(val startDestination: NavKey): Navigator {
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