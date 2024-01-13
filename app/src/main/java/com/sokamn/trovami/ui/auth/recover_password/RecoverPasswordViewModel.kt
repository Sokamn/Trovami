package com.sokamn.trovami.ui.auth.recover_password

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokamn.trovami.R
import com.sokamn.trovami.core.Event
import com.sokamn.trovami.core.ex.toast
import com.sokamn.trovami.domain.usecase.auth.RecoverPasswordUseCase
import com.sokamn.trovami.ui.auth.signin.SignInViewState
import com.sokamn.trovami.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecoverPasswordViewModel @Inject constructor(
    private val recoverPasswordUseCase: RecoverPasswordUseCase
) : ViewModel() {

    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = _navigateToLogin

    private var _showSuccessDialog = MutableLiveData<Event<Boolean>>()
    val showSuccessDialog: LiveData<Event<Boolean>>
        get() = _showSuccessDialog

    private var _showErrorDialog = MutableLiveData<Event<Boolean>>()
    val showErrorDialog: LiveData<Event<Boolean>>
        get() = _showErrorDialog

    private var _showInputError = MutableLiveData<Event<Boolean>>()
    val showInputError: LiveData<Event<Boolean>>
        get() = _showInputError

    fun sendPasswordPetition(email: String){
        if(isValidOrEmptyEmail(email)){
            sendPasswordLink(email)
        }else{
            _showErrorDialog.value = Event(true)
        }
    }

    fun onGoToLogin(){
        _navigateToLogin.value= Event(true)
    }

    fun isValidOrEmptyEmail(email: String) = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun sendPasswordLink(email: String) {
        viewModelScope.launch {
            when(val result = recoverPasswordUseCase(email)){
                is Resource.Error -> {
                    _showErrorDialog.value = Event(true)
                    Log.e("SOKIERROR", result.message)
                }
                is Resource.Success -> {
                    _showSuccessDialog.value = Event(true)
                }
            }
        }
    }
}