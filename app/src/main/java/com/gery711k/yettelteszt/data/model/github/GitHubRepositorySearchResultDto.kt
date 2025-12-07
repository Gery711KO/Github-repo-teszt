package com.gery711k.yettelteszt.data.model.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRepositorySearchResultDto(
    @SerialName("total_count") val total: Long,
    val items: List<GitHubRepositoryListItemDto>
)