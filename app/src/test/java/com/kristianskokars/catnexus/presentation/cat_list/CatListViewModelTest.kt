package com.kristianskokars.catnexus.presentation.cat_list

import com.kristianskokars.catnexus.core.data.repository.OfflineFirstCatRepository
import com.kristianskokars.catnexus.feature.cat_list.presentation.CatListViewModel
import org.junit.Before
import org.junit.Test

class CatListViewModelTest {
    private lateinit var viewModel: CatListViewModel

    @Before
    fun setup() {
        viewModel = CatListViewModel(OfflineFirstCatRepository())
    }

    @Test
    fun `Refreshing updates the cat flow`() {

    }

    @Test
    fun `Refreshing updates the isRefreshing flow`() {

    }
}
