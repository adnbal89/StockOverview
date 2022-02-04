package com.aceofhigh.stockoverview.ui.stocks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aceofhigh.stockoverview.R
import com.aceofhigh.stockoverview.databinding.FragmentStocksBinding
import com.aceofhigh.stockoverview.util.OnQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StocksFragment : Fragment(R.layout.fragment_stocks) {

    private val viewModel: StocksViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentStocksBinding.bind(view)

        val stockAdapter = StocksAdapter()

        binding.apply {
            recyclerViewStocks.apply {
                adapter = stockAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.stocks.observe(viewLifecycleOwner) {
            stockAdapter.submitList(it)
        }

        setHasOptionsMenu(true)


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_stocks, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.OnQueryTextChanged {
            //crossinline cannot allow return here.
            viewModel.searchQuery.value = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.sortOrder.value = SortOrder.BY_NAME
                true
            }
            R.id.action_sort_by_profit -> {
                viewModel.sortOrder.value = SortOrder.BY_PROFIT
                true
            }
            R.id.action_sort_by_volume -> {
                viewModel.sortOrder.value = SortOrder.BY_VOLUME
                true
            }
            R.id.action_delete_selected -> {
                //delete selected.
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

