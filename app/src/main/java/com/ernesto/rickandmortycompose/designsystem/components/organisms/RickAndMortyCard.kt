package com.ernesto.rickandmortycompose.designsystem.components.organisms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ernesto.rickandmortycompose.R
import com.ernesto.rickandmortycompose.designsystem.components.atoms.RickAndMortyText
import com.ernesto.rickandmortycompose.designsystem.components.molecules.CharacterStatusChip
import com.ernesto.rickandmortycompose.designsystem.components.molecules.RickAndMortyButton
import com.ernesto.rickandmortycompose.designsystem.components.molecules.RickAndMortyEpisodeList
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.episodes.domain.model.EpisodeModel

@Composable
fun RickAndMortyCard(
    characterModel: CharacterModel,
    episodes: List<EpisodeModel>,
    modifier: Modifier,
    onLoadEpisodes: (List<String>) -> Unit
) {

    var showEpisodes by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        shape = RoundedCornerShape(
            topStart = 36.dp,
            topEnd = 36.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                RickAndMortyText(
                    text = characterModel.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CharacterStatusChip(status = characterModel.status)
                Spacer(modifier = Modifier.width(16.dp))
                RickAndMortyText(text = characterModel.species)
            }

            Spacer(modifier = Modifier.height(16.dp))
            RickAndMortyText(text = stringResource(R.string.character_detail_gender_title))
            Spacer(modifier = Modifier.height(8.dp))
            RickAndMortyText(
                text = characterModel.gender,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            RickAndMortyText(text = stringResource(R.string.character_detail_screen_about_title))
            Spacer(modifier = Modifier.height(8.dp))
            RickAndMortyText(
                text = stringResource(R.string.character_detail_screen_about_description),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            RickAndMortyButton(
                modifier = Modifier.fillMaxWidth(),
                text = if (showEpisodes) "Ocultar Episodios"
                else stringResource(R.string.character_detail_screen_text_button),
                icon = Icons.Default.Tv,
                onClick = { showEpisodes = !showEpisodes
                    if (showEpisodes && episodes.isEmpty()) {
                        onLoadEpisodes(characterModel.episodes)
                    }
                }
            )
            AnimatedVisibility(visible = showEpisodes) {
                if (episodes.isEmpty()) {
                    RickAndMortyText(
                        text = "No episodes found"
                    )
                } else {
                    RickAndMortyEpisodeList(
                        episodes = episodes,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}