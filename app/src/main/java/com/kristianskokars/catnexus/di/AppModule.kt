package com.kristianskokars.catnexus.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.kristianskokars.catnexus.BuildConfig
import com.kristianskokars.catnexus.core.BASE_URL
import com.kristianskokars.catnexus.core.CAT_DATABASE
import com.kristianskokars.catnexus.core.NETWORK_TIMEOUT
import com.kristianskokars.catnexus.core.data.data_source.local.CatDao
import com.kristianskokars.catnexus.core.data.data_source.local.CatDatabase
import com.kristianskokars.catnexus.core.domain.repository.FileStorage
import com.kristianskokars.catnexus.core.data.data_source.local.AndroidFileStorage
import com.kristianskokars.catnexus.core.data.data_source.remote.CatAPI
import com.kristianskokars.catnexus.core.data.repository.OfflineFirstCatRepository
import com.kristianskokars.catnexus.core.domain.repository.CatRepository
import com.kristianskokars.catnexus.lib.json
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@ExperimentalSerializationApi
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient
            .Builder()
            .connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(interceptor)
        }
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofitClient(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideCatAPI(retrofit: Retrofit): CatAPI = retrofit.create(CatAPI::class.java)

    @Provides
    @Singleton
    fun provideCatDatabase(@ApplicationContext context: Context): CatDatabase =
        Room.databaseBuilder(
            context,
            CatDatabase::class.java,
            CAT_DATABASE,
        ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideCatDao(db: CatDatabase): CatDao = db.catDao()

    @Provides
    @Singleton
    fun provideCatRepository(
        catAPI: CatAPI,
        catDb: CatDatabase,
        workManager: WorkManager,
    ): CatRepository = OfflineFirstCatRepository(catDb, catAPI, workManager)

    @Provides
    @Singleton
    fun provideFileStorage(@ApplicationContext context: Context): FileStorage = AndroidFileStorage(context)

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager = WorkManager.getInstance(context)
}
