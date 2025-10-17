package com.ernesto.rickandmortycompose.designsystem.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ernesto.rickandmortycompose.designsystem.components.atoms.RickAndMortyText

@Composable
fun RickAndMortyError(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.Red),
        contentAlignment = Alignment.Center
    ) {
        RickAndMortyText(message, color = Color.Green, fontWeight = FontWeight.Bold)
    }
}