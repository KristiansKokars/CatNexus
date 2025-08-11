package com.kristianskokars.catnexus.di

import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.room.Room
import androidx.work.WorkManager
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.kristianskokars.catnexus.core.BASE_URL
import com.kristianskokars.catnexus.core.CAT_DATABASE
import com.kristianskokars.catnexus.core.data.data_source.local.AndroidImageDownloader
import com.kristianskokars.catnexus.core.data.data_source.local.AndroidImageSharer
import com.kristianskokars.catnexus.core.data.data_source.local.CatDao
import com.kristianskokars.catnexus.core.data.data_source.local.CatDatabase
import com.kristianskokars.catnexus.core.data.data_source.local.ContributorDao
import com.kristianskokars.catnexus.core.data.data_source.local.PagedCatDao
import com.kristianskokars.catnexus.core.data.data_source.local.userSettingsStore
import com.kristianskokars.catnexus.core.data.data_source.remote.CatAPI
import com.kristianskokars.catnexus.core.data.data_source.remote.NetworkClient
import com.kristianskokars.catnexus.core.data.repository.OfflineFirstCatRepository
import com.kristianskokars.catnexus.core.domain.model.UserSettings
import com.kristianskokars.catnexus.core.domain.repository.CatRepository
import com.kristianskokars.catnexus.core.domain.repository.ImageDownloader
import com.kristianskokars.catnexus.core.domain.repository.ImageSharer
import com.kristianskokars.catnexus.feature.contributors.data.GitHubAPI
import com.kristianskokars.catnexus.feature.contributors.data.repository.OfflineFirstContributorRepository
import com.kristianskokars.catnexus.feature.contributors.domain.repository.ContributorRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.ExperimentalSerializationApi
import retrofit2.Retrofit
import javax.inject.Singleton
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@ExperimentalSerializationApi
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofitClient(): Retrofit = NetworkClient.retrofitClient(BASE_URL)

    @Provides
    @Singleton
    fun provideCatAPI(retrofit: Retrofit): CatAPI = retrofit.create(CatAPI::class.java)

    @Provides
    @Singleton
    fun provideGitHubAPI(retrofit: Retrofit): GitHubAPI = retrofit.create(GitHubAPI::class.java)

    @Provides
    @Singleton
    fun provideCatDatabase(@ApplicationContext context: Context): CatDatabase =
        Room.databaseBuilder(
            context,
            CatDatabase::class.java,
            CAT_DATABASE,
        ).build()

    @Provides
    @Singleton
    fun provideCatDao(db: CatDatabase): CatDao = db.catDao()

    @Provides
    @Singleton
    fun providePagedCatDao(db: CatDatabase): PagedCatDao = db.pagedCatDao()

    @Provides
    @Singleton
    fun provideContributorDao(db: CatDatabase): ContributorDao = db.contributorDao()

    @Provides
    @Singleton
    fun provideCatRepository(
        catDao: CatDao,
        pagedCatDao: PagedCatDao,
        remote: CatAPI,
        workManager: WorkManager,
    ): CatRepository = OfflineFirstCatRepository(catDao, pagedCatDao, remote, workManager)

    @OptIn(ExperimentalTime::class)
    @Provides
    @Singleton
    fun provideContributorRepository(
        gitHubAPI: GitHubAPI,
        contributorDao: ContributorDao,
        clock: Clock,
    ): ContributorRepository = OfflineFirstContributorRepository(
        gitHubAPI = gitHubAPI,
        contributorDao = contributorDao,
        ioScope = CoroutineScope(
            Dispatchers.IO + SupervisorJob()
        ),
        clock = clock,
    )

    @OptIn(ExperimentalTime::class)
    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.System

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager = WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideGifImageLoader(@ApplicationContext context: Context): ImageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    @Provides
    @Singleton
    fun provideImageDownloader(androidImageDownloader: AndroidImageDownloader): ImageDownloader = androidImageDownloader

    @Provides
    @Singleton
    fun provideImageSharer(@ApplicationContext context: Context): ImageSharer = AndroidImageSharer(context, CoroutineScope(Dispatchers.IO))

    @Provides
    @Singleton
    fun provideUserSettingStore(@ApplicationContext context: Context): DataStore<UserSettings> = context.userSettingsStore
}
