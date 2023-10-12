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
import com.example.redditclient.databinding.FragmentSubredditBinding
import com.example.redditclient.entity.Subreddit
import com.example.redditclient.presentation.MainActivity
import com.example.redditclient.presentation.MainViewModel
import com.example.redditclient.presentation.adapters.LinksPagedAdapter
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class SubredditFragment : Fragment(R.layout.fragment_subreddit) {
    private val viewModel: MainViewModel by activityViewModels()
    private val binding by viewBinding(FragmentSubredditBinding::bind)
    private var isSubscriber: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linksPagedAdapter = LinksPagedAdapter(
            { name -> onItemClick(name) },
            { name -> onUserClick(name) },
            { name -> onSrClick(name) }
        )
        setBtnPanel()
        binding.recyclerView.adapter = linksPagedAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.subredditFlow.collect {
                if (it != null) {
                    setImages(it)
                    setData(it, linksPagedAdapter)
                    setSubscribeBtn()
                }
            }
        }
    }

    private fun setImages(subreddit: Subreddit) {
        println(subreddit.communityIcon)
        if (!subreddit.bannerBackgroundImage.isNullOrEmpty()) {
            GlideApp.with(this)
                .asBitmap()
                .load(subreddit.bannerBackgroundImage)
                .centerCrop()
                .into(binding.srImg)
            binding.srImg.isVisible = true
            binding.margin.isVisible = true
        } else {
            binding.margin.isVisible = false
            binding.srImg.isVisible = false
        }

        GlideApp.with(this)
            .asBitmap()
            .load(subreddit.communityIcon)
            .circleCrop()
            .into(binding.icon)
    }

    private fun setData(subreddit: Subreddit, linksPagedAdapter: LinksPagedAdapter) {
        binding.srName.text = subreddit.title
        binding.userName.text = subreddit.publicDescription
        isSubscriber = subreddit.userIsSubscriber
        viewModel.loadLinksBySrName(linksPagedAdapter, subreddit.displayNamePrefixed.substringAfter("/"))
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
            SubredditFragmentDirections.actionSubredditFragmentToLinkDetailsFragment(name)
        findNavController().navigate(action)
    }

    private fun onUserClick(name: String) {
        viewModel.loadUserFull(name)
        val action =
            SubredditFragmentDirections.actionGlobalUserDetailsFragment()
        findNavController().navigate(action)
    }

    private fun onSrClick(name: String) {
        viewModel.loadSubredditDetails(name)
        findNavController().navigate(R.id.action_global_subredditFragment)
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