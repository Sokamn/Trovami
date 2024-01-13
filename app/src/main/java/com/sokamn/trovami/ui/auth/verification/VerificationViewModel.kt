package com.sokamn.trovami.ui.auth.verification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokamn.trovami.core.Event
import com.sokamn.trovami.data.source.datastore.DataStore
import com.sokamn.trovami.domain.usecase.auth.IsUserVerifiedUseCase
import com.sokamn.trovami.domain.usecase.auth.LogOutUseCase
import com.sokamn.trovami.domain.usecase.auth.SendEmailVerificationUseCase
import com.sokamn.trovami.domain.usecase.user.GetCurrentUserEmail
import com.sokamn.trovami.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val isUserVerifiedUseCase: IsUserVerifiedUseCase,
    private val getCurrentUserEmailUseCase: GetCurrentUserEmail,
    private val logOutUseCase: LogOutUseCase,
    private val dataStore: DataStore
) : ViewModel() {

    private val _navigateToMain = MutableLiveData<Event<Boolean>>()
    val navigateToMain: LiveData<Event<Boolean>>
        get() = _navigateToMain

    private val _showContinueButton = MutableLiveData<Event<Boolean>>()
    val showContinueButton: LiveData<Event<Boolean>>
        get() = _showContinueButton

    private val _sendEmail = MutableLiveData<Event<Boolean>>()
    val sendEmail: LiveData<Event<Boolean>>
        get() = _sendEmail

    private val _emailVerified = MutableLiveData<Event<String>>()
    val emailVerified: LiveData<Event<String>>
        get() = _emailVerified

    private val _navigateToBack = MutableLiveData<Event<Boolean>>()
    val navigateToBack: LiveData<Event<Boolean>>
        get() = _navigateToBack

    init {
        getCurrentEmail()
    }

    fun onGoToMainSelected() {
        _navigateToMain.value = Event(true)
    }

    fun onSendEmail() {
        _sendEmail.value = Event(true)
    }

    fun sendEmailVerification() {
        viewModelScope.launch { sendEmailVerificationUseCase() }
    }

    private fun getCurrentEmail() {
        viewModelScope.launch {
            when (val result = getCurrentUserEmailUseCase()) {
                is Resource.Error -> {
                    Log.e("SOKI", "Verification error: ${result.message}")
                }
                is Resource.Success -> {
                    _emailVerified.value = Event(result.data)
                    sendEmailVerification()
                    verifyIfMailVerified()
                }
            }
        }
    }

    private fun verifyIfMailVerified() {
        viewModelScope.launch {
            isUserVerifiedUseCase()
                .catch {
                    Log.e("SOKI", "Verification error: ${it.message}")
                }
                .collect { verification ->
                    if (verification) {
                        _showContinueButton.value = Event(true)
                    }
                }
        }
    }

    fun onGoToBackSelected() {
        viewModelScope.launch {
            when (val result = logOutUseCase()) {
                is Resource.Error -> {
                    Log.e("SOKIERROR", result.message)
                }

                is Resource.Success -> {
                    dataStore.clearAllPreferences()
                    _navigateToBack.value = Event(true)
                }
            }
        }
    }
}