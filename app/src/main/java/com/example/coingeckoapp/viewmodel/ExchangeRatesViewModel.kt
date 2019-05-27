package com.example.coingeckoapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.coingeckoapp.Constants
import com.example.coingeckoapp.networkservice.ExchangeRateService
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ExchangeRatesViewModel(application: Application) : AndroidViewModel(application) {

    internal var currencyRates: MutableLiveData<JsonObject> = MutableLiveData()

    private val exchangeService by lazy {
        ExchangeRateService.create()
    }

    private var disposable: Disposable? = null

    fun getExchangeRates() {
        disposable =
            exchangeService.getExchangeRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        val rates = result.getAsJsonObject(Constants.RATES)
                        currencyRates.value = rates
                    },
                    { error ->
                        Log.e("error", error.message)
                    }
                )
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}