package com.aceofhigh.stockoverview.ui.stocks

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aceofhigh.stockoverview.data.Stock
import com.aceofhigh.stockoverview.databinding.ItemStockBinding


class StocksAdapter : ListAdapter<Stock, StocksAdapter.StocksViewHolder>(DiffCallback()) {

    //how to instantiate one of our viewHolder classes.
    //Whenever an item in the list needed, this is how it can get one.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StocksViewHolder {

        //layoutinflation means xml to object
        val binding = ItemStockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StocksViewHolder(binding)
    }

    //we have to define how we bind the data to the viewHolder
    override fun onBindViewHolder(holder: StocksViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    //binding from XML, you can use inside the function.
    class StocksViewHolder(private val binding: ItemStockBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stock: Stock) {
            binding.apply {

                val profit = calculateProfit(stock.buyPrice, stock.currentPrice)
                checkBoxToDelete.isChecked = stock.checked

                textViewName.text = stock.name
                textViewBuyPrice.text = stock.buyPrice.toString()
                textViewCurrentPrice.text = stock.currentPrice.toString()
                textViewProfit.text = profit.toString()
                textViewProfit.setTextColor(getColorForProfitValue(profit))
                //checkBoxToDelete.isVisible = false
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Stock>() {
        override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean =
            oldItem.id == newItem.id

        //When one of Stock's fields have changed, this callback will know. and we know that we have to refresh item on the screen
        //= ->  return x functionality is the same.
        override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean =
            oldItem == newItem
    }

}

private fun calculateProfit(buyPrice: Double, currentPrice: Double): Double {
    return currentPrice - buyPrice
}

private fun getColorForProfitValue(profit: Double): Int {

    val selectedColor: Int
    val colorRed: Int = Color.rgb(255, 0, 0)
    val colorBlue: Int = Color.rgb(24, 123, 205)

    if (profit >= 0)
        selectedColor = colorBlue
    else selectedColor = colorRed

    return selectedColor
}
