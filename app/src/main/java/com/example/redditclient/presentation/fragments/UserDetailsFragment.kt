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
import com.example.redditclient.GlideApp
import com.example.redditclient.R
import com.example.redditclient.databinding.FragmentUserDetailsBinding
import com.example.redditclient.entity.UserFull
import com.example.redditclient.presentation.MainActivity
import com.example.redditclient.presentation.MainViewModel
import com.example.redditclient.presentation.adapters.LinksAdapter
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class UserDetailsFragment : Fragment(R.layout.fragment_user_details) {
    private val viewModel: MainViewModel by activityViewModels()
    private val binding by viewBinding(FragmentUserDetailsBinding::bind)
    private var isSubscriber: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linksBySrNameAdapter = LinksAdapter({ name -> onItemClick(name) },
            { name -> onUserClick(name) },
            { name -> onSrClick(name)},
            { id -> onUnSaveClick(id) },
            { id -> onSaveClick(id) })
        setBtnPanel()
        binding.recyclerView.adapter = linksBySrNameAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userFlow.collect {
                if (it != null) {
                    setImages(it)
                    setSubscribeBtn()
                    setData(it, linksBySrNameAdapter)
                }
            }
        }
    }

    private fun setImages(user: UserFull) {
        if (user.subreddit.bannerImg.isNotBlank()) {
            GlideApp.with(this)
                .asBitmap()
                .load(user.subreddit.bannerImg)
                .into(binding.srImg)
            binding.srImg.isVisible = true
            binding.margin.isVisible = true
        } else {
            binding.margin.isVisible = false
            binding.srImg.isVisible = false
        }

        GlideApp.with(this)
            .asBitmap()
            .load(user.iconImg)
            .circleCrop()
            .into(binding.userIcon)
    }

    private suspend fun setData(user: UserFull, adapter: LinksAdapter) {
        binding.srName.text = user.subreddit.title
        binding.userName.text = user.userName
        isSubscriber = user.subreddit.userIsSubscriber
        viewModel.loadUserLinks(user.userName)
        viewModel.linksFlow.collect {
            if (!it.isNullOrEmpty()) adapter.submitList(it)
        }
    }

    private fun setSubscribeBtn() {
        if (isSubscriber) {
            isSubscribed()
        } else {
            isUnSubscribed()
        }
        binding.followBtn.setOnClickListener {
            if (isSubscriber) {
                isUnSubscribed()
            } else isSubscribed()
        }
    }

    private fun isSubscribed() {
        binding.subscribeText.text = requireContext().getText(R.string.unsubscribe)
        binding.subscribeIcon.setImageDrawable(
            ContextCompat.getDrawable(
                this.requireContext(),
                R.drawable.subscribed_white
            )
        )
        isSubscriber = true
    }

    private fun isUnSubscribed() {
        binding.subscribeText.text = requireContext().getText(R.string.subscribe)
        binding.subscribeIcon.setImageDrawable(
            ContextCompat.getDrawable(
                this.requireContext(),
                R.drawable.unsubscribed_white
            )
        )
        isSubscriber = false
    }

    private fun onItemClick(name: String) {
        val action =
            UserDetailsFragmentDirections.actionUserDetailsFragmentToLinkDetailsFragment(name)
        findNavController().navigate(action)
    }

    private fun onUserClick(name: String) {
        viewModel.loadUserFull(name)
        val action =
            UserDetailsFragmentDirections.actionGlobalUserDetailsFragment()
        findNavController().navigate(action)
    }

    private fun onSrClick(name: String) {
        viewModel.loadSubredditDetails(name)
        findNavController().navigate(R.id.action_global_subredditFragment)
    }

    private fun onSaveClick(id: String) {
        viewModel.saveById(id)
    }

    private fun onUnSaveClick(id: String) {
        viewModel.unSaveById(id)
    }

    private fun setBtnPanel() {
        val mainActivity: WeakReference<MainActivity> =
            WeakReference(requireActivity() as MainActivity)
        mainActivity.get()!!.binding.buttonPanel.isVisible = true
        mainActivity.get()!!.binding.subredditsBtn.isEnabled = true
        mainActivity.get()!!.binding.favoritesBtn.isEnabled = true
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
}