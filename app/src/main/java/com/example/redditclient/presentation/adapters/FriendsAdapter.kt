package com.example.redditclient.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.redditclient.GlideApp
import com.example.redditclient.databinding.FriendItemBinding
import com.example.redditclient.entity.UserPure
import javax.inject.Inject

class FriendsAdapter @Inject constructor(
    private val onClick: (UserPure) -> Unit,
) : ListAdapter<UserPure, FriendViewHolder>(FriendsDifutiilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = FriendItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = getItem(position)
        setIcon(friend!!, holder)
        holder.binding.name.text = friend.name
        holder.binding.itemLayout.setOnClickListener {
            onClick(friend)
        }
    }

    private fun setIcon(friend: UserPure, holder: FriendViewHolder) {
        GlideApp.with(holder.itemView)
            .asBitmap()
            .load(friend.profileImg)
            .circleCrop()
            .into(holder.binding.icon)
    }
}

class FriendsDifutiilCallback : DiffUtil.ItemCallback<UserPure>(){
    override fun areItemsTheSame(oldItem: UserPure, newItem: UserPure): Boolean {
       return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: UserPure, newItem: UserPure): Boolean {
        return oldItem == newItem
    }

}


class FriendViewHolder @Inject constructor
    (val binding: FriendItemBinding) :
    RecyclerView.ViewHolder(binding.root)