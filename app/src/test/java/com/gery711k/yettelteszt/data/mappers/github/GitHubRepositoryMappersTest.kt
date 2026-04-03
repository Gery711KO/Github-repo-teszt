package com.gery711k.yettelteszt.data.mappers.github

import com.gery711k.yettelteszt.data.mappers.toDomain
import com.gery711k.yettelteszt.data.model.github.GitHubRepositoryListItemDto
import com.gery711k.yettelteszt.data.model.github.GitHubRepositoryOwnerDto
import com.gery711k.yettelteszt.data.model.github.GitHubRepositorySearchResultDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class GitHubRepositoryMappersTest {

    @Test
    fun `GitHubRepositorySearchResultDto toDomain maps correctly`() {
        // given
        val now = LocalDateTime.now()
        val dto = GitHubRepositorySearchResultDto(
            total = 100,
            items = listOf(
                GitHubRepositoryListItemDto(
                    id = 1L,
                    name = "repo",
                    description = "desc",
                    repositoryLink = "link",
                    stars = 10,
                    forksCount = 5,
                    createdAt = now,
                    lastUpdatedAt = now,
                    owner = GitHubRepositoryOwnerDto(
                        id = 1L,
                        name = "user",
                        avatarUrl = "avatar",
                        url = "url",
                    )
                )
            )
        )

        // when
        val domain = dto.toDomain()

        // then
        assertEquals(dto.total, domain.total)
        assertEquals(dto.items.size, domain.items.size)
        assertEquals(dto.items[0].id, domain.items[0].id)
        assertEquals(dto.items[0].name, domain.items[0].name)
        assertEquals(dto.items[0].owner.name, domain.items[0].owner.name)
        assertEquals(dto.items[0].owner.avatarUrl, domain.items[0].owner.avatarUrl)
    }
}