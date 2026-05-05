package com.sokamn.trovami.presentation.introduction

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokamn.trovami.core.Event
import com.sokamn.trovami.domain.usecase.VerifyEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor() : ViewModel() {

    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = _navigateToLogin

    private val _navigateToSignUp = MutableLiveData<Event<Boolean>>()
    val navigateToSignUp: LiveData<Event<Boolean>>
        get() = _navigateToSignUp

    private val _navigateToVerification = MutableLiveData<Event<Boolean>>()
    val navigateToVerification: LiveData<Event<Boolean>>
        get() = _navigateToVerification


    fun onLoginSelected() {
        _navigateToLogin.value = Event(true)
    }

    fun onSignUpSelected() {
        _navigateToSignUp.value = Event(true)
    }
}