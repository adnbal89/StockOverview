package com.aceofhigh.stockoverview.util

import android.view.View
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.item_stock.view.*

inline fun SearchView.OnQueryTextChanged(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}

inline fun View.onLongClickListener(crossinline listener: (String) -> Unit) {
    this.setOnLongClickListener {
        listener(it.text_view_name.text.toString())
        true
    }
}

fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()
