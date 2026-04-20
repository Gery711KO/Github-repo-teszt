package com.gery711k.yettelteszt.ui.screen.githubrepositorydetail

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.ForkLeft
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryListItem
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryOwner
import com.gery711k.yettelteszt.ui.navigation.Navigator
import com.gery711k.yettelteszt.ui.theme.MyApplicationTheme
import com.gery711k.yettelteszt.ui.utils.getSharedTransitionKeyForProperty
import com.gery711k.yettelteszt.ui.utils.shimmerLoadingAnimation
import com.gery711k.yettelteszt.ui.utils.skipInPreview
import com.gery711k.yettelteszt.ui.utils.toReadableString
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.LocalDateTime

@Composable
fun GitHubRepositoryDetailScreen(
    navigator: Navigator,
    repositoryId: Long,
    viewModel: GitHubRepositoryDetailScreenViewModel = koinViewModel(key = repositoryId.toString()) {
        parametersOf(repositoryId)
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    with(navigator.getSharedTransitionScope()) {
        RepositoryDetailScreenContent(
            item = uiState,
            onNavigateBack = {
                navigator.navigateBack()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SharedTransitionScope.RepositoryDetailScreenContent(
    item: GitHubRepositoryListItem?,
    onNavigateBack: () -> Unit,
) {
    item?.let { item ->
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.skipInPreview {
                sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = item.getSharedTransitionKeyForProperty(item::class.java.simpleName),
                    ),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                )
            },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.headlineSmall,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBackIosNew,
                                contentDescription = "navigate back"
                            )
                        }
                    },
                )
            }
        ) { padding ->
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(16.dp)
            ) {
                OwnerDetails(
                    owner = item.owner,
                    modifier = Modifier.wrapContentHeight()
                )

                RepositoryDetails(item)
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun OwnerDetails(
    owner: GitHubRepositoryOwner,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GlideImage(
            modifier = modifier
                .padding(12.dp)
                .clip(CircleShape)
                .size(100.dp),
            model = owner.avatarUrl,
            loading = placeholder {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(100.dp)
                        .shimmerLoadingAnimation(),
                )
            },
            contentDescription = null,
        )

        Column(
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Text(
                text = owner.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = buildAnnotatedString {
                    append(owner.url)

                    addLink(
                        url = LinkAnnotation.Url(url = owner.url),
                        start = 0,
                        end = owner.url.length
                    )
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun RepositoryDetails(
    item: GitHubRepositoryListItem,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item.description?.let {
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        InfoRow(
            modifier = Modifier.fillMaxWidth(),
            infoIcon = Icons.Rounded.Link,
            infoText = buildAnnotatedString {
                append(item.repositoryLink)

                addLink(
                    url = LinkAnnotation.Url(item.repositoryLink),
                    start = 0,
                    end = item.repositoryLink.length
                )
            }
        )

        InfoRow(
            modifier = Modifier.fillMaxWidth(),
            infoIcon = Icons.Rounded.Create,
            infoText = buildAnnotatedString {
                append(item.createdAt.toReadableString())
            }
        )

        InfoRow(
            modifier = Modifier.fillMaxWidth(),
            infoIcon = Icons.Rounded.Update,
            infoText = buildAnnotatedString {
                append(item.lastUpdatedAt.toReadableString())
            }
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            InfoRow(
                modifier = Modifier.weight(1f),
                infoIcon = Icons.Rounded.Star,
                infoText = buildAnnotatedString {
                    append(item.stars.toString())
                }
            )

            Spacer(Modifier.width(8.dp))

            InfoRow(
                modifier = Modifier.weight(1f),
                infoIcon = Icons.Rounded.ForkLeft,
                infoText = buildAnnotatedString {
                    append(item.forksCount.toString())
                }
            )
        }
    }
}

@Composable
fun InfoRow(
    infoIcon: ImageVector,
    infoText: AnnotatedString,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(intrinsicSize = IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = infoIcon,
                contentDescription = null,
            )

            Text(text = infoText)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun GitHubRepositoryDetailScreenPreview() {
    MyApplicationTheme {
        SharedTransitionLayout {
            RepositoryDetailScreenContent(
                item = GitHubRepositoryListItem(
                    id = 1,
                    name = "Compose",
                    description = "Jetpack Compose repository description. It is a long description to see how it looks like in the UI.",
                    repositoryLink = "https://github.com/google/compose",
                    stars = 1000,
                    forksCount = 500,
                    createdAt = LocalDateTime.now(),
                    lastUpdatedAt = LocalDateTime.now(),
                    owner = GitHubRepositoryOwner(
                        name = "Google",
                        avatarUrl = "",
                        url = "https://github.com/google"
                    )
                ),
                onNavigateBack = {}
            )
        }
    }
}
