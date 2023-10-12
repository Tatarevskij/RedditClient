package com.example.redditclient.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.redditclient.GlideApp
import com.example.redditclient.R


import com.example.redditclient.databinding.FragmentLinkDetailsBinding
import com.example.redditclient.databinding.LinkItemBinding
import com.example.redditclient.entity.*
import com.example.redditclient.presentation.CommentItemViewLight
import com.example.redditclient.presentation.MainActivity
import com.example.redditclient.presentation.MainViewModel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


class LinkDetailsFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private val args: LinkDetailsFragmentArgs by navArgs()
    private var _binding: FragmentLinkDetailsBinding? = null
    private val binding get() = _binding!!
    private var linkIsSaved = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinkDetailsBinding.inflate(inflater, container, false)
        setBtnPanel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadLinkById(args.linkId, 5)
        binding.commentsLayout.setOnClickListener {
            val action =
                LinkDetailsFragmentDirections.actionGlobalCommentsLightFragment(
                    args.linkId
                )
            findNavController().navigate(action)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.linkByIdFlow.collect { LinkWithComments ->

                val link = LinkWithComments?.first
                val comments = LinkWithComments?.second

                if (comments?.isNotEmpty() == true) {
                    comments.forEach {
                        setCommentToView(it)
                    }
                } else binding.commentsLayout.removeAllViews()

                if (link != null) {
                    val isReposted = !link.data.crosspostParentList.isNullOrEmpty()
                    val mediaUrl = if (isReposted) {
                        getMediaUrl(link.data.crosspostParentList!![0])
                    } else getMediaUrl(link.data)

                    linkIsSaved = link.data.saved == true

                    with(binding) {
                        repostLayout.visibility = View.GONE
                        selftext.visibility = View.GONE
                        video.visibility = View.GONE
                        image.visibility = View.GONE
                        text.visibility = View.GONE
                        when {
                            !mediaUrl.isNullOrEmpty() && !isVideo -> {
                                image.visibility = View.VISIBLE

                                GlideApp.with(this@LinkDetailsFragment)
                                    .load(mediaUrl)
                                    .fitCenter()
                                    .into(image)
                            }

                            !mediaUrl.isNullOrEmpty() && isVideo -> {
                                val url = mediaUrl.substringBeforeLast("?")
                                val videoControl =
                                    MediaController(video.context)
                                video.visibility = View.VISIBLE
                                video.setMediaController(videoControl)
                                video.setVideoPath(url)
                                video.start()
                                video.setOnClickListener {
                                    if (!video.isPlaying) video.start()
                                    else video.pause()
                                }
                            }
                        }

                        if (!link.data.selftext.isNullOrEmpty()) {
                            selftext.visibility = View.VISIBLE
                            selftext.text = link.data.selftext
                        }

                        if (link.data.srDetail?.userIsSubscriber == true) subscribed.setImageResource(
                            R.drawable.subscribed
                        )
                        title.text = link.data.title
                        commentsNumber.text = link.data.numComments.toString()
                        author.text = link.data.author

                        if (isReposted) {
                            repostLayout.visibility = View.VISIBLE
                            repostedFrom.text = link.data.crosspostParentList!![0].subreddit
                        }
                        binding.commentsNumber

                        saveToDefault(binding)
                        saveLinkBtn.setOnClickListener {
                            save(this, link.data.name)
                        }
                        deleteLinkBtn.setOnClickListener {
                            save(this, link.data.name)
                        }
                    }
                }

                binding.returnBtn.setOnClickListener {
                    findNavController().navigate(R.id.action_global_linksFragment)

                }
            }
        }
    }

    private fun getMediaUrl(data: Data): String? {
        if (data.preview?.images?.get(0)?.variants?.mp4 != null) {
            return try {
                isVideo = false
                data.urlOverriddenByDest
            } catch (ex: Exception) {
                println(ex)
                null
            }
        }
        if (data.media?.redditVideo?.fallbackUrl != null) {
            return try {
                isVideo = true
                data.media!!.redditVideo!!.fallbackUrl
            } catch (ex: Exception) {
                println(ex)
                null
            }
        }
        if (data.media?.redditVideo?.fallbackUrl == null) {
            return try {
                isVideo = false
                data.preview!!.images[0].resolutions[2].url
            } catch (ex: Exception) {
                println(ex)
                null
            }
        }
        return null
    }

    private fun save (binding: FragmentLinkDetailsBinding, name: String, linkId: String = "") {
        when (linkIsSaved) {
            true -> {
                binding.deleteLinkBtn.isVisible = false
                binding.saveLinkBtn.isVisible = true
                onUnSaveClick(name)
                linkIsSaved = false
            }
            false -> {
                binding.deleteLinkBtn.isVisible = true
                binding.saveLinkBtn.isVisible = false
                onSaveClick(name, linkId)
                linkIsSaved = true
            }
        }
    }
    private fun saveToDefault(binding: FragmentLinkDetailsBinding) {
        binding.deleteLinkBtn.isVisible = linkIsSaved
        binding.saveLinkBtn.isVisible = !linkIsSaved
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

    private suspend fun setCommentToView(comment: Comment) {
        val view = CommentItemViewLight(
            this@LinkDetailsFragment.requireContext(),
            { _, _ -> onMoreCommentsClick() },
            { id, linkId -> onSaveClick(id, linkId) },
            { id -> onUnSaveClick(id) })
        val userIcon = getUserIconUrl(comment)
        view.setComment(comment, userIcon)
        binding.commentsLayout.addView(view)
        checkReplies(comment)
    }

    private suspend fun checkReplies(comment: Comment) {
        if (comment.data?.replies == null) return
        comment.data?.replies?.data?.children?.forEach {
            setCommentToView(it)
        }
    }

    private suspend fun getUserIconUrl(comment: Comment): String? {
        return comment.data?.authorFullName?.let { viewModel.getUserPureById(it) }?.profileImg
    }

    private fun onMoreCommentsClick() {
    }

    private fun onSaveClick(id: String, linkId: String = "") {
        viewModel.saveById(id, linkId)
    }

    private fun onUnSaveClick(id: String) {
        viewModel.unSaveById(id)
    }

    companion object {
        private var isVideo = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}