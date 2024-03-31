package com.kristianskokars.catnexus.feature.cat_list.presentation

import androidx.work.WorkManager
import com.kristianskokars.catnexus.core.data.FakeCatDao
import com.kristianskokars.catnexus.core.data.FakePagedCatDao
import com.kristianskokars.catnexus.core.data.FakeUserSettingsStore
import com.kristianskokars.catnexus.core.data.data_source.remote.CatAPI
import com.kristianskokars.catnexus.core.data.data_source.remote.NetworkClient
import com.kristianskokars.catnexus.core.data.repository.OfflineFirstCatRepository
import com.kristianskokars.catnexus.core.domain.CarModeHandler
import com.kristianskokars.catnexus.lib.MainDispatcherRule
import com.kristianskokars.catnexus.lib.Toaster
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CatListViewModelTest {
    private lateinit var viewModel: CatListViewModel
    private lateinit var fakeCatDao: FakeCatDao
    private lateinit var fakePagedCatDao: FakePagedCatDao
    private lateinit var fakeUserSettingsStore: FakeUserSettingsStore
    private val mockWebServer = MockWebServer()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        mockWebServer.start()
        fakeCatDao = FakeCatDao()
        fakePagedCatDao = FakePagedCatDao()
        fakeUserSettingsStore = FakeUserSettingsStore()
        viewModel = CatListViewModel(
            repository = OfflineFirstCatRepository(
                catDao = fakeCatDao,
                pagedCatDao = fakePagedCatDao,
                remote = NetworkClient.retrofitClient(
                    mockWebServer.url("/").toString(),
                ).create(CatAPI::class.java),
                workManager = mockk<WorkManager>(relaxed = true),
                ioDispatcher = StandardTestDispatcher(),
            ),
            carModeHandler = CarModeHandler(
                toaster = Toaster(),
                store = fakeUserSettingsStore,
            ),
        )
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `Is loading in new cats at the start`() {
        true shouldBe true
        viewModel.state.value shouldBe CatListState.Loading
    }
}
