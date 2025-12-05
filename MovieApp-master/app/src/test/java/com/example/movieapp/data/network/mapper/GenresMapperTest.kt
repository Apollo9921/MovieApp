package com.example.movieapp.data.network.mapper

import com.example.movieapp.data.network.dto.genres.GenreDTO
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GenresMapperTest {

    @Test
    fun `toGenre should map correctly when all fields are present`() = runTest {
        // --- ARRANGE ---
        val dto = GenreDTO(
            id = 1,
            name = "Action"
        )

        // --- ACT ---
        val domain = dto.toGenre()

        // --- ASSERT ---
        assertEquals(1, domain.id)
        assertEquals("Action", domain.name)
    }

}