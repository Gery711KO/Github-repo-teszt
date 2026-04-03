package com.gery711k.yettelteszt.ui.utils

import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryListItem


fun GitHubRepositoryListItem.getSharedTransitionKeyForProperty(
    property: String
) = "$id**$property"