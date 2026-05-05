package com.sokamn.trovami.presentation.password_recovery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokamn.trovami.core.Event
import com.sokamn.trovami.domain.usecase.PasswordRecoveryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordRecoveryViewModel @Inject constructor(
    private val passwordRecoveryUseCase: PasswordRecoveryUseCase
) : ViewModel() {

    private val _navigateToLogin = MutableLiveData<Event<Boolean>>()
    val navigateToLogin: LiveData<Event<Boolean>>
        get() = _navigateToLogin

    private val _passwordSent = MutableLiveData<Event<Boolean>>()
    val passwordSent: LiveData<Event<Boolean>>
        get() = _passwordSent

    fun sendPasswordLink(email: String) {
        viewModelScope.launch {
            passwordRecoveryUseCase(email).onEach {
                _passwordSent.value = Event(it)
                if(it){
                   _navigateToLogin.value = Event(true)
                }
            }.launchIn(viewModelScope)
        }
    }
}