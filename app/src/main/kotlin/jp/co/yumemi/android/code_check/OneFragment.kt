/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.co.yumemi.android.code_check.databinding.FragmentOneBinding
import jp.co.yumemi.android.code_check.databinding.LayoutItemBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OneFragment : Fragment(R.layout.fragment_one) {

    private var searchJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentOneBinding.bind(view)

        val viewModel = OneViewModel(requireActivity().application)

        val layoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecoration(context, layoutManager.orientation)
        val adapter = CustomAdapter(object : CustomAdapter.OnItemClickListener {
            override fun itemClick(item: Item) {
                gotoRepositoryFragment(item)
            }
        })

        binding.searchInputText
            .setOnEditorActionListener { editText, action, _ ->
                if (action == EditorInfo.IME_ACTION_SEARCH) {
                    editText.text.toString().let {
                        searchJob?.cancel()
                        searchJob = viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.searchResults(it).apply {
                                adapter.submitList(this)
                            }
                        }
                    }
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

        binding.recyclerView.also {
            it.layoutManager = layoutManager
            it.addItemDecoration(dividerItemDecoration)
            it.adapter = adapter
        }
    }

    fun gotoRepositoryFragment(item: Item) {
        val action = OneFragmentDirections
            .actionRepositoriesFragmentToRepositoryFragment(item = item)
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
