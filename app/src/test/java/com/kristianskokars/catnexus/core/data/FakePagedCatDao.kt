package com.kristianskokars.catnexus.core.data

import com.kristianskokars.catnexus.core.data.data_source.local.PagedCatDao
import com.kristianskokars.catnexus.core.data.model.PagedCatEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakePagedCatDao : PagedCatDao {
    private val storedCats = MutableStateFlow<List<PagedCatEntity>>(emptyList())

    override fun getPagedCats(): Flow<List<PagedCatEntity>> = storedCats.map { cats ->
        cats.filter { it.isFavourited }
    }

    override fun getCat(catId: String): Flow<PagedCatEntity> = storedCats.map { cats ->
        cats.find { it.id == catId } ?: throw RuntimeException("Cat not found")
    }

    override suspend fun addCats(cats: List<PagedCatEntity>) {
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

    override suspend fun updateCat(cat: PagedCatEntity) {
        storedCats.update { storedCats ->
            val index = storedCats.indexOfFirst { it.id == cat.id }.takeIf { it != -1 } ?: return@update storedCats
            val newStoredCats = storedCats.toMutableList()
            newStoredCats.removeAt(index)
            newStoredCats.add(index, cat)
            newStoredCats
        }
    }
}
