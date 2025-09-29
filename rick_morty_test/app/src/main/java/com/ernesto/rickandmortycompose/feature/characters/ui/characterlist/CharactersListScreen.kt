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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.ernesto.rickandmortycompose.designsystem.components.atoms.RickAndMortyText
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel

@Composable
fun CharactersListScreen(
    viewModel: CharactersListViewModel = hiltViewModel(),
    navigateToDetail: (CharacterModel) -> Unit
) {
    val characters = viewModel.characters.collectAsLazyPagingItems()
    CharactersGridList(characters = characters, navigateToDetail = navigateToDetail)
}

@Composable
fun CharactersGridList(
    characters: LazyPagingItems<CharacterModel>,
    navigateToDetail: (CharacterModel) -> Unit
) {
    HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 0.2.dp, color = MaterialTheme.colorScheme.primary)
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
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
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
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

