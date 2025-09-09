/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.co.yumemi.android.code_check.databinding.FragmentSearchBinding
import jp.co.yumemi.android.code_check.databinding.LayoutItemBinding
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSearchBinding.bind(view)

        val layoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecoration(context, layoutManager.orientation)
        val adapter = CustomAdapter(object : CustomAdapter.OnItemClickListener {
            override fun itemClick(item: Item) {
                gotoRepositoryFragment(item)
            }
        })

        binding.searchInputText.setOnEditorActionListener { editText, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.onImeSearch(editText.text.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.recyclerView.also {
            it.layoutManager = layoutManager
            it.addItemDecoration(dividerItemDecoration)
            it.adapter = adapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchUiState.collect { searchUiState ->
                    binding.applyUiState(searchUiState)
                }
            }
        }
    }

    private fun FragmentSearchBinding.applyUiState(searchUiState: SearchUiState) {
        val adapter = recyclerView.adapter as CustomAdapter
        when (searchUiState) {
            SearchUiState.Init -> {
                adapter.submitList(emptyList())
            }

            SearchUiState.Loading -> {
                adapter.submitList(emptyList())
            }

            is SearchUiState.Results -> {
                val itemList = searchUiState.itemList
                if (itemList.isEmpty()) {
                    Toast.makeText(context, R.string.search_empty_message, Toast.LENGTH_SHORT).show()
                    viewModel.onShowEmptyResult()
                } else {
                    adapter.submitList(itemList)
                }
            }

            SearchUiState.Error -> {
                Toast.makeText(context, R.string.search_error_message, Toast.LENGTH_SHORT).show()
                viewModel.onShowErrorResult()
            }
        }
    }

    fun gotoRepositoryFragment(item: Item) {
        val action = SearchFragmentDirections
            .actionRepositoriesFragmentToRepositoryFragment(url = item.url, searchDate = item.fetchDate)
        findNavController().navigate(action)
    }
}

val diffUtil = object : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}

class CustomAdapter(
    private val itemClickListener: OnItemClickListener,
) : ListAdapter<Item, CustomAdapter.ViewHolder>(diffUtil) {

    class ViewHolder(val binding: LayoutItemBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener {
        fun itemClick(item: Item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.repositoryNameView.apply {
            text = item.name
            setOnClickListener {
                itemClickListener.itemClick(item)
            }
        }
    }
}
