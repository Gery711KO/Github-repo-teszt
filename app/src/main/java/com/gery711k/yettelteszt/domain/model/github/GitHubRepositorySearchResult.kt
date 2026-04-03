package com.gery711k.yettelteszt.domain.model.github

import kotlinx.collections.immutable.ImmutableList

data class GitHubRepositorySearchResult(
    val total: Long,
    val items: ImmutableList<GitHubRepositoryListItem>
)