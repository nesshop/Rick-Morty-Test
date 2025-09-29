package com.ernesto.rickandmortycompose.designsystem.components.organisms

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ernesto.rickandmortycompose.R
import com.ernesto.rickandmortycompose.designsystem.components.atoms.RickAndMortyText
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel

@Composable
fun RickAndMortyHeader(characterModel: CharacterModel) {
    Box(modifier = Modifier.height(300.dp)) {
        Image(
            painter = painterResource(R.drawable.loader),
            contentDescription = "Background header",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(topStartPercent = 10, topEndPercent = 10))
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                RickAndMortyText(characterModel.name, color = Color.Green)
                RickAndMortyText(characterModel.species)
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(contentAlignment = Alignment.TopCenter) {
                    Box(
                        modifier = Modifier
                            .size(204.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = characterModel.image,
                            contentDescription = "Character image",
                            modifier = Modifier
                                .size(190.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}