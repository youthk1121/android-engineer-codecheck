/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import jp.co.yumemi.android.code_check.databinding.FragmentDetailBinding

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val args: DetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("検索した日時", args.searchDate.toString())

        val binding = FragmentDetailBinding.bind(view)

        val detail = args.detail

        binding.ownerIconView.load(detail.ownerIconUrl)
        binding.nameView.text = detail.name
        binding.languageView.text = detail.getLanguageText(requireContext())
        binding.starsView.text = "${detail.stargazersCount} stars"
        binding.watchersView.text = "${detail.watchersCount} watchers"
        binding.forksView.text = "${detail.forksCount} forks"
        binding.openIssuesView.text = "${detail.openIssuesCount} open issues"
    }
}
