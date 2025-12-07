package com.gery711k.yettelteszt.data.api.github

import com.gery711k.yettelteszt.data.model.github.GitHubRepositorySearchResultDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApiService {

    @GET("search/repositories")
    suspend fun fetchGithubRepositories(
        @Query("q") query: String,
        @Query("page") page: Int,
    ): GitHubRepositorySearchResultDto
}