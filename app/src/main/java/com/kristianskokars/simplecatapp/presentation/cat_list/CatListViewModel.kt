package com.kristianskokars.simplecatapp.presentation.cat_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.simplecatapp.domain.repository.CatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Remember to add Application and MainActivity @AndroidEntryPoint
@HiltViewModel
class CatListViewModel @Inject constructor(
    private val repository: CatRepository
): ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    val cats = repository.cats.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        refreshCats()
    }

    fun refreshCats() {
        viewModelScope.launch {
            Log.d("CatListViewModel", "Refreshing cats")
            _isRefreshing.update { true }
            repository.refreshCats()
            _isRefreshing.update { false }
        }
    }
}
