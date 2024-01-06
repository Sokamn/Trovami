package com.sokamn.trovami.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sokamn.trovami.core.Event
import com.sokamn.trovami.data.source.datastore.DataStore
import com.sokamn.trovami.domain.model.UserModel
import com.sokamn.trovami.domain.usecase.auth.VerifyEmailUseCase
import com.sokamn.trovami.domain.usecase.network.CheckInternetConnectionUseCase
import com.sokamn.trovami.domain.usecase.user.GetCurrentUserUidUseCase
import com.sokamn.trovami.domain.usecase.user.ExistsUserConnectedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val checkNetworkConnectionUseCase: CheckInternetConnectionUseCase,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
    private val existsUserConnectedUseCase: ExistsUserConnectedUseCase,
    private val dataStore: DataStore
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

    private val _currentUser = MutableLiveData<String>()
    val currentUser: LiveData<String>
        get() = _currentUser

    init {
        if (checkNetworkConnectionUseCase()) { // There is Connection
            getCurrentUserUID()
            verifyEmail()
        } else {// There is not Connection
            getCurrentUserWithoutConnection()
        }
    }

    private fun getCurrentUserUID() {
        viewModelScope.launch {
            getCurrentUserUidUseCase().catch {
                Log.e("SOKAMN", "Verification error: ${it.message}")
            }.collect { userUID ->
                _currentUser.value = userUID
            }
        }
    }

    private fun verifyEmail() {
        viewModelScope.launch {
            verifyEmailUseCase()
                .catch {
                    Log.e("SOKI","Verification error: ${it.message}")
                }
                .collect { verification ->
                    delay(3000)
                    if (verification) {
                        _navigateToMain.value = Event(true)
                    } else {
                        existsUserConnectedUseCase().collect { existUser ->
                            if (existUser) {
                                _navigateToVerification.value = Event(true)
                            } else {
                                _navigateToIntroduction.value = Event(true)
                            }
                        }
                    }
                }
        }
    }

    private fun getCurrentUserWithoutConnection() {
        viewModelScope.launch {
            dataStore.getCurrentUser().catch {

            }.collect { currentUserDS ->
                delay(3000)
                if (currentUserDS == "{}") {
                    _navigateToIntroduction.value = Event(true)
                } else {
                    val user: UserModel = Gson().fromJson(currentUserDS, UserModel::class.java)
                    _currentUser.value = user.uid
                    _navigateToMain.value = Event(true)
                }
            }
        }
    }
}