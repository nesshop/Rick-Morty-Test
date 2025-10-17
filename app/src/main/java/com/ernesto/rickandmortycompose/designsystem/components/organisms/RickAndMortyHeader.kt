package com.ernesto.rickandmortycompose.designsystem.components.organisms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import com.ernesto.rickandmortycompose.R
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel

@Composable
fun RickAndMortyHeader(characterModel: CharacterModel, modifier: Modifier) {
    Box(modifier = modifier) {
        AsyncImage(
            model = characterModel.image,
            contentDescription = stringResource(R.string.rick_morty_header_character_content_description),
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxHeight(0.5f)
        )
    }
}