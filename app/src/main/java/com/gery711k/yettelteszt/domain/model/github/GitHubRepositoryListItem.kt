package com.gery711k.yettelteszt.domain.model.github

import androidx.compose.runtime.Immutable
import java.time.LocalDateTime

@Immutable
data class GitHubRepositoryListItem(
    val id: Long,
    val name: String,
    val description: String?,
    val repositoryLink: String,
    val stars: Int,
    val forksCount: Int,
    val createdAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
    val owner: GitHubRepositoryOwner
)

@Immutable
data class GitHubRepositoryOwner(
    val name: String,
    val avatarUrl: String,
    val url: String,
)
