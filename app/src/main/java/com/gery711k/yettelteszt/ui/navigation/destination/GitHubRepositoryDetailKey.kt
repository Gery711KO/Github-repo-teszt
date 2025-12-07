package com.gery711k.yettelteszt.ui.navigation.destination

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRepositoryDetailKey(val repositoryId: Long) : NavKey