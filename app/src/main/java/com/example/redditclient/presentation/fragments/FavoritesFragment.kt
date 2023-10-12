package com.example.redditclient.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.redditclient.R
import com.example.redditclient.databinding.FragmentFavoritesBinding
import com.example.redditclient.entity.Comment
import com.example.redditclient.entity.Link
import com.example.redditclient.presentation.MainActivity
import com.example.redditclient.presentation.MainViewModel
import com.example.redditclient.presentation.adapters.CommentsAdapter
import com.example.redditclient.presentation.adapters.LinksAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {
    private val viewModel: MainViewModel by activityViewModels()
    private val binding by viewBinding(FragmentFavoritesBinding::bind)
    private val linksAdapter = LinksAdapter(
        { name -> onItemClick(name) },
        { name -> onUserClick(name) },
        { name -> onSrClick(name) },
        { id -> onUnSaveClick(id) },
        { id -> onSaveClick(id) })
    private val commentsAdapter = CommentsAdapter(
        { id, linkId -> onSaveClick(id, linkId) },
        { id -> onUnSaveClick(id) })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val state = viewModel.state
        super.onViewCreated(view, savedInstanceState)
        val emptyCommentList = listOf<Comment>()
        val emptyLinkList = listOf<Link>()
        viewModel.setUserName()
        setBtnPanel()
        setSwitchListeners()
        setDefault()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.linksFlow.collect {
                if (!it.isNullOrEmpty()) linksAdapter.submitList(it)
                else linksAdapter.submitList(emptyLinkList)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.commentsFlow.collect {
                if (!it.isNullOrEmpty()) commentsAdapter.submitList(it)
                else commentsAdapter.submitList(emptyCommentList)
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            state.isOnLineCheck()
            viewModel.refreshApi()
            setDefault()
        }

        viewModel.isLoading.onEach {
            binding.swipeRefresh.isRefreshing = it
            binding.recyclerView.scrollToPosition(0)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun onItemClick(id: String) {
        val action =
            FavoritesFragmentDirections.actionFavoritesFragmentToLinkDetailsFragment(
                id
            )
        findNavController().navigate(action)
    }

    private fun onUserClick(name: String) {
        viewModel.loadUserFull(name)
        findNavController().navigate(R.id.action_global_userDetailsFragment)
    }

    private fun onSrClick(name: String) {
        viewModel.loadSubredditDetails(name)
        findNavController().navigate(R.id.action_global_subredditFragment)
    }

    private fun onSaveClick(id: String, linkId: String = "") {
        viewModel.saveById(id, linkId)
    }

    private fun onUnSaveClick(id: String) {
        viewModel.unSaveById(id)
    }

    private fun setDefault() {
        binding.recyclerView.adapter = linksAdapter
        viewModel.loadMyLinks()
    }

    private fun setSwitchListeners() {
        setSrSwitchAlpha()
        setSavedSwitchAlpha()
        binding.srComSwitch.setOnCheckedChangeListener { _, _ ->
            checker()
            setSrSwitchAlpha()
        }
        binding.allSaveSwitch.setOnCheckedChangeListener { _, _ ->
            checker()
            setSavedSwitchAlpha()
        }
    }

    private fun checker() {
        val srSwitch = binding.srComSwitch
        val saveSwitch = binding.allSaveSwitch
        when {
            !srSwitch.isChecked && !saveSwitch.isChecked -> {
                println("all Subreddits")
                binding.recyclerView.adapter = linksAdapter
                viewModel.loadMyLinks()
                binding.recyclerView.scrollToPosition(0)
            }
            !srSwitch.isChecked && saveSwitch.isChecked -> {
                println("Saved subreddits")
                binding.recyclerView.adapter = linksAdapter
                viewModel.loadMySavedLinks()
                binding.recyclerView.scrollToPosition(0)
            }
            srSwitch.isChecked && saveSwitch.isChecked -> {
                println("Saved comments")
                binding.recyclerView.adapter = commentsAdapter
                viewModel.loadMySavedComments()
                binding.recyclerView.scrollToPosition(0)
            }
            srSwitch.isChecked && !saveSwitch.isChecked -> {
                println("all comments")
                binding.recyclerView.adapter = commentsAdapter
                viewModel.loadMyComments()
                binding.recyclerView.scrollToPosition(0)

            }
        }
    }

    private fun setSrSwitchAlpha() {
        if (binding.srComSwitch.isChecked) {
            binding.comText.alpha = 0F
            binding.srText.alpha = 1F
        } else {
            binding.comText.alpha = 1F
            binding.srText.alpha = 0F
        }
    }

    private fun setSavedSwitchAlpha() {
        if (binding.allSaveSwitch.isChecked) {
            binding.allText.alpha = 1F
            binding.savedText.alpha = 0F
        } else {
            binding.allText.alpha = 0F
            binding.savedText.alpha = 1F
        }
    }


    private fun setBtnPanel() {
        val mainActivity: WeakReference<MainActivity> =
            WeakReference(requireActivity() as MainActivity)
        mainActivity.get()!!.binding.buttonPanel.isVisible = true
        mainActivity.get()!!.binding.subredditsBtn.isEnabled = true
        mainActivity.get()!!.binding.favoritesBtn.isEnabled = false
        mainActivity.get()!!.binding.profileBtn.isEnabled = true

        mainActivity.get()!!.binding.subredditsBtn.setImageDrawable(
            ContextCompat.getDrawable(
                this.requireContext(),
                R.drawable.subredditbtn
            )
        )
        mainActivity.get()!!.binding.favoritesBtn.setImageDrawable(
            ContextCompat.getDrawable(
                this.requireContext(),
                R.drawable.favoritesbtn_clicked
            )
        )
        mainActivity.get()!!.binding.profileBtn.setImageDrawable(
            ContextCompat.getDrawable(
                this.requireContext(),
                R.drawable.profilebtn
            )
        )
    }
}