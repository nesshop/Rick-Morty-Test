package com.ernesto.rickandmortycompose.designsystem.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ernesto.rickandmortycompose.designsystem.components.atoms.RickAndMortyText
import com.ernesto.rickandmortycompose.feature.characters.Constants.ALIVE
import com.ernesto.rickandmortycompose.feature.characters.Constants.DEAD
import com.ernesto.rickandmortycompose.feature.characters.Constants.UNKNOWN

@Composable
fun CharacterStatusChip(status: String, modifier: Modifier = Modifier) {
    val backgroundColor = when {
        status.equals(ALIVE, ignoreCase = true) -> Color.Green
        status.equals(DEAD, ignoreCase = true) -> Color.Red
        status.equals(UNKNOWN, ignoreCase = true) -> Color.Yellow
        else -> Color.Gray
    }

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = backgroundColor.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(backgroundColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            RickAndMortyText(
                text = status
            )
        }
    }
}