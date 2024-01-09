package com.sokamn.trovami.ui.auth.verification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokamn.trovami.core.Event
import com.sokamn.trovami.domain.usecase.auth.IsUserVerifiedUseCase
import com.sokamn.trovami.domain.usecase.auth.SendEmailVerificationUseCase
import com.sokamn.trovami.domain.usecase.user.GetCurrentUserEmail
import com.sokamn.trovami.domain.usecase.user.GetCurrentUserUidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val isUserVerifiedUseCase: IsUserVerifiedUseCase,
    private val getCurrentUserEmailUseCase: GetCurrentUserEmail,
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
        //sendEmailVerification()
        getCurrentEmail()
        //verifyIfMailVerified()
    }

    fun onGoToMainSelected() {
        _navigateToMain.value = Event(true)
    }

    fun onSendEmail(){
        _sendEmail.value = Event(true)
    }

    fun sendEmailVerification() {
        viewModelScope.launch { sendEmailVerificationUseCase() }
    }

    private fun getCurrentEmail(){
        viewModelScope.launch {
            getCurrentUserEmailUseCase().catch {

            }.collect{ emailVerified ->
                _emailVerified.value = Event(emailVerified)
            }
        }
    }

    private fun verifyIfMailVerified(){
        viewModelScope.launch {
            isUserVerifiedUseCase()
                .catch {
                    Log.e("SOKI", "Verification error: ${it.message}")
                }
                .collect { verification ->
                    if(verification){
                        _showContinueButton.value = Event(verification)
                    }
                }
        }
    }

    fun onGoToBackSelected(){
        // BORRAR DATASTORE
        _navigateToBack.value = Event(true)
    }
}