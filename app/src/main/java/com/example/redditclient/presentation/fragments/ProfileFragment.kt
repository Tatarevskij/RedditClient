package com.example.redditclient.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.redditclient.GlideApp
import com.example.redditclient.R
import com.example.redditclient.databinding.FragmentProfileBinding
import com.example.redditclient.entity.Me
import com.example.redditclient.presentation.MainActivity
import com.example.redditclient.presentation.MainViewModel
import kotlinx.coroutines.launch
import shadow.com.google.auto.common.AnnotationValues.getString
import java.lang.ref.WeakReference

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val viewModel: MainViewModel by activityViewModels()
    private val binding by viewBinding(FragmentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBtnPanel()
        setBtnActions()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadMyInfo().apply {
                if (this != null) {
                    loadMyImage(this)
                    setMyData(this)
                }
            }
        }
    }

    private fun loadMyImage(me: Me) {
        GlideApp.with(this@ProfileFragment)
            .load(me.iconImg)
            .circleCrop()
            .into(binding.icon)
    }

    private fun setMyData(me: Me) {
        binding.userName.text = me.name
        binding.totalFriends.text =
            String.format(requireContext().getString(R.string.friends_all), me.numFriends)
    }

    private fun setBtnActions() {
        binding.friendsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_friendsFragment)
        }
        binding.logoutBtn.setOnClickListener {
            viewModel.removeToken()
            findNavController().navigate(R.id.action_global_authorizationFragment)
        }
        binding.clearDbBtn.setOnClickListener {
            viewModel.clearDb()
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
}