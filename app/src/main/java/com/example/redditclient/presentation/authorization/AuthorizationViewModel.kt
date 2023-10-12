package com.example.redditclient.presentation.authorization

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redditclient.ACCESS_TOKEN
import com.example.redditclient.REFRESH_TOKEN
import com.example.redditclient.authorization.AppAuth
import com.example.redditclient.authorization.AuthRepository
import com.example.redditclient.authorization.TokenStorage

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    val tokenPrefs: SharedPreferences,
    private val appAuth: AppAuth,
    application: Application
) : AndroidViewModel(application)  {
    private val authRepository = AuthRepository()
    private val authService: AuthorizationService = AuthorizationService(getApplication())
    private val openAuthPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    private val authSuccessEventChannel = Channel<Unit>(Channel.BUFFERED)

    val openAuthPageFlow: Flow<Intent>
        get() = openAuthPageEventChannel.receiveAsFlow()

    val authSuccessFlow: Flow<Unit>
        get() = authSuccessEventChannel.receiveAsFlow()

    fun onAuthCodeReceived(tokenRequest: TokenRequest) {
        viewModelScope.launch {
            runCatching {
                authRepository.performTokenRequest(
                    authService = authService,
                    tokenRequest = tokenRequest
                )
            }.onSuccess {
                authSuccessEventChannel.send(Unit)
                setTokenToSP()
            }.onFailure {
                println(it.message)
               // Toast.makeText(getApplication<Application>(), it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openLoginPage() {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        val authRequest = AppAuth.getAuthRequest()

        val openAuthPageIntent = authService.getAuthorizationRequestIntent(
            authRequest,
            customTabsIntent
        )
        openAuthPageEventChannel.trySendBlocking(openAuthPageIntent)
    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }

    private fun setTokenToSP() {
        tokenPrefs.edit()
            .putString(ACCESS_TOKEN, TokenStorage.accessToken)
            .putString(REFRESH_TOKEN, TokenStorage.refreshToken)
            .apply()
    }
}