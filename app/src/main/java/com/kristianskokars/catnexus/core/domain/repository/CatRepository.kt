package com.kristianskokars.catnexus.core.domain.repository

import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.domain.model.ServerError
import com.kristianskokars.catnexus.lib.Result
import com.kristianskokars.catnexus.lib.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CatRepository {
    val cats: StateFlow<List<Cat>>
    val catCount: StateFlow<Int>
    fun getFavouritedCats(): Flow<List<Cat>>
    fun getPagedCat(id: String): Flow<Cat>
    fun getSavedCat(id: String): Flow<Cat>
    fun saveCatImage(cat: Cat)
    suspend fun toggleFavouriteForCat(id: String)
    fun isCatDownloading(catId: String): Flow<Boolean>
    suspend fun refreshCats(shouldClearPrevious: Boolean = false): Result<ServerError, Success>
}
