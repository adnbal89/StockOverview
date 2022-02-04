package com.aceofhigh.stockoverview.ui.stocks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.aceofhigh.stockoverview.data.StockDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class StocksViewModel @ViewModelInject constructor(
    private val stockDao: StockDao
) : ViewModel() {
    //initial value is empty string
    val searchQuery = MutableStateFlow("")
    val sortOrder = MutableStateFlow(SortOrder.BY_NAME)

    private val stockFlow = combine(
        searchQuery,
        sortOrder
    ) { query, sortOrder ->
        Pair(query, sortOrder)
    }.flatMapLatest { (query, sortOrder) ->
        stockDao.getStocks(query, sortOrder)
    }

    val stocks = stockFlow.asLiveData()
}

enum class SortOrder {
    BY_NAME,
    BY_PROFIT,
    BY_VOLUME
}