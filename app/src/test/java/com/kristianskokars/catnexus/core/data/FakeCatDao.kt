package com.kristianskokars.catnexus.core.data

import com.kristianskokars.catnexus.core.data.data_source.local.CatDao
import com.kristianskokars.catnexus.core.domain.model.Cat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeCatDao : CatDao {
    private val storedCats = MutableStateFlow<List<Cat>>(emptyList())

    override fun getCats() = storedCats
    override fun getFavouritedCats(): Flow<List<Cat>> = storedCats.map { cats ->
        cats.filter { it.isFavourited }
    }

    override fun getCat(catId: String): Flow<Cat> = storedCats.map { cats ->
        cats.find { it.id == catId } ?: throw RuntimeException("Cat not found")
    }

    override suspend fun addCats(cats: List<Cat>) {
        val catIds = cats.map { it.id }
        storedCats.update { storedCats ->
            val storedCatsWithoutConflicts = storedCats.filter { it.id in catIds }
            storedCatsWithoutConflicts.toMutableList().apply {
                addAll(cats)
            }
            return@update storedCatsWithoutConflicts
        }
    }

    override suspend fun clearCatsNotIn(catIds: List<String>) {
        storedCats.update { storedCats ->
            storedCats.filter { it.id in catIds }
        }
    }

    override suspend fun updateCat(cat: Cat) {
       storedCats.update { storedCats ->
           val index = storedCats.indexOfFirst { it.id == cat.id }.takeIf { it != -1 } ?: return@update storedCats
           val newStoredCats = storedCats.toMutableList()
           newStoredCats.removeAt(index)
           newStoredCats.add(index, cat)
           newStoredCats
       }
    }
}
