package com.gery711k.yettelteszt.data.api.github

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit

class GitHubApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: GitHubApiService

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        val contentType = "application/json".toMediaType()
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(GitHubApiService::class.java)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `fetchGithubRepositories returns data correctly`() = runTest {
        // given
        val responseBody = """
            {
              "total_count": 1,
              "items": [
                {
                  "id": 1,
                  "name": "test-repo",
                  "description": "A test repository",
                  "html_url": "https://github.com/user/test-repo",
                  "stargazers_count": 100,
                  "forks_count": 50,
                  "created_at": "2023-10-27T12:00:00Z",
                  "updated_at": "2023-10-27T13:00:00Z",
                  "owner": {
                    "id": 123,
                    "login": "user",
                    "avatar_url": "https://example.com/avatar.png",
                    "url": "https://api.github.com/users/user"
                  }
                }
              ]
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(responseBody).setResponseCode(200))

        // when
        val result = apiService.fetchGithubRepositories("test", 1)

        // then
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/search/repositories?q=test&page=1", recordedRequest.path)
        assertEquals(1L, result.total)
        assertEquals(1, result.items.size)
        assertEquals("test-repo", result.items[0].name)
        assertEquals("user", result.items[0].owner.name)
        assertEquals(100, result.items[0].stars)
        assertEquals(50, result.items[0].forksCount)
    }
}
