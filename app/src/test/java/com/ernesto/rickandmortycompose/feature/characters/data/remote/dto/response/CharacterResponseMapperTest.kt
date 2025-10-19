package com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class CharacterResponseMapperTest {
    @Test
    fun `GIVEN CharacterResponse WHEN toDomain THEN all fields are mapped correctly`() = runTest {
        //GIVEN
        val response = CharacterResponse(
            id = 1,
            name = "Rick",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            image = "url",
            episode = emptyList()
        )

        //WHEN
        val domainModel = response.toDomain()

        //THEN
        assertEquals(response.id, domainModel.id)
        assertEquals(response.name, domainModel.name)
        assertEquals(response.status, domainModel.status)
        assertEquals(response.species, domainModel.species)
        assertEquals(response.type, domainModel.type)
        assertEquals(response.gender, domainModel.gender)
        assertEquals(response.image, domainModel.image)
        assertEquals(response.episode, domainModel.episodes)

    }
}