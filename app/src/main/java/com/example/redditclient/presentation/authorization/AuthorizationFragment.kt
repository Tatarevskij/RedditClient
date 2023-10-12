package com.example.redditclient.presentation.authorization

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.redditclient.R
import com.example.redditclient.authorization.utils.launchAndCollectIn
import com.example.redditclient.databinding.FragmentAuthorizationBinding
import com.example.redditclient.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import java.lang.ref.WeakReference

@AndroidEntryPoint
class AuthorizationFragment : Fragment(R.layout.fragment_authorization) {
    private val viewModel: AuthorizationViewModel by activityViewModels()
    private val binding by viewBinding(FragmentAuthorizationBinding::bind)
    private var mainActivity: WeakReference<MainActivity>? = null
    private val getAuthResponse = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val dataIntent = it.data ?: return@registerForActivityResult
        handleAuthResponseIntent(dataIntent)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = WeakReference(requireActivity() as MainActivity)

        mainActivity!!.get()!!.binding.buttonPanel.isVisible = false

        binding.loginBtn.setOnClickListener { viewModel.openLoginPage() }
        viewModel.openAuthPageFlow.launchAndCollectIn(viewLifecycleOwner) {
            getAuthResponse.launch(it)
        }

        viewModel.authSuccessFlow.launchAndCollectIn(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_authorizationFragment_to_subredditsFragment)
        }
    }

    private fun handleAuthResponseIntent(intent: Intent) {
        // пытаемся получить ошибку из ответа. null - если все ок
        val exception = AuthorizationException.fromIntent(intent)
        // пытаемся получить запрос для обмена кода на токен, null - если произошла ошибка
        val tokenExchangeRequest = AuthorizationResponse.fromIntent(intent)
            ?.createTokenExchangeRequest()
        when {
            // авторизация завершались ошибкой
            exception != null -> Toast.makeText(this.requireContext(), exception.message, Toast.LENGTH_SHORT).show()
            // авторизация прошла успешно, меняем код на токен
            tokenExchangeRequest != null ->
                viewModel.onAuthCodeReceived(tokenExchangeRequest)
        }
    }
}