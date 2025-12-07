package com.gery711k.yettelteszt.ui.screen.githubrepositorylist

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.gery711k.yettelteszt.data.model.github.GitHubRepositoryListItemDto
import com.gery711k.yettelteszt.domain.model.PaginatedList
import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.ui.navigation.Navigator
import com.gery711k.yettelteszt.ui.navigation.destination.GitHubRepositoryDetailKey
import com.gery711k.yettelteszt.ui.utils.getSharedTransitionKeyForProperty
import com.gery711k.yettelteszt.ui.utils.rotatingBorderAnimation
import com.gery711k.yettelteszt.ui.utils.toReadableString
import kotlinx.collections.immutable.ImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
fun GitHubRepositoryListScreen(
    sharedTransitionScope: SharedTransitionScope,
    navigator: Navigator,
    viewModel: GitHubRepositoryListScreenViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    with(sharedTransitionScope) {
        DashboardScreenContent(
            uiState = uiState,
            onItemClicked = {
                navigator.navigateTo(GitHubRepositoryDetailKey(it.id))
            },
            onSearch = {
                viewModel.searchRepositories(it)
            },
            onLoadMore = {
                viewModel.loadMore()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SharedTransitionScope.DashboardScreenContent(
    uiState: DashboardScreenUiState?,
    onItemClicked: (GitHubRepositoryListItemDto) -> Unit,
    onLoadMore: () -> Unit,
    onSearch: (query: String) -> Unit,
) {
    val textFieldState = rememberTextFieldState()
    var isExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SearchBar(
                inputField = {
                    SearchBarInput(
                        textFieldState = textFieldState,
                        isExpanded = isExpanded,
                        onToggleExpand = { isExpanded = it },
                        onSearch = onSearch,
                        modifier = Modifier.rotatingBorderAnimation(
                            isLoading = uiState?.listItems is Result.Loading,
                            shape = SearchBarDefaults.inputFieldShape
                        )
                    )
                },
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it },
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.8f to MaterialTheme.colorScheme.background,
                                1f to Color.Transparent
                            )
                        )
                    )
                    .padding(
                        horizontal = animateDpAsState(
                            targetValue = if (!isExpanded) 16.dp
                            else 0.dp
                        ).value
                    )
            ) {
                Box(modifier = Modifier.weight(2f)) {
                    SearchHistoryItems(
                        searchHistoryItems = uiState?.searchHistory,
                        onSearch = {
                            textFieldState.setTextAndPlaceCursorAtEnd(it)
                            onSearch(it)
                        },
                        onExpand = { isExpanded = it }
                    )
                    SearchButton(
                        textFieldState = textFieldState,
                        onSearch = onSearch,
                        onToggleExpand = { isExpanded = it }
                    )
                }
            }
        }
    ) { padding ->
        val listItemsResult = uiState?.listItems

        ErrorToast(listItemsResult)

        ListContent(
            contentPadding = padding,
            listItems = listItemsResult?.data?.list,
            onItemClicked = onItemClicked,
            listState = listState,
            emptyContentSlot = {
                if (listItemsResult is Result.Success || listItemsResult == null) item {
                    EmptyContent(listItemsResult?.data == null)
                }
            },
            errorContentSlot = {
                (listItemsResult as? Result.Error)?.let { errorResult ->
                    item {
                        ErrorContent(modifier = Modifier.animateItem())
                    }
                }
            },
            loadingContentSlot = {
                if (listItemsResult is Result.Loading) item {
                    LoadingContent(
                        modifier = Modifier
                            .padding(vertical = 48.dp)
                            .animateItem()
                    )
                }
            },
            loadMoreButtonSlot = {
                if (listItemsResult?.data?.canLoadMore == true) {
                    item {
                        LoadMoreButton(listItemsResult, onLoadMore)
                    }
                }
            }
        )
    }
}

@Composable
private fun ErrorToast(
    listItemsResult: Result<PaginatedList<GitHubRepositoryListItemDto>>?,
    context: Context = LocalContext.current
) {
    DisposableEffect(listItemsResult) {
        val toast = if (
            listItemsResult is Result.Error && listItemsResult.data?.list.isNullOrEmpty()
        ) {
            Toast.makeText(
                context,
                "Valami hiba történt, kérlek próbáld újra.",
                Toast.LENGTH_SHORT
            )
        } else {
            null
        }

        toast?.show()

        onDispose {
            toast?.cancel()
        }
    }
}

@Composable
private fun LoadMoreButton(
    listItemState: Result<PaginatedList<GitHubRepositoryListItemDto>>,
    onLoadMore: () -> Unit,
) {
    TextButton(
        onClick = onLoadMore,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Továbbiak betöltése")

            CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
                    .alpha(
                        if (listItemState is Result.Loading<*>) 1f
                        else 0f
                    )
            )
        }
    }
}

@Composable
private fun BoxScope.SearchButton(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    onToggleExpand: (Boolean) -> Unit,
) {
    Button(
        onClick = {
            onSearch(textFieldState.text.toString())
            onToggleExpand(false)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .align(Alignment.BottomCenter)
            .imePadding(),
    ) {
        Text(text = "Keresés")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarInput(
    textFieldState: TextFieldState,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    onToggleExpand: (Boolean) -> Unit,
    onSearch: (query: String) -> Unit,
) {
    val focusRequest = remember { FocusRequester() }

    SearchBarDefaults.InputField(
        query = textFieldState.text.toString(),
        onQueryChange = { textFieldState.setTextAndPlaceCursorAtEnd(it) },
        onSearch = { query ->
            onSearch(query)
            onToggleExpand(false)
        },
        expanded = isExpanded,
        onExpandedChange = { onToggleExpand(it) },
        placeholder = { Text("Kezdj el gépelni...") },
        trailingIcon = {
            Crossfade(targetState = textFieldState.text.isNotEmpty()) { hasText ->
                if (hasText) {
                    IconButton(onClick = { textFieldState.clearText() }) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null
                        )
                    }
                } else {
                    Box(Modifier)
                }
            }
        },
        leadingIcon = {
            Crossfade(
                targetState = isExpanded
            ) { expanded ->
                if (expanded) {
                    IconButton(onClick = { onToggleExpand(false) }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = null
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            onToggleExpand(true)
                            focusRequest.requestFocus()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null
                        )
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequest)
    )
}

@Composable
private fun SearchHistoryItems(
    searchHistoryItems: ImmutableList<String>?,
    onExpand: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
) {
    Column {
        searchHistoryItems?.forEachIndexed { index, searchText ->
            Text(
                text = searchText,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = {
                            onSearch(searchText)
                            onExpand(false)
                        }
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            if (index != searchHistoryItems.lastIndex) HorizontalDivider()
        }
    }
}

@Composable
private fun SharedTransitionScope.ListContent(
    listItems: ImmutableList<GitHubRepositoryListItemDto>?,
    listState: LazyListState,
    contentPadding: PaddingValues,
    emptyContentSlot: LazyListScope.() -> Unit,
    errorContentSlot: LazyListScope.() -> Unit,
    loadingContentSlot: LazyListScope.() -> Unit,
    loadMoreButtonSlot: LazyListScope.() -> Unit,
    onItemClicked: (GitHubRepositoryListItemDto) -> Unit,
) {
    LazyColumn(
        contentPadding = contentPadding,
        state = listState,
    ) {
        if (listItems.isNullOrEmpty()) {
            emptyContentSlot()
            errorContentSlot()
            loadingContentSlot()
        } else {
            items(
                items = listItems,
                key = { it.id }
            ) { item ->
                GitHubRepositoryListItem(
                    item = item,
                    modifier = Modifier
                        .animateItem()
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                key = item.getSharedTransitionKeyForProperty(item::class.java.simpleName),
                            ),
                            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(),
                            onClick = {
                                onItemClicked(item)
                            }
                        )
                )
            }

            loadMoreButtonSlot()
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent(
    hasNoResultYet: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(32.dp)) {
        Text(
            text = if (hasNoResultYet) {
                "Kezdj el gépelni a kereső mezőbe, GitHub repository-k kereséséhez."
            } else {
                "Nincs találat a megadott keresésre."
            },
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = if (hasNoResultYet) {
                "A keresés után a találatok itt fognak megjelenni."
            } else {
                "Próbáld meg újra egy másik kulcsszóval."
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ErrorContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(32.dp)) {
        Text(
            text = "Valami hiba történt",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Kérlek próbáld újra.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun GitHubRepositoryListItem(
    item: GitHubRepositoryListItemDto,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background,
            headlineColor = MaterialTheme.colorScheme.onPrimaryContainer,
            supportingColor = MaterialTheme.colorScheme.secondary
        ),
        headlineContent = {
            Text(
                text = item.name,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        supportingContent = item.description?.let {
            {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 4,
                )
            }
        },
        overlineContent = {
            Text(
                text = item.lastUpdatedAt.toReadableString(),
                style = MaterialTheme.typography.bodySmall
            )
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = item.stars.toString())

                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                )
            }
        }
    )
}
