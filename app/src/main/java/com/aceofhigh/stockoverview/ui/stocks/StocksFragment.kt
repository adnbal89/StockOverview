package com.aceofhigh.stockoverview.ui.stocks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aceofhigh.stockoverview.R
import com.aceofhigh.stockoverview.databinding.FragmentStocksBinding
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
    }

}