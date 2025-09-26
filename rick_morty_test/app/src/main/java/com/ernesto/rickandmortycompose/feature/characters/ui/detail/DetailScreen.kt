package com.ernesto.rickandmortycompose.feature.characters.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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

@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel = hiltViewModel(),
    characterId: Int,
    onBackPressed: () -> Unit
) {

    val scrollState = rememberScrollState()
    val character by detailViewModel.character.collectAsState()

    LaunchedEffect(characterId) {
        detailViewModel.loadCharacter(characterId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {

    }
}

@Preview
@Composable
fun ShowDetailScreen() {
    DetailScreen(
        characterId = 1,
        onBackPressed = {})

}