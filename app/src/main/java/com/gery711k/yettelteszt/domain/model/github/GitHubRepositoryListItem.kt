package com.gery711k.yettelteszt.domain.model.github

import java.time.LocalDateTime

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

data class GitHubRepositoryOwner(
    val name: String,
    val avatarUrl: String,
    val url: String,
)
