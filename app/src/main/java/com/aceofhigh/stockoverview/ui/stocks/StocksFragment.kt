package com.aceofhigh.stockoverview.ui.stocks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aceofhigh.stockoverview.R
import com.aceofhigh.stockoverview.data.SortOrder
import com.aceofhigh.stockoverview.data.Stock
import com.aceofhigh.stockoverview.databinding.FragmentStocksBinding
import com.aceofhigh.stockoverview.util.OnQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class StocksFragment : Fragment(R.layout.fragment_stocks), StocksAdapter.OnItemCLickListener {

    private val viewModel: StocksViewModel by viewModels()
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentStocksBinding.bind(view)

        //delegate to here. (this)
        val stockAdapter = StocksAdapter(this)

        binding.apply {
            recyclerViewStocks.apply {
                adapter = stockAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val stock = stockAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onStockSwiped(stock)
                }
            }).attachToRecyclerView(recyclerViewStocks)

            fabStocks.setOnClickListener {
                viewModel.onAddStockCLick()
            }
        }

        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewModel.stocks.observe(viewLifecycleOwner) {
            stockAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.stocksEvent.collect { event ->
                when (event) {
                    is StocksViewModel.StocksEvent.ShowUndoDeleteStockMessage -> {
                        Snackbar.make(requireView(), "Stock deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoDeleteClick(event.stock)
                            }.show()
                    }
                    is StocksViewModel.StocksEvent.NavigateToAddStockScreen -> {
                        //comes from nav_args
                        //compile time safety, in case if fragment expects an argument etc.
                        val action =
                            StocksFragmentDirections.actionStocksFragmentToAddEditStockFragment(
                                null,
                                "New Stock"
                            )
                        findNavController().navigate(action)
                    }
                    is StocksViewModel.StocksEvent.NavigateToEditStockScreen -> {
                        val action =
                            StocksFragmentDirections.actionStocksFragmentToAddEditStockFragment(
                                event.Stock,
                                "Edit Stock"
                            )
                        findNavController().navigate(action)
                    }
                    is StocksViewModel.StocksEvent.ShowStockSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    StocksViewModel.StocksEvent.NavigateToDeleteAllCheckedScreen -> {
                        val action =
                            StocksFragmentDirections.actionGlobalDeleteAllCheckedDialogFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onItemClick(stock: Stock) {
        viewModel.onStockSelected(stock)
    }

    override fun onCheckBoxCLick(stock: Stock, isChecked: Boolean) {
        viewModel.onStockCheckedChanged(stock, isChecked)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_stocks, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.OnQueryTextChanged {
            //crossinline cannot allow return here.
            viewModel.searchQuery.value = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_profit -> {
                viewModel.onSortOrderSelected(SortOrder.BY_PROFIT)
                true
            }
            R.id.action_sort_by_volume -> {
                viewModel.onSortOrderSelected(SortOrder.BY_VOLUME)
                true
            }
            R.id.action_delete_selected -> {
                viewModel.onDeleteAllCheckedClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //remove the listener so we do not send this unnecessary string.
        searchView.setOnQueryTextListener(null)
    }
}

