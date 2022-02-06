package com.aceofhigh.stockoverview.ui.deleteallchecked

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.aceofhigh.stockoverview.data.StockDao
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllCheckedViewModel @ViewModelInject constructor(
    private val stockDao: StockDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {
    fun onConfirmClick() = applicationScope.launch {
        stockDao.deleteCheckedStocks()
    }
}