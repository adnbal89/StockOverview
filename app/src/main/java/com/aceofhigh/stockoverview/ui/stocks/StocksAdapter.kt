package com.aceofhigh.stockoverview.ui.stocks

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aceofhigh.stockoverview.data.Stock
import com.aceofhigh.stockoverview.databinding.ItemStockBinding
import com.aceofhigh.stockoverview.util.onLongClickListener
import com.aceofhigh.stockoverview.util.round


class StocksAdapter(private val listener: OnItemCLickListener) :
    ListAdapter<Stock, StocksAdapter.StocksViewHolder>(DiffCallback()) {

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
    inner class StocksViewHolder(private val binding: ItemStockBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val stock = getItem(position)
                        listener.onItemClick(stock)
                    }
                }

                checkBoxToDelete.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val stock = getItem(position)
                        listener.onCheckBoxCLick(stock, checkBoxToDelete.isChecked)
                    }
                }
            }
        }

        fun bind(stock: Stock) {
            binding.apply {

                val profit = calculateProfit(stock.buyPrice, stock.currentPrice).round(2) * 100
                val percentageProfit = "%$profit"

                checkBoxToDelete.isChecked = stock.checked

                textViewName.text = stock.name
                textViewBuyPrice.text = stock.buyPrice.round(2).toString()
                textViewCurrentPrice.text = stock.currentPrice.round(2).toString()
                textViewProfit.text = percentageProfit.toString()
                textViewLotSize.text = stock.lotSize.toString()
                textViewVolumeAmount.text =
                    calculateVolumeAmount(stock.lotSize, stock.currentPrice).round(2).toString()
                textViewProfit.setTextColor(getColorForProfitValue(profit))
                //checkBoxToDelete.isVisible = false

                itemView.onLongClickListener {
                    println(it)
                }
            }
        }


    }

    interface OnLongClickListener {

    }

    interface OnItemCLickListener {
        fun onItemClick(stock: Stock)
        fun onCheckBoxCLick(stock: Stock, isChecked: Boolean)
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

//TODO : refactor these functions
private fun calculateProfit(buyPrice: Double, currentPrice: Double): Double {
    return (currentPrice - buyPrice) / buyPrice
}

private fun calculateVolumeAmount(lotSize: Int, currentPrice: Double): Double {
    return lotSize * currentPrice
}

private fun getColorForProfitValue(profit: Double): Int {

    val selectedColor: Int
    val colorRed: Int = Color.rgb(255, 0, 0)
    val colorBlue: Int = Color.rgb(24, 123, 205)

    selectedColor = if (profit >= 0)
        colorBlue
    else colorRed

    return selectedColor
}

