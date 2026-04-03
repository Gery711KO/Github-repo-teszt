package com.gery711k.yettelteszt.data.model.github

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Serializable
data class GitHubRepositoryListItemDto(
    val id: Long,
    val name: String,
    val description: String?,
    @SerialName("html_url")
    val repositoryLink: String,
    @SerialName("stargazers_count")
    val stars: Int,
    @SerialName("forks_count")
    val forksCount: Int,
    @SerialName("created_at")
    @Serializable(with = LocalDateTimeUtcSerializer::class)
    val createdAt: LocalDateTime,
    @SerialName("updated_at")
    @Serializable(with = LocalDateTimeUtcSerializer::class)
    val lastUpdatedAt: LocalDateTime,
    val owner: GitHubRepositoryOwnerDto
)

@Serializable
data class GitHubRepositoryOwnerDto(
    val id: Long,
    @SerialName("login") val name: String,
    @SerialName("avatar_url") val avatarUrl: String,
    val url: String,
)

private object LocalDateTimeUtcSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val instant = value.toInstant(ZoneOffset.UTC)
        encoder.encodeString(instant.toString()) // outputs ISO-8601 with Z
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val string = decoder.decodeString()
        val instant = Instant.parse(string)
        return instant.atOffset(ZoneOffset.UTC).toLocalDateTime()
    }
}
