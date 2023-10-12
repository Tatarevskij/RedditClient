package com.example.redditclient.presentation.adapters


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.redditclient.GlideApp
import com.example.redditclient.Options
import com.example.redditclient.R
import com.example.redditclient.databinding.LinkItemBinding
import com.example.redditclient.entity.Data
import com.example.redditclient.entity.Link
import javax.inject.Inject

class LinksPagedAdapter @Inject constructor(
    private val onClick: (String) -> Unit,
    private val onUserClick: (String) -> Unit,
    private val onSrClick: (String) -> Unit
) : PagingDataAdapter<Link, LinkViewHolder>(LinksDifutiilCallback()) {
    private val options = Options()
    private var isReposted = false
    private var mediaUrl: String? = ""
    private var isVideo = false
    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        mediaUrl = ""
        if (getItem(position) != null) {
            val linkData = getItem(position)!!.data
            if (checkNSFW(linkData, holder.binding)) return
            isReposted = !linkData.crosspostParentList.isNullOrEmpty()
            if (isReposted) isReposted(linkData, holder.binding)
            else mediaUrl = getMediaUrl(linkData)

            if (mediaUrl.isNullOrBlank()) mediaUrl = linkData.preview?.images?.get(0)?.resolutions?.get(0)?.url

            setGallery(linkData)

            with(holder.binding) {
                setImage(this@with, mediaUrl)
                setData(linkData, this@with)
                if (linkData.srDetail?.userIsSubscriber == true) subscribed.setImageResource(R.drawable.subscribed)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val binding = LinkItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LinkViewHolder(binding)
    }

    private fun checkNSFW(linkData: Data, binding: LinkItemBinding): Boolean {
        if (linkData.thumbnail == "nsfw" && !options.NSFV) {
            binding.linkItemLayout.visibility = View.GONE
            binding.linkItemLayout.layoutParams = LinearLayout.LayoutParams(0, 0)
            return true
        }
        return false
    }

    private fun isReposted(linkData: Data, binding: LinkItemBinding) {
        binding.repostedFrom.isVisible = true
        binding.from.isVisible = true
        binding.repostedFrom.text = linkData.crosspostParentList!![0].subreddit
        getMediaUrl(linkData.crosspostParentList!![0])
    }

    private fun setData(linkData: Data, binding: LinkItemBinding) {
        binding.linkItemLayout.visibility = View.VISIBLE
        binding.fullViewLayout.isVisible = false
        binding.title.text = linkData.title
        binding.author.text = linkData.author
        binding.subreddit.text = linkData.subreddit
        binding.comments.text = linkData.numComments.toString()
        binding.contentText.text = linkData.selftext
        if (!linkData.selftext.isNullOrBlank()){
            binding.contentText.text = linkData.selftext
            binding.contentText.isVisible = true
        } else binding.contentText.isVisible = false
        binding.title.setOnClickListener {
            binding.fullViewLayout.isVisible = !binding.fullViewLayout.isVisible
        }
        binding.contentLayout.setOnClickListener {
            onClick(linkData.name)
        }
        binding.author.setOnClickListener {
            onUserClick(linkData.author!!)
        }
        binding.subreddit.setOnClickListener {
            onSrClick(linkData.subreddit!!)
        }
    }

    private fun setImage(binding: LinkItemBinding, mediaUrl: String?) {
        if (!mediaUrl.isNullOrEmpty()) {
            binding.image.isVisible = true
            GlideApp.with(binding.linkItemLayout)
                .load(mediaUrl)
                .into(binding.image)
        }
    }

    private fun getMediaUrl(data: Data): String? {
        if (data.preview?.images?.get(0)?.variants?.mp4 != null) {
            return try {
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
        if (data.preview?.images?.get(0)?.resolutions?.get(0)?.url != null) {
            return try {
                data.preview!!.images[0].resolutions[1].url
            } catch (ex: Exception) {
                println(ex)
                null
            }
        }
        if (!data.url.isNullOrEmpty()) {
            return try {
                data.url
            } catch (ex: Exception) {
                println(ex)
                null
            }
        }
        return null
    }
}

fun setGallery(linkData: Data) {
    //println(linkData.gallery?.images?.size)
}

class LinksDifutiilCallback : DiffUtil.ItemCallback<Link>() {
    override fun areItemsTheSame(oldItem: Link, newItem: Link): Boolean {
        return oldItem.data.name == newItem.data.name
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Link, newItem: Link): Boolean {
        return oldItem == newItem
    }
}

class LinkViewHolder @Inject constructor
    (val binding: LinkItemBinding) :
    RecyclerView.ViewHolder(binding.root)