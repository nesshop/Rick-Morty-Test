package com.ernesto.rickandmortycompose.designsystem.components.molecules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ernesto.rickandmortycompose.R
import com.ernesto.rickandmortycompose.designsystem.components.atoms.RickAndMortyText
import com.ernesto.rickandmortycompose.designsystem.theme.MediumGreen
import com.ernesto.rickandmortycompose.feature.episodes.domain.model.EpisodeModel

@Composable
fun RickAndMortyEpisodeList(episodes: List<EpisodeModel>?, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        RickAndMortyText(
            text = stringResource(R.string.episode_list_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 240.dp)
        ) {
            items(episodes ?: emptyList()) { episode ->
                Column {
                    RickAndMortyText(
                        text = episode.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MediumGreen,
                        fontWeight = FontWeight.Bold
                    )
                    RickAndMortyText(
                        text = episode.episode,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
