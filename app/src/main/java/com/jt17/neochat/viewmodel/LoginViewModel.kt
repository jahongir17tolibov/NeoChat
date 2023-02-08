package com.jt17.neochat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.jt17.neochat.repository.FireBaseUserLiveData

class LoginViewModel : ViewModel() {

    enum class AuthenticationState() {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATED
    }

    val authenticationState = FireBaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }

    }

}