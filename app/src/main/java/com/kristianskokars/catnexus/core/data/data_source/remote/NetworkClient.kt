package com.kristianskokars.catnexus.core.data.data_source.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.kristianskokars.catnexus.BuildConfig
import com.kristianskokars.catnexus.core.NETWORK_TIMEOUT
import com.kristianskokars.catnexus.lib.json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object NetworkClient {
    private val httpClient by lazy {
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
        builder.build()
    }

    fun retrofitClient(url: String): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(url)
            .client(httpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}
