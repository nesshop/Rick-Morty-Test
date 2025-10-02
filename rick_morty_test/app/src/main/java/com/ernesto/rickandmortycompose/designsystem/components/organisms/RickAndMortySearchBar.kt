package com.ernesto.rickandmortycompose.designsystem.components.organisms

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ernesto.rickandmortycompose.R
import com.ernesto.rickandmortycompose.designsystem.components.atoms.RickAndMortyText

@Composable
fun RickAndMortySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { RickAndMortyText(stringResource(R.string.rick_morty_search_bar_placeholder_text))},
        leadingIcon = {
            Icon(
                Icons.Default.Search, contentDescription = stringResource(R.string.rick_morty_search_bar_search_content_description_icon)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Clear, contentDescription = stringResource(R.string.rick_morty_search_bar_content_description_clear_search)
                    )
                }
            }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}