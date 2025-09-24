package com.ernesto.rickandmortycompose.feature.characters.ui.characterlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.ernesto.rickandmortycompose.R
import com.ernesto.rickandmortycompose.designsystem.components.atoms.RickAndMortyText
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
@Composable
fun CharactersListScreen(viewModel: CharactersListViewModel = viewModel()) {
    val characters = viewModel.characters.collectAsLazyPagingItems()
    CharactersGridList(characters = characters)
}

@Composable
fun CharactersGridList(characters: LazyPagingItems<CharacterModel>) {

    RickAndMortyText(
        stringResource(R.string.character_list_screen_title),
    )
    LazyVerticalGrid(contentPadding = PaddingValues(16.dp),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(characters.itemCount) { index ->
            characters[index]?.let {
                CharacterItem(it)
            }
        }

    }
}

@Composable
fun CharacterItem(characterModel: CharacterModel) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(24))
            .border(2.dp, Color.Green, shape = RoundedCornerShape(0, 24, 0, 24)).fillMaxWidth()
            .height(150.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        AsyncImage(
            model = ImageRequest.Builder(
                LocalContext.current)
                .data(characterModel.image)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier.fillMaxWidth().height(60.dp).background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(0f),
                        Color.Black.copy(0.6f),
                        Color.Black.copy(1f)
                    )
                )
            ), contentAlignment = Alignment.Center
        ) {
            Text(characterModel.name, color = Color.White, fontSize = 18.sp)
        }
    }
}
