package com.kristianskokars.catnexus.core.data

import com.kristianskokars.catnexus.core.data.data_source.local.CatDao
import com.kristianskokars.catnexus.core.data.model.CatEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeCatDao : CatDao {
    private val storedCats = MutableStateFlow<List<CatEntity>>(emptyList())

    override fun getFavouritedCats(): Flow<List<CatEntity>> = storedCats.map { cats ->
        cats.filter { it.isFavourited }
    }

    override fun getCat(catId: String): Flow<CatEntity> = storedCats.map { cats ->
        cats.find { it.id == catId } ?: throw RuntimeException("Cat not found")
    }

    override suspend fun addCat(cat: CatEntity) {
        storedCats.update { storedCats ->
            val storedCatsWithoutConflicts = storedCats.filter { it.id == cat.id }
            storedCatsWithoutConflicts.toMutableList().apply {
                add(cat)
            }
            return@update storedCatsWithoutConflicts
        }
    }

    override suspend fun clearCatsNotIn(catIds: List<String>) {
        storedCats.update { storedCats ->
            storedCats.filter { it.id in catIds }
        }
    }

    override suspend fun updateCat(cat: CatEntity) {
       storedCats.update { storedCats ->
           val index = storedCats.indexOfFirst { it.id == cat.id }.takeIf { it != -1 } ?: return@update storedCats
           val newStoredCats = storedCats.toMutableList()
           newStoredCats.removeAt(index)
           newStoredCats.add(index, cat)
           newStoredCats
       }
    }
}
