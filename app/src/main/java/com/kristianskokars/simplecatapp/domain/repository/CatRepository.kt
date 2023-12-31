package com.kristianskokars.simplecatapp.domain.repository

import com.kristianskokars.simplecatapp.core.Success
import com.kristianskokars.simplecatapp.core.Result
import com.kristianskokars.simplecatapp.domain.model.Cat
import com.kristianskokars.simplecatapp.domain.model.ServerError
import kotlinx.coroutines.flow.Flow

interface CatRepository {
    val cats: Flow<List<Cat>>
    fun getCat(id: String): Flow<Cat>
    fun saveCatImage(cat: Cat)
    suspend fun refreshCats(clearPrevious: Boolean = false): Result<ServerError, Success>
}
