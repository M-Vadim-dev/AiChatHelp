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
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PerplexityApiClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeepSeekApiClient

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
    @PerplexityApiClient
    fun providePerplexityClient(): OkHttpClient = createOkHttpClient(BuildConfig.PERPLEXITY_API_KEY)

    @Provides
    @Singleton
    @DeepSeekApiClient
    fun provideDeepSeekClient(): OkHttpClient = createOkHttpClient(BuildConfig.DEEPSEEK_API_KEY)

    @Provides
    @Singleton
    @PerplexityApiClient
    fun providePerplexityRetrofit(json: Json, @PerplexityApiClient client: OkHttpClient): Retrofit =
        createRetrofit(json = json, baseUrl = "https://api.perplexity.ai/", client = client)

    @Provides
    @Singleton
    @DeepSeekApiClient
    fun provideDeepSeekRetrofit(json: Json, @DeepSeekApiClient client: OkHttpClient): Retrofit =
        createRetrofit(json = json, baseUrl = "https://api.deepseek.com/", client = client)

    @Provides
    @Singleton
    @PerplexityApiClient
    fun providePerplexityChatApi(@PerplexityApiClient retrofit: Retrofit): ChatApi =
        retrofit.create(ChatApi::class.java)

    @Provides
    @Singleton
    @DeepSeekApiClient
    fun provideDeepSeekChatApi(@DeepSeekApiClient retrofit: Retrofit): ChatApi =
        retrofit.create(ChatApi::class.java)

    @Provides
    fun provideJsonResponseMapper(json: Json): JsonResponseMapper = JsonResponseMapper(json)

    @Provides
    fun provideRequestMapper(json: Json): ChatRequestMapper = ChatRequestMapper(json)

    @Provides
    @Singleton
    fun providePromptRepository(): PromptRepository = PromptRepositoryImpl(ChatPrompts)

}

private fun createOkHttpClient(apiKey: String): OkHttpClient {
    val logging = HttpLoggingInterceptor().apply {
        level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
    }

    val apiKeyInterceptor = Interceptor { chain ->
        val req = chain.request()
            .newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .build()
        chain.proceed(req)
    }

    return OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(apiKeyInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
}

private fun createRetrofit(json: Json, baseUrl: String, client: OkHttpClient): Retrofit {
    val contentType = "application/json".toMediaType()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory(contentType))
        .client(client)
        .build()
}