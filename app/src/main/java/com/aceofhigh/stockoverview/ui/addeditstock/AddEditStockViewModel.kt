package com.aceofhigh.stockoverview.ui.addeditstock

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aceofhigh.stockoverview.data.Stock
import com.aceofhigh.stockoverview.data.StockDao
import com.aceofhigh.stockoverview.ui.ADD_TASK_RESULT_OK
import com.aceofhigh.stockoverview.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditStockViewModel @ViewModelInject constructor(
    private val stockDao: StockDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    //getting from nav_graph argument, string has to be exactly the same.
    val stock = state.get<Stock>("stock")

    var stockName = state.get<String>("stockName") ?: stock?.name ?: ""
        set(value) {
            field = value
            state.set("stockName", value)
        }

    var stockBuyPrice = state.get<Double>("stockBuyPrice") ?: stock?.buyPrice ?: 0.00
        set(value) {
            field = value
            state.set("stockBuyPrice", value)
        }

    var stockCurrentPrice = state.get<Double>("stockCurrentPrice") ?: stock?.currentPrice ?: 0.00
        set(value) {
            field = value
            state.set("stockCurrentPrice", value)
        }

    var stockLot = state.get<Int>("stockLot") ?: stock?.lotSize ?: 0
        set(value) {
            field = value
            state.set("stockLot", value)
        }

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (stockName.isBlank()) {
            showInvalidInputMessage("Name cannot be empty")
            return
        }

        if (stock != null) {
            val updatedStock = stock.copy(
                name = stockName,
                buyPrice = stockBuyPrice,
                currentPrice = stockCurrentPrice,
                lotSize = stockLot
            )
            updateStock(updatedStock)
        } else {
            val newStock = Stock(
                name = stockName,
                buyPrice = stockBuyPrice,
                currentPrice = stockCurrentPrice,
                lotSize = stockLot
            )
            createStock(newStock)
        }
    }

    private fun createStock(stock: Stock) = viewModelScope.launch {
        stockDao.insert(stock)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateStock(stock: Stock) = viewModelScope.launch {
        stockDao.update(stock)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
    }

    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
    }
}