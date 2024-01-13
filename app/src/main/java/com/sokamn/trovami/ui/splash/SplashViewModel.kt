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
import com.sokamn.trovami.domain.usecase.user.GetCurrentUserUidUseCase
import com.sokamn.trovami.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isUserVerifiedUseCase: IsUserVerifiedUseCase,
    private val checkNetworkConnectionUseCase: CheckInternetConnectionUseCase,
    private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
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
            verifyEmail()
        } else { // There is not Connection
            getCurrentUserWithoutConnection()
        }
    }


    private fun verifyEmail() {
        viewModelScope.launch {
            isUserVerifiedUseCase()
                .catch {
                    Log.e("SOKI", "Verification error: ${it.message}")
                }
                .collect { isVerificated ->
                    delay(3000)
                    when(val result = getCurrentUserUidUseCase()){
                        is Resource.Error -> {
                            Log.e("SOKINULL", result.message)
                            _navigateToIntroduction.value = Event(true)
                        }
                        is Resource.Success -> {
                            if (isVerificated){
                                _navigateToMain.value = Event(result.data)
                            }else{
                                _navigateToVerification.value = Event(result.data)
                            }
                        }
                    }
                }
        }
    }


    private fun getCurrentUserWithoutConnection() {
        viewModelScope.launch {
            dataStore.getCurrentUser().catch {
                Log.e("SOKIERROR", it.message.toString())
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