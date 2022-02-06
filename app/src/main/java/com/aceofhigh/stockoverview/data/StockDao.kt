package com.aceofhigh.stockoverview.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {

    fun getStocks(query: String, sortOrder: SortOrder): Flow<List<Stock>> =
        when (sortOrder) {
            SortOrder.BY_NAME -> getStocksSortedByName(query)
            SortOrder.BY_PROFIT -> getStocksSortedByProfit(query)
            SortOrder.BY_VOLUME -> getStocksSortedByVolume(query)
        }

    @Query("SELECT * FROM stock_table WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name")
    fun getStocksSortedByName(searchQuery: String): Flow<List<Stock>>

    @Query("SELECT * FROM stock_table WHERE name LIKE '%' || :searchQuery || '%' ORDER BY (currentPrice - buyPrice)/currentPrice DESC")
    fun getStocksSortedByProfit(searchQuery: String): Flow<List<Stock>>

    @Query("SELECT * FROM stock_table WHERE name LIKE '%' || :searchQuery || '%' ORDER BY (lotSize * currentPrice) DESC")
    fun getStocksSortedByVolume(searchQuery: String): Flow<List<Stock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stock: Stock)

    @Update
    suspend fun update(stock: Stock)

    @Delete
    suspend fun delete(task: Stock)

    @Query("DELETE FROM stock_table WHERE checked = 1")
    suspend fun deleteCheckedStocks()

}