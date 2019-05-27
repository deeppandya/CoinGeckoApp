package com.example.coingeckoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.coingeckoapp.networkservice.ExchangeRateService
import com.example.coingeckoapp.viewmodel.ExchangeRatesViewModel
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.reactivex.Observable
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner.Silent::class)
class ExchangeRateViewModelTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    // Test rule for making the RxJava to run synchronously in unit test
    companion object {
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }

    @Mock
    lateinit var exchangeRateService: ExchangeRateService

    @Mock
    lateinit var observer: Observer<JsonObject>

    lateinit var exchangeRatesViewModel: ExchangeRatesViewModel

    @Before
    fun setUp() {
        exchangeRatesViewModel = ExchangeRatesViewModel()
    }

    @Test
    fun shouldShowApiResult() {

        val jsonString = "{\n" +
                "    \"btc\": {\n" +
                "      \"name\": \"Bitcoin\",\n" +
                "      \"unit\": \"BTC\",\n" +
                "      \"value\": 1,\n" +
                "      \"type\": \"crypto\"\n" +
                "    }" +
                "}"


        val jsonObject = JsonParser().parse(jsonString).asJsonObject

        Mockito.`when`(exchangeRateService.getExchangeRates()).thenReturn(Observable.just(jsonObject))

        // observe on the MutableLiveData with an observer
        exchangeRatesViewModel.currencyRates.observeForever(observer)
        exchangeRatesViewModel.getExchangeRates()

        assert(
            exchangeRatesViewModel.currencyRates.value!!.getAsJsonObject(Constants.BTC).get(
                "value"
            ).asInt == 1
        )
    }
}