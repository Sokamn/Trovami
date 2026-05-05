package com.sokamn.trovami.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokamn.trovami.core.Event
import com.sokamn.trovami.domain.usecase.VerifyEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    val verifyEmailUseCase: VerifyEmailUseCase
) : ViewModel() {

    private val _navigateToVerification = MutableLiveData<Event<Boolean>>()
    val navigateToVerification: LiveData<Event<Boolean>>
        get() = _navigateToVerification

    private val _navigateToMain = MutableLiveData<Event<Boolean>>()
    val navigateToMain: LiveData<Event<Boolean>>
        get() = _navigateToMain

    private val _navigateToIntroduction = MutableLiveData<Event<Boolean>>()
    val navigateToIntroduction: LiveData<Event<Boolean>>
        get() = _navigateToIntroduction

    init {
        viewModelScope.launch {
            verifyEmailUseCase()
                .catch {
                    Timber.i("Verification error: ${it.message}")
                }
                .collect { verification ->
                    delay(2500)
                    if(verification){
                        _navigateToMain.value = Event(true)
                    }else{
                        verifyEmailUseCase.existUserConnected().collect{ existUser ->
                            if(existUser){
                                _navigateToVerification.value = Event(true)
                            }else{
                                _navigateToIntroduction.value = Event(true)
                            }
                        }
                    }
                }
        }
    }


}