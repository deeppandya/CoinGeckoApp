package com.example.coingeckoapp.activity

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.coingeckoapp.Constants
import com.example.coingeckoapp.R
import com.example.coingeckoapp.model.CurrencyModel
import com.example.coingeckoapp.viewmodel.ExchangeRatesViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private lateinit var exchangeRatesViewModel: ExchangeRatesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        exchangeRatesViewModel = ViewModelProviders.of(this).get(ExchangeRatesViewModel::class.java)

        exchangeRatesViewModel.currencyRates.observe(this, Observer {
            val btcCurrency = Gson().fromJson(it.getAsJsonObject(Constants.BTC), CurrencyModel::class.java)
            val usdCurrency = Gson().fromJson(it.getAsJsonObject(Constants.USD), CurrencyModel::class.java)
            val ethCurrency = Gson().fromJson(it.getAsJsonObject(Constants.ETH), CurrencyModel::class.java)

            val btcRate = usdCurrency.value
            tvBtc.text = resources.getString(R.string.btc_rate, btcRate.toString())
            val ethRate = (btcCurrency.value.div(ethCurrency.value)) * usdCurrency.value
            tvEth.text = resources.getString(R.string.eth_rate, ethRate.toString())

            loadWebview(btcRate, ethRate)
        })

        btnRefresh.setOnClickListener {
            showToastMessage(resources.getString(R.string.updating_rates))
            exchangeRatesViewModel.getExchangeRates()
        }
    }

    override fun onStart() {
        super.onStart()
        startApiSchedular()
    }

    override fun onStop() {
        stopApiSchedular()
        super.onStop()
    }

    private fun loadWebview(btcRate: Double, ethRate: Double) {
        val customHtml = String.format(getString(R.string.html_page), btcRate, ethRate)
        webview.loadData(customHtml, "text/html", "UTF-8")
    }

    private fun startApiSchedular() {
        handler = Handler()
        apiRunnable.run()
    }

    private fun stopApiSchedular() {
        handler.removeCallbacks(apiRunnable)
    }

    fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private var apiRunnable: Runnable = object : Runnable {

        private val mInterval: Long = 60 * 1000
        override fun run() {
            showToastMessage(resources.getString(R.string.updating_rates))
            try {
                exchangeRatesViewModel.getExchangeRates()
            } finally {
                handler.postDelayed(this, mInterval)
            }
        }
    }
}
