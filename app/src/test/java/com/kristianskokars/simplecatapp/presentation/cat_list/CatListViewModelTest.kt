package com.kristianskokars.simplecatapp.presentation.cat_list

import com.kristianskokars.simplecatapp.data.repository.CatRepositoryImpl
import org.junit.Before
import org.junit.Test

class CatListViewModelTest {
    private lateinit var viewModel: CatListViewModel

    @Before
    fun setup() {
        viewModel = CatListViewModel(CatRepositoryImpl())
    }

    @Test
    fun `Refreshing updates the cat flow`() {

    }

    @Test
    fun `Refreshing updates the isRefreshing flow`() {

    }
}
