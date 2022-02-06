package com.aceofhigh.stockoverview.ui.stocks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.aceofhigh.stockoverview.data.PreferencesManager
import com.aceofhigh.stockoverview.data.SortOrder
import com.aceofhigh.stockoverview.data.Stock
import com.aceofhigh.stockoverview.data.StockDao
import com.aceofhigh.stockoverview.ui.ADD_TASK_RESULT_OK
import com.aceofhigh.stockoverview.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class StocksViewModel @ViewModelInject constructor(
    private val stockDao: StockDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    //initial value is empty string
    //we cannot store stateFlow inside a SavedStateHandle but we can store liveData and transform it into a flow and viceversa
    val searchQuery = state.getLiveData("searchQuery", "")

    private val preferencesFlow = preferencesManager.preferencesFlow

    //will be used to send information to fragment
    private val stocksEventChannel = Channel<StocksEvent>()
    val stocksEvent = stocksEventChannel.receiveAsFlow()

    private val stockFlow = combine(
        //turn it into a flow
        searchQuery.asFlow(),
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        stockDao.getStocks(query, filterPreferences.sortOrder)
    }

    val stocks = stockFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onStockSelected(stock: Stock) = viewModelScope.launch {
        stocksEventChannel.send(StocksEvent.NavigateToEditStockScreen(stock))
    }

    fun onStockCheckedChanged(stock: Stock, isChecked: Boolean) = viewModelScope.launch {
        stockDao.update(stock.copy(checked = isChecked))
    }

    fun onStockSwiped(stock: Stock) = viewModelScope.launch {
        stockDao.delete(stock)
        //use channel to send event
        stocksEventChannel.send(StocksEvent.ShowUndoDeleteStockMessage(stock))
    }

    fun onUndoDeleteClick(stock: Stock) = viewModelScope.launch {
        stockDao.insert(stock)
    }

    fun onAddStockCLick() = viewModelScope.launch {
        stocksEventChannel.send(StocksEvent.NavigateToAddStockScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showStockSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showStockSavedConfirmationMessage("Task edited")

        }
    }

    private fun showStockSavedConfirmationMessage(text: String) = viewModelScope.launch {

    }

    fun onDeleteAllCheckedClick() = viewModelScope.launch {
        stocksEventChannel.send(StocksEvent.NavigateToDeleteAllCheckedScreen)
    }

    //will be used in Channel to send to Fragment for <SnackBar>
    sealed class StocksEvent {
        object NavigateToAddStockScreen : StocksEvent()
        data class NavigateToEditStockScreen(val Stock: Stock) : StocksEvent()
        data class ShowUndoDeleteStockMessage(val stock: Stock) : StocksEvent()
        data class ShowStockSavedConfirmationMessage(val msg: String) : StocksEvent()
        object NavigateToDeleteAllCheckedScreen : StocksEvent()
    }
}

