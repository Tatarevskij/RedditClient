package com.example.redditclient.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.redditclient.R
import com.example.redditclient.databinding.FragmentFriendsBinding
import com.example.redditclient.entity.UserPure
import com.example.redditclient.presentation.MainActivity
import com.example.redditclient.presentation.MainViewModel
import com.example.redditclient.presentation.adapters.FriendsAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class FriendsFragment : Fragment(R.layout.fragment_friends) {
    private val viewModel: MainViewModel by activityViewModels()
    private val binding by viewBinding(FragmentFriendsBinding::bind)
    private val friendsAdapter = FriendsAdapter { user -> onFriendClick(user) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val state = viewModel.state
        viewModel.loadMyFriends()
        setBtnPanel()
        setHeader()
        binding.recyclerView.adapter = friendsAdapter

        val staggeredGridLayoutManager = GridLayoutManager(this@FriendsFragment.requireContext(), 2, LinearLayoutManager.VERTICAL,  false)
        binding.recyclerView.layoutManager = staggeredGridLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.friendsFlow.collect{
                friendsAdapter.submitList(it)
            }
        }
        viewModel.isLoading.onEach {
            binding.swipeRefresh.isRefreshing = it
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.swipeRefresh.setOnRefreshListener {
            state.isOnLineCheck()
            viewModel.loadMyFriends()
        }
    }

    private fun setBtnPanel() {
        val mainActivity: WeakReference<MainActivity> =
            WeakReference(requireActivity() as MainActivity)
        mainActivity.get()!!.binding.buttonPanel.isVisible = true
        mainActivity.get()!!.binding.subredditsBtn.isEnabled = true
        mainActivity.get()!!.binding.favoritesBtn.isEnabled = true
        mainActivity.get()!!.binding.profileBtn.isEnabled = false

        mainActivity.get()!!.binding.subredditsBtn.setImageDrawable(
            ContextCompat.getDrawable(
                this.requireContext(),
                R.drawable.subredditbtn
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
                R.drawable.profilebtn_clicked
            )
        )
    }

    private fun setHeader() {
        binding.returnBtn.setOnClickListener {
            findNavController().navigate(R.id.action_global_profileFragment)
        }
    }

    private fun onFriendClick(user: UserPure) {
        viewModel.loadUserFull(user.name)
        findNavController().navigate(R.id.action_global_userDetailsFragment)
    }

}