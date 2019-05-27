package com.example.coingeckoapp.networkservice

import com.example.coingeckoapp.BuildConfig
import com.example.coingeckoapp.Constants
import retrofit2.Retrofit
import retrofit2.http.GET
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


interface ExchangeRateService {
    companion object {
        fun create(): ExchangeRateService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.EXCHANGE_RATES_END_POINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            return retrofit.create(ExchangeRateService::class.java)
        }
    }

    @GET(Constants.EXCHANGE_RATES)
    fun getExchangeRates(): Observable<JsonObject>
}