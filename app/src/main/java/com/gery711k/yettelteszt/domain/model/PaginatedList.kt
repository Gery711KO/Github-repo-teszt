package com.gery711k.yettelteszt.domain.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

data class PaginatedList<T: Any>(
    val list: ImmutableList<T>,
    val canLoadMore: Boolean,
)

fun <T: Any> emptyPaginatedList(canLoadMore: Boolean = false) = PaginatedList<T>(
    list = persistentListOf(),
    canLoadMore = canLoadMore
)

fun <T: Any> List<T>.toPaginatedList(canLoadMore: Boolean) = PaginatedList(
    list = this.toImmutableList(),
    canLoadMore = canLoadMore
)