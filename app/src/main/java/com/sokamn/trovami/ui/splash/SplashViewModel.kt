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
import com.sokamn.trovami.domain.usecase.auth.IsUserVerifiedUseCase
import com.sokamn.trovami.domain.usecase.network.CheckInternetConnectionUseCase
import com.sokamn.trovami.domain.usecase.user.ExistsUserConnectedUseCase
import com.sokamn.trovami.domain.usecase.user.GetCurrentUserUidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isUserVerifiedUseCase: IsUserVerifiedUseCase,
    private val checkNetworkConnectionUseCase: CheckInternetConnectionUseCase,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
    private val existsUserConnectedUseCase: ExistsUserConnectedUseCase,
    private val dataStore: DataStore
) : ViewModel() {

    private val _navigateToVerification = MutableLiveData<Event<String>>()
    val navigateToVerification: LiveData<Event<String>>
        get() = _navigateToVerification

    private val _navigateToMain = MutableLiveData<Event<String>>()
    val navigateToMain: LiveData<Event<String>>
        get() = _navigateToMain

    private val _navigateToIntroduction = MutableLiveData<Event<Boolean>>()
    val navigateToIntroduction: LiveData<Event<Boolean>>
        get() = _navigateToIntroduction

    init {
        if (checkNetworkConnectionUseCase()) { // There is Connection
            Log.e("SOKI", "HAY INTERNET")
            verifyEmail()
        } else { // There is not Connection
            Log.e("SOKI", "NO HAY INTERNET")
            getCurrentUserWithoutConnection()
        }
    }


    private fun verifyEmail() {
        viewModelScope.launch {
            isUserVerifiedUseCase()
                .catch {
                    Log.e("SOKI", "Verification error: ${it.message}")
                }
                .collect { verificated ->
                    delay(3000)
                    if (verificated){
                        getCurrentUserUidUseCase().catch {

                        }.collect{ userUID ->
                            _navigateToMain.value = Event(userUID)
                        }

                    }else{
                        if (existsUserConnectedUseCase()) {
                            getCurrentUserUidUseCase().catch {

                            }.collect{ userUID ->
                                _navigateToVerification.value = Event(userUID)
                            }
                        } else {
                            _navigateToIntroduction.value = Event(true)
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
                navigateIfExistsStoredUser(currentUserDS)
            }
        }
    }

    private fun navigateIfExistsStoredUser(currentUserDS: String) {
        if (currentUserDS != "{}") {
            val user: UserModel = Gson().fromJson(currentUserDS, UserModel::class.java)
            _navigateToMain.value = Event(user.uid)
        } else {
            _navigateToIntroduction.value = Event(true)
        }
    }
}