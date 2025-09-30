package com.ernesto.rickandmortycompose.feature.characters.ui.characterlist

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.ernesto.rickandmortycompose.designsystem.theme.LightGray
import com.ernesto.rickandmortycompose.feature.characters.Constants.ALIVE
import com.ernesto.rickandmortycompose.feature.characters.Constants.DEAD
import com.ernesto.rickandmortycompose.feature.characters.Constants.UNKNOWN
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel

@Composable
fun CharactersListScreen(
    viewModel: CharactersListViewModel = hiltViewModel(),
    navigateToDetail: (CharacterModel) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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
                    val message = refreshLoadState.error.message ?: "Error loading characters list"
                    RickAndMortyError(message)
                }

                else -> {
                    CharactersGridList(characters = characters, navigateToDetail = navigateToDetail)
                }
            }
        }

        is CharactersUiState.Error -> {
            val message = (uiState as CharactersUiState.Error).message
            RickAndMortyError(message)
        }
    }
}

@Composable
fun CharactersGridList(
    characters: LazyPagingItems<CharacterModel>,
    navigateToDetail: (CharacterModel) -> Unit
) {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 0.2.dp,
        color = MaterialTheme.colorScheme.primary
    )
    LazyVerticalGrid(
        contentPadding = PaddingValues(16.dp),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(characters.itemCount) { index ->
            characters[index]?.let {
                CharacterItem(
                    it,
                    onItemSelected = { characterModel -> navigateToDetail(characterModel) })
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

