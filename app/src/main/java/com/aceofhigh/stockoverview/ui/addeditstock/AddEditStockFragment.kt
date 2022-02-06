package com.aceofhigh.stockoverview.ui.addeditstock

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aceofhigh.stockoverview.R
import com.aceofhigh.stockoverview.databinding.FragmentAddEditStockBinding
import com.aceofhigh.stockoverview.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditStockFragment : Fragment(R.layout.fragment_add_edit_stock) {

    private val viewModel: AddEditStockViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditStockBinding.bind(view)

        binding.apply {
            editTextStockName.setText(viewModel.stockName)
            editTextStockBuyPrice.setText(viewModel.stockBuyPrice.toString())
            editTextStockCurrentPrice.setText(viewModel.stockCurrentPrice.toString())
            editTextStockLot.setText(viewModel.stockLot.toString())
            textViewDateCreated.isVisible = viewModel.stock != null
            textViewDateCreated.text = "Created: ${viewModel.stock?.createDateFormatted}"

            editTextStockName.addTextChangedListener {
                viewModel.stockName = it.toString()
            }
            editTextStockBuyPrice.addTextChangedListener {
                try {
                    viewModel.stockBuyPrice = it.toString().toDouble()
                } catch (e: Exception) {
                    Snackbar.make(
                        requireView(),
                        "Buy Price must be number or cannot be blank",
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                }
            }
            editTextStockCurrentPrice.addTextChangedListener {
                try {
                    viewModel.stockCurrentPrice = it.toString().toDouble()
                } catch (e: Exception) {
                    Snackbar.make(
                        requireView(),
                        "Current Price must be number or cannot be blank",
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                }
            }
            editTextStockLot.addTextChangedListener {
                try {
                    viewModel.stockLot = it.toString().toInt()
                } catch (e: Exception) {
                    Snackbar.make(
                        requireView(),
                        "Lot size must be number  or cannot be blank",
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                }

            }

            fabSaveStock.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        //to read the channel created in the viewModel
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect { event ->
                when (event) {
                    is AddEditStockViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is AddEditStockViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                        binding.editTextStockName.clearFocus()
                        binding.editTextStockBuyPrice.clearFocus()
                        binding.editTextStockBuyPrice.clearFocus()
                        binding.editTextStockLot.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)

                        )
                        findNavController().popBackStack()
                    }
                }.exhaustive
            }
        }
    }
}