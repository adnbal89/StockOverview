package com.aceofhigh.stockoverview.ui.stocks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.aceofhigh.stockoverview.data.StockDao

class StocksViewModel @ViewModelInject constructor(
    private val stockDao: StockDao
) : ViewModel() {
    val stocks = stockDao.getStocks().asLiveData()
}