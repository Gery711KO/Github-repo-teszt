package com.gery711k.yettelteszt.ui.utils

import com.gery711k.yettelteszt.data.model.github.GitHubRepositoryListItemDto


fun GitHubRepositoryListItemDto.getSharedTransitionKeyForProperty(
    property: String
) = "$id**$property"