package com.example.redditclient.presentation.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.redditclient.ACCESS_TOKEN
import com.example.redditclient.ONBOARD_SCR
import com.example.redditclient.R
import com.example.redditclient.REFRESH_TOKEN
import com.example.redditclient.authorization.TokenStorage
import com.example.redditclient.databinding.OnboardLayoutBinding
import com.example.redditclient.presentation.MainActivity
import com.example.redditclient.presentation.fragments.OnBoardScreenElement
import java.lang.ref.WeakReference

class OnBoardingFragment : Fragment(R.layout.fragment_on_boarding) {
    private val viewModel: AuthorizationViewModel by activityViewModels()
    private var mainActivity: WeakReference<MainActivity>? = null
    private lateinit var demoCollectionAdapter: OnBoardingAdapter
    private lateinit var viewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = WeakReference(requireActivity() as MainActivity)
        mainActivity!!.get()!!.binding.buttonPanel.isVisible = false

        if (!viewModel.tokenPrefs.getString(ACCESS_TOKEN, null).isNullOrBlank()){
            TokenStorage.accessToken = viewModel.tokenPrefs.getString(ACCESS_TOKEN, null)
            println(viewModel.tokenPrefs.getString(REFRESH_TOKEN, null))
            findNavController().navigate(R.id.action_global_linksFragment)
        }

        demoCollectionAdapter = OnBoardingAdapter(this)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = demoCollectionAdapter
    }
}

class OnBoardingAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        val fragment = OnBoardScreenElement()
        fragment.arguments = Bundle().apply {
            putInt(ONBOARD_SCR, position + 1)
        }
        return fragment
    }
}