package com.ernesto.rickandmortycompose.feature.characters.ui.characterlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.ernesto.rickandmortycompose.designsystem.components.atoms.RickAndMortyText
import com.ernesto.rickandmortycompose.designsystem.components.molecules.RickAndMortyError
import com.ernesto.rickandmortycompose.designsystem.components.molecules.RickAndMortyLoading
import com.ernesto.rickandmortycompose.designsystem.components.organisms.RickAndMortySearchBar
import com.ernesto.rickandmortycompose.designsystem.theme.LightGray
import com.ernesto.rickandmortycompose.feature.characters.Constants.ALIVE
import com.ernesto.rickandmortycompose.feature.characters.Constants.DEAD
import com.ernesto.rickandmortycompose.feature.characters.Constants.UNKNOWN
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel

@Composable
fun CharactersListScreen(
    viewModel: CharactersListViewModel = hiltViewModel(),
    navigateToDetail: (CharacterModel) -> Unit,
    onSetTopBarActions: ((@Composable RowScope.() -> Unit)?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSearchBar by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState()

    LaunchedEffect(Unit) {
        onSetTopBarActions {
            IconButton(onClick = {
                showSearchBar = !showSearchBar
                if (!showSearchBar) viewModel.clearSearchQuery()
            }) {
                Icon(
                    imageVector = if (showSearchBar) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = if (showSearchBar) "Close search" else "Search"
                )
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            onSetTopBarActions(null)
        }
    }

    Column {
        AnimatedVisibility(
            visible = showSearchBar,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            RickAndMortySearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        when (uiState) {
            is CharactersUiState.Loading -> {
                RickAndMortyLoading()
            }

            is CharactersUiState.Success -> {
                val characters =
                    (uiState as CharactersUiState.Success).characters.collectAsLazyPagingItems()

                when (val refreshLoadState = characters.loadState.refresh) {
                    is LoadState.Loading -> RickAndMortyLoading()
                    is LoadState.Error -> {
                        val message =
                            refreshLoadState.error.message ?: "Error loading characters list"
                        RickAndMortyError(message)
                    }

                    else -> {
                        CharactersGridList(
                            characters = characters,
                            searchQuery = searchQuery,
                            navigateToDetail = navigateToDetail
                        )
                    }
                }
            }

            is CharactersUiState.Error -> {
                val message = (uiState as CharactersUiState.Error).message
                RickAndMortyError(message)
            }
        }
    }
}

@Composable
fun CharactersGridList(
    characters: LazyPagingItems<CharacterModel>,
    searchQuery: String,
    navigateToDetail: (CharacterModel) -> Unit
) {
    val filteredList = remember(characters.itemSnapshotList, searchQuery) {
        if (searchQuery.isBlank()) {
            characters.itemSnapshotList.items
        } else {
            characters.itemSnapshotList.items.filter { character ->
                character.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    Column {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 0.2.dp,
            color = MaterialTheme.colorScheme.primary
        )
        if (filteredList.isEmpty() && searchQuery.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    RickAndMortyText("No results found", color = LightGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    RickAndMortyText("Try another search", color = LightGray)
                }
            }
        } else {
            LazyVerticalGrid(
                contentPadding = PaddingValues(16.dp),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(characters.itemCount) { index ->
                    characters[index]?.let { character ->
                        if (searchQuery.isBlank() || character.name.contains(
                                searchQuery,
                                ignoreCase = true
                            )
                        ) {
                            CharacterItem(
                                character,
                                onItemSelected = { characterModel -> navigateToDetail(characterModel) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterItem(
    characterModel: CharacterModel,
    onItemSelected: (CharacterModel) -> Unit
) {
    val borderColor = when {
        characterModel.status.equals(ALIVE, ignoreCase = true) -> MaterialTheme.colorScheme.primary
        characterModel.status.equals(DEAD, ignoreCase = true) -> Color.Red
        characterModel.status.equals(UNKNOWN, ignoreCase = true) -> Color.Yellow
        else -> Color.Gray
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable { onItemSelected(characterModel) }
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(characterModel.image)
                    .crossfade(true)
                    .build(),
                contentDescription = "Character",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        RickAndMortyText(
            text = characterModel.name,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        RickAndMortyText(
            text = characterModel.species,
            color = LightGray
        )
    }
}

