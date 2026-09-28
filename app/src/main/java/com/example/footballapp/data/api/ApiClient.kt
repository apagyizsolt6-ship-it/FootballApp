package com.example.footballapp.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

object ApiClient {

    /**
     * 1. Menj ide: https://www.football-data.org/client/register
     * 2. Regisztrálj (ingyenes)
     * 3. A kapott tokent írd be ide:
     */
    private const val API_TOKEN = "b429d8a5da2345449573583e29ecff12"   // ← CSERÉLD KI!

    private const val BASE_URL = "https://api.football-data.org/v4/"

    /** Rate limit: millis when we can retry again (0 = ok) */
    val rateLimitUntilMs = AtomicLong(0)

    fun isRateLimited(): Boolean = System.currentTimeMillis() < rateLimitUntilMs.get()

    fun rateLimitSecondsLeft(): Int {
        val left = rateLimitUntilMs.get() - System.currentTimeMillis()
        return if (left > 0) ((left + 999) / 1000).toInt() else 0
    }

    class RateLimitException(val retryAfterSeconds: Int) :
        IOException("Túl sok kérés (rate limit). Próbáld újra ${retryAfterSeconds} mp múlva.")

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("X-Auth-Token", API_TOKEN)
            .build()
        chain.proceed(request)
    }

    private val rateLimitInterceptor = Interceptor { chain ->
        if (isRateLimited()) {
            throw RateLimitException(rateLimitSecondsLeft().coerceAtLeast(1))
        }
        val response: Response = chain.proceed(chain.request())
        if (response.code == 429) {
            val retryAfter = response.header("Retry-After")?.toLongOrNull() ?: 60L
            rateLimitUntilMs.set(System.currentTimeMillis() + retryAfter * 1000)
            response.close()
            throw RateLimitException(retryAfter.toInt())
        }
        // football-data also sends X-Requests-Available-Minute
        response
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(rateLimitInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: SportsApi = retrofit.create(SportsApi::class.java)
}
