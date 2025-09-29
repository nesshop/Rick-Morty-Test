package com.ernesto.rickandmortycompose.designsystem.components.organisms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel

@Composable
fun RickAndMortyHeader(characterModel: CharacterModel) {
    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = characterModel.image,
            contentDescription = "Character",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        )
    }
}