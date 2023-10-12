package com.example.redditclient.presentation.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.redditclient.R
import com.example.redditclient.databinding.FragmentLinksBinding
import com.example.redditclient.presentation.MainActivity
import com.example.redditclient.presentation.MainViewModel
import com.example.redditclient.presentation.adapters.LinksPagedAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.ref.WeakReference


class LinksFragment : Fragment(R.layout.fragment_links) {
    private val viewModel: MainViewModel by activityViewModels()
    private val binding by viewBinding(FragmentLinksBinding::bind)
    private val linksPagedAdapter = LinksPagedAdapter(
        { name -> onItemClick(name) },
        { name -> onUserClick(name) },
        { name -> onSrClick(name)},
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val state = viewModel.state
        setBtnPanel()
        viewModel.setUserName()
        viewModel.getToken()
        binding.recyclerView.adapter = linksPagedAdapter

        binding.popularLinksBtn.setOnClickListener {
            viewModel.loadBestLinks(linksPagedAdapter)
            binding.newLinksBtn.setTextColor(getColor(this.requireContext(), R.color.black))
            binding.popularLinksBtn.setTextColor(
                getColor(
                    this.requireContext(),
                    R.color.purple_600
                )
            )
            binding.popularLinksBtn.setTypeface(null, Typeface.BOLD)
            binding.newLinksBtn.setTypeface(null, Typeface.NORMAL)
        }

        binding.newLinksBtn.setOnClickListener {
            viewModel.loadNewLinks(linksPagedAdapter)
            binding.popularLinksBtn.setTextColor(getColor(this.requireContext(), R.color.black))
            binding.newLinksBtn.setTextColor(getColor(this.requireContext(), R.color.purple_600))
            binding.newLinksBtn.setTypeface(null, Typeface.BOLD)
            binding.popularLinksBtn.setTypeface(null, Typeface.NORMAL)
        }
        binding.newLinksBtn.performClick()

        binding.searchBtn.setOnClickListener {
            val query = binding.input.text.toString()
            viewModel.loadLinksByQuery(linksPagedAdapter, query)
        }

        binding.swipeRefresh.setOnRefreshListener {
            state.isOnLineCheck()
            linksPagedAdapter.refresh()
        }

        viewModel.isLoading.onEach {
            binding.swipeRefresh.isRefreshing = it
            binding.recyclerView.scrollToPosition(0)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setBtnPanel() {
        val mainActivity: WeakReference<MainActivity> =
            WeakReference(requireActivity() as MainActivity)
        mainActivity.get()!!.binding.buttonPanel.isVisible = true
        mainActivity.get()!!.binding.subredditsBtn.isEnabled = false
        mainActivity.get()!!.binding.favoritesBtn.isEnabled = true
        mainActivity.get()!!.binding.profileBtn.isEnabled = true
        mainActivity.get()!!.binding.subredditsBtn.setImageDrawable(
            ContextCompat.getDrawable(
                this.requireContext(),
                R.drawable.subredditbtn_clicked
            )
        )
        mainActivity.get()!!.binding.favoritesBtn.setImageDrawable(
            ContextCompat.getDrawable(
                this.requireContext(),
                R.drawable.favoritesbtn
            )
        )
        mainActivity.get()!!.binding.profileBtn.setImageDrawable(
            ContextCompat.getDrawable(
                this.requireContext(),
                R.drawable.profilebtn
            )
        )
    }

    private fun onItemClick(name: String) {
        val action =
            LinksFragmentDirections.actionLinksFragmentToLinkDetailsFragment(
                name
            )
        findNavController().navigate(action)
    }

    private fun onUserClick(name: String) {
        viewModel.loadUserFull(name)
        val action =
            LinksFragmentDirections.actionLinksFragmentToUserDetailsFragment()
        findNavController().navigate(action)
    }

    private fun onSrClick(name: String) {
        viewModel.loadSubredditDetails(name)
        findNavController().navigate(R.id.action_global_subredditFragment)
    }

}