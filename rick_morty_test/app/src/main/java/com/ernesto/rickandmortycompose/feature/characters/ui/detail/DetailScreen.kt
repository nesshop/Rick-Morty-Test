package com.ernesto.rickandmortycompose.feature.characters.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ernesto.rickandmortycompose.designsystem.components.molecules.RickAndMortyCard
import com.ernesto.rickandmortycompose.designsystem.components.molecules.RickAndMortyError
import com.ernesto.rickandmortycompose.designsystem.components.molecules.RickAndMortyLoading
import com.ernesto.rickandmortycompose.designsystem.components.organisms.RickAndMortyHeader


@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel = hiltViewModel(),
    characterId: Int
) {

    val uiState by detailViewModel.uiState.collectAsState()

    LaunchedEffect(characterId) {
        detailViewModel.loadCharacter(characterId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (uiState) {
            is DetailUiState.Loading -> {
                RickAndMortyLoading()
            }

            is DetailUiState.Success -> {
                val character = (uiState as DetailUiState.Success).character
                Column(modifier = Modifier.fillMaxSize()) {
                    RickAndMortyHeader(character)
                    RickAndMortyCard(character, modifier = Modifier.fillMaxWidth().weight(1f))
                }
            }

            is DetailUiState.Error -> {
                val message = (uiState as DetailUiState.Error).message
                RickAndMortyError(message)
            }
        }
    }
}

@Preview
@Composable
fun ShowDetailScreen() {
    DetailScreen(
        characterId = 1)
}