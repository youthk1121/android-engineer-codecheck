/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import coil.load
import jp.co.yumemi.android.code_check.databinding.FragmentDetailBinding
import kotlinx.coroutines.flow.onEach

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val viewModel: DetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("検索した日時", viewModel.searchDate.toString())

        viewModel.onViewCreated()

        val binding = FragmentDetailBinding.bind(view)

        viewModel.uiState.onEach { state ->
            applyState(state, binding)
        }.launchRepeatingOnLifecycle(viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun applyState(
        state: DetailUiState,
        binding: FragmentDetailBinding
    ) {
        if (state is DetailUiState.Success) {
            val detail = state.detail

            binding.ownerIconView.load(detail.ownerIconUrl)
            binding.nameView.text = detail.name
            binding.languageView.text = detail.getLanguageText(requireContext())
            binding.starsView.text = detail.getStarsText(requireContext())
            binding.watchersView.text = detail.getWatchersText(requireContext())
            binding.forksView.text = detail.getForksText(requireContext())
            binding.openIssuesView.text = detail.getOpenIssuesText(requireContext())
        }
    }
}
