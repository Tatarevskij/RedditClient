package com.example.redditclient.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.redditclient.ONBOARD_SCR
import com.example.redditclient.R
import com.example.redditclient.databinding.OnboardLayoutBinding

class OnBoardScreenElement : Fragment(R.layout.onboard_layout) {
    private val binding by viewBinding(OnboardLayoutBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.onboard_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ONBOARD_SCR) }?.apply {
            when (getInt(ONBOARD_SCR)) {
                1 -> setFirstScreen()
                2 -> setSecondScreen()
                3 -> setThirdScreen()
            }
        }
        binding.nextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_global_authorizationFragment)
        }
    }

    private fun setFirstScreen() {
        binding.text.text = getString(R.string.ob1_text)
        binding.title.text = getString(R.string.ob1_title)
        binding.nextBtn.text = getString(R.string.skip_btn)
        binding.image.setImageDrawable(AppCompatResources.getDrawable(this.requireContext(), R.drawable.onboard1))

    }
    private fun setSecondScreen() {
        binding.text.text = getString(R.string.ob2_text)
        binding.title.text = getString(R.string.ob2_title)
        binding.nextBtn.text = getString(R.string.skip_btn)
        binding.image.setImageDrawable(AppCompatResources.getDrawable(this.requireContext(), R.drawable.onboard2))
    }
    private fun setThirdScreen() {
        binding.text.text = getString(R.string.ob3_text)
        binding.title.text = getString(R.string.ob3_title)
        binding.nextBtn.text = getString(R.string.ready_btn)
        binding.image.setImageDrawable(AppCompatResources.getDrawable(this.requireContext(), R.drawable.onboard3))

    }
}