package com.sokamn.trovami.ui.auth.verification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokamn.trovami.core.Event
import com.sokamn.trovami.domain.usecase.auth.IsEmailVerifiedUseCase
import com.sokamn.trovami.domain.usecase.auth.SendEmailVerificationUseCase
import com.sokamn.trovami.domain.usecase.user.GetCurrentUserEmail
import com.sokamn.trovami.domain.usecase.user.GetCurrentUserUidUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class VerificationViewModel @Inject constructor(
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val isEmailVerifiedUseCase: IsEmailVerifiedUseCase,
    private val getCurrentUserEmailUseCase: GetCurrentUserEmail,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase
) : ViewModel() {

    private val _navigateToMain = MutableLiveData<Event<String>>()
    val navigateToMain: LiveData<Event<String>>
        get() = _navigateToMain

    private val _showContinueButton = MutableLiveData<Event<Boolean>>()
    val showContinueButton: LiveData<Event<Boolean>>
        get() = _showContinueButton

    private val _sendEmail = MutableLiveData<Event<Boolean>>()
    val sendEmail: LiveData<Event<Boolean>>
        get() = _sendEmail

    private val _emailVerified = MutableLiveData<String>()
    val emailVerified: LiveData<String>
        get() = _emailVerified

    private val _navigateToBack = MutableLiveData<Event<Boolean>>()
    val navigateToBack: LiveData<Event<Boolean>>
        get() = _navigateToBack

    init {
        sendEmailVerification()
        getCurrentEmail()
        verifyIfMailVerified()
    }

    fun onGoToMainSelected() {
        viewModelScope.launch {
            getCurrentUserUidUseCase().catch{

            }.collect{ currentUserUid ->
                _navigateToMain.value = Event(currentUserUid)
            }
        }
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
                _emailVerified.value = emailVerified
            }
        }
    }

    private fun verifyIfMailVerified(){
        viewModelScope.launch {
            isEmailVerifiedUseCase()
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