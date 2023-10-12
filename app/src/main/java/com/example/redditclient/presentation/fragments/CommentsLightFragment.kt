package com.example.redditclient.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.redditclient.R
import com.example.redditclient.databinding.FragmentCommentsLightBinding
import com.example.redditclient.entity.Comment
import com.example.redditclient.entity.Link
import com.example.redditclient.presentation.CommentItemViewLight
import com.example.redditclient.presentation.MainActivity
import com.example.redditclient.presentation.MainViewModel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class CommentsLightFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentCommentsLightBinding? = null
    private val binding get() = _binding!!
    private val args: CommentsLightFragmentArgs by navArgs()
    private var nextCommentsIds: MutableList<String> = mutableListOf()
    private val commentsToLoadMore = 20
    private var commentsToLoad: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsLightBinding.inflate(inflater, container, false)
        setBtnPanel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadLinkById(args.linkId, 20)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.linkByIdFlow.collect { LinkWithComments ->
                setLinkDetails(LinkWithComments?.first)
                loadComments(LinkWithComments?.second)
            }
        }
    }

    private fun setLinkDetails(link: Link?) {
        binding.title.text = link?.data?.title
    }

    private suspend fun loadComments(comments: List<Comment>?, index: Int? = null) {
        if (comments?.isNotEmpty() == true) {
            comments.forEach {
                if (it.kind == "more" && it.data?.depth == 0) {
                    nextCommentsIds = it.data?.children!!.toMutableList()
                }
                setCommentToView(it, index)
            }
        } else binding.commentsLayout.removeAllViews()
        if (nextCommentsIds.isNotEmpty() && commentsToLoad.isEmpty()) getMoreCommentsBtnActive()
    }

    private suspend fun setCommentToView(comment: Comment, index: Int? = null) {
        val view = CommentItemViewLight(
            this@CommentsLightFragment.requireContext(),
            { children, view -> onMoreCommentsClick(children, view) },
            { id, linkId -> onSaveClick(id, linkId) },
            { id -> onUnSaveClick(id) }
        )
        val userIcon = getUserIconUrl(comment)

        if (comment.kind == "more" && comment.data?.depth != 0) {
            view.setMoreCommentsBtn(comment)
            binding.commentsLayout.addView(view)
            val currentIndex = binding.commentsLayout.indexOfChild(view)
            val children = comment.data?.children
            view.setIndexForReplyViews(currentIndex, children!!)
            return
        }
        view.setComment(comment, userIcon)
        if (index is Int) {
            binding.commentsLayout.addView(view, index)
            checkReplies(comment)
            return
        }
        binding.commentsLayout.addView(view)
        checkReplies(comment)
    }

    private suspend fun checkReplies(comment: Comment) {
        if (comment.data?.replies == null) return
        comment.data?.replies?.data?.children?.forEach {
            setCommentToView(it)
        }
    }

    private fun getMoreCommentsBtnActive() {
        if (nextCommentsIds.isEmpty()) return
        binding.moreBtn.visibility = View.VISIBLE

        binding.moreBtn.setOnClickListener {
            commentsToLoad = nextCommentsIds.take(commentsToLoadMore).toMutableList()
            nextCommentsIds.removeAll(commentsToLoad)
            viewLifecycleOwner.lifecycleScope.launch {
                /*viewModel.getMoreChildren(args.linkId, commentsToLoad).apply {
                    loadComments(this!!)
                }*/
                while (commentsToLoad.isNotEmpty()) {
                    commentsToLoad.removeFirst().apply {
                        viewModel.getChildrenComments(args.linkId.substringAfter("t3_"), this)
                            .apply {
                                loadComments(this!!)
                            }
                    }
                }
            }
            binding.moreBtn.isVisible = false
        }
    }

    private fun onMoreCommentsClick(
        children: List<String>,
        view: View
    ) {
        var index = binding.commentsLayout.indexOfChild(view)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getMoreChildren(args.linkId, children).apply {
                this?.forEach {
                    setCommentToView(it, index)
                    index++
                }
            }
        }
    }

    private suspend fun getUserIconUrl(comment: Comment): String? {
        return comment.data?.authorFullName?.let { viewModel.getUserPureById(it) }?.profileImg
    }

    private fun onSaveClick(id: String, linkId: String) {
        viewModel.saveById(id, linkId)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}