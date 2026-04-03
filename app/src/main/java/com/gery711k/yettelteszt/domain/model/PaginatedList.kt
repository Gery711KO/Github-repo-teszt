package com.gery711k.yettelteszt.domain.model

import kotlinx.collections.immutable.ImmutableList

data class PaginatedList<T: Any>(
    val list: ImmutableList<T>,
    val canLoadMore: Boolean,
)