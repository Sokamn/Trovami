package com.sokamn.trovami.presentation.verification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokamn.trovami.core.Event
import com.sokamn.trovami.data.network.FirebaseClient
import com.sokamn.trovami.domain.usecase.SendEmailVerificationUseCase
import com.sokamn.trovami.domain.usecase.VerifyEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    val verifyEmailUseCase: VerifyEmailUseCase
) : ViewModel() {

    private val _navigateToMainWithVerifyAccount = MutableLiveData<Event<Boolean>>()
    val navigateToMainWithVerifyAccount: LiveData<Event<Boolean>>
        get() = _navigateToMainWithVerifyAccount

    private val _showContinueButton = MutableLiveData<Event<Boolean>>()
    val showContinueButton: LiveData<Event<Boolean>>
        get() = _showContinueButton

    private val _sendEmail = MutableLiveData<Event<Boolean>>()
    val sendEmail: LiveData<Event<Boolean>>
        get() = _sendEmail

    private val _emailVerified = MutableLiveData<String>()
    val emailVerified: LiveData<String>
        get() = _emailVerified

    init {
        sendEmailVerification()
        viewModelScope.launch {
            verifyEmailUseCase.emailVerified().collect{ emailVerified ->
                _emailVerified.value = emailVerified
            }
            verifyEmailUseCase()
                .catch {
                    Timber.i("Verification error: ${it.message}")
                }
                .collect { verification ->
                    if(verification){
                        _showContinueButton.value = Event(verification)
                    }
                }
        }
    }

    fun onGoToMainSelected() {
        _navigateToMainWithVerifyAccount.value = Event(true)
    }

    fun onSendEmail(){
        _sendEmail.value = Event(true)
    }

    fun sendEmailVerification() {
        viewModelScope.launch { sendEmailVerificationUseCase() }
    }
}