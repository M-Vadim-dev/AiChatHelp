package com.example.aichathelp.di

import com.example.aichathelp.BuildConfig
import com.example.aichathelp.data.config.ChatPrompts
import com.example.aichathelp.data.mapper.ChatRequestMapper
import com.example.aichathelp.data.mapper.JsonResponseMapper
import com.example.aichathelp.data.remote.ChatApi
import com.example.aichathelp.data.repository.PromptRepositoryImpl
import com.example.aichathelp.domain.repository.PromptRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        val apiKeyInterceptor = Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", "Bearer ${BuildConfig.API_KEY}")
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(apiKeyInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(json: Json, client: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://api.perplexity.ai/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideChatApi(retrofit: Retrofit): ChatApi = retrofit.create(ChatApi::class.java)

    @Provides
    fun provideJsonResponseMapper(json: Json): JsonResponseMapper = JsonResponseMapper(json)

    @Provides
    fun provideRequestMapper(json: Json): ChatRequestMapper = ChatRequestMapper(json)

    @Provides
    @Singleton
    fun providePromptRepository(): PromptRepository = PromptRepositoryImpl(ChatPrompts)

}