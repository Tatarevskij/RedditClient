package com.example.redditclient.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.redditclient.GlideApp
import com.example.redditclient.databinding.CommentSavedItemBinding
import com.example.redditclient.entity.Comment
import org.jsoup.Jsoup
import java.util.*
import javax.inject.Inject

class CommentsAdapter(
    private val onSaveClick: (String, String) -> Unit,
    private val onUnSaveClick: (String) -> Unit,
) : ListAdapter<Comment, CommentViewHolder>(CommentsDifutiilCallback()) {
    private var isSaved = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = CommentSavedItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = getItem(position)
        with(holder.binding) {
            isSaved = comment.data?.saved == true
            userName.text = comment.data?.author
            setDate(comment, this)
            setText(comment, this)
            saveToDefault(this)
            saveComBtn.setOnClickListener {
                save(this, comment.data?.name!!, comment.data?.linkId!!)
            }
            deleteComBtn.setOnClickListener {
                save(this, comment.data?.name!!)
            }
        }
    }

    private fun setUserIcon(userIcon: String?, binding: CommentSavedItemBinding) {
        GlideApp.with(binding.icon)
            .asBitmap()
            .load(userIcon)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.icon)
    }

    private fun setDate(comment: Comment, binding: CommentSavedItemBinding) {
        val time = comment.data?.createdUtc ?: return
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = time.toLong() * 1000
        binding.createdAt.text =
            android.text.format.DateFormat.format("HH:mm, dd MMM yyyy", calendar).toString()
    }

    private fun setText(comment: Comment, binding: CommentSavedItemBinding) {
        val doc = comment.data?.bodyHtml?.let { Jsoup.parse(it) }
        binding.commentText.text = doc?.body()?.text()
    }

    private fun save (binding: CommentSavedItemBinding, name: String, linkId: String = "") {
        when (isSaved) {
            true -> {
                binding.deleteComBtn.isVisible = false
                binding.saveComBtn.isVisible = true
                onUnSaveClick(name)
                isSaved = false
            }
            false -> {
                binding.deleteComBtn.isVisible = true
                binding.saveComBtn.isVisible = false
                onSaveClick(name, linkId)
                isSaved = true
            }
        }
    }

    private fun saveToDefault(binding: CommentSavedItemBinding) {
        binding.deleteComBtn.isVisible = isSaved
        binding.saveComBtn.isVisible = !isSaved
    }

}

class CommentsDifutiilCallback : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem.data?.id == newItem.data?.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem == newItem
    }
}

class CommentViewHolder @Inject constructor
    (val binding: CommentSavedItemBinding) :
    RecyclerView.ViewHolder(binding.root)