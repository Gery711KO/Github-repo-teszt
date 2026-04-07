package com.gery711k.yettelteszt.data.mappers

import com.gery711k.yettelteszt.data.model.github.GitHubRepositoryListItemDto
import com.gery711k.yettelteszt.data.model.github.GitHubRepositoryOwnerDto
import com.gery711k.yettelteszt.data.model.github.GitHubRepositorySearchResultDto
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryListItem
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryOwner
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositorySearchResult
import kotlinx.collections.immutable.toImmutableList

fun GitHubRepositorySearchResultDto.toDomain() = GitHubRepositorySearchResult(
    total = total,
    items = items.toDomain().toImmutableList()
)

fun List<GitHubRepositoryListItemDto>.toDomain() = map { it.toDomain() }

fun GitHubRepositoryListItemDto.toDomain() = GitHubRepositoryListItem(
    id = id,
    name = name,
    description = description,
    repositoryLink = repositoryLink,
    stars = stars,
    forksCount = forksCount,
    createdAt = createdAt,
    lastUpdatedAt = lastUpdatedAt,
    owner = owner.toDomain()
)

private fun GitHubRepositoryOwnerDto.toDomain() = GitHubRepositoryOwner(
    name = name,
    avatarUrl = avatarUrl,
    url = url
)