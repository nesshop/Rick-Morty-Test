package com.ernesto.rickandmortycompose.feature.testutils

import androidx.recyclerview.widget.DiffUtil
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel

class CharacterModelDiffCallback : DiffUtil.ItemCallback<CharacterModel>() {
    override fun areItemsTheSame(
        oldItem: CharacterModel,
        newItem: CharacterModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: CharacterModel,
        newItem: CharacterModel
    ): Boolean {
        return oldItem == newItem
    }
}