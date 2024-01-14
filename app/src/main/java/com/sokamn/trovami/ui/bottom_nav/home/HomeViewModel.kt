package com.sokamn.trovami.ui.bottom_nav.home

import androidx.lifecycle.ViewModel
import com.sokamn.trovami.data.providers.MasterProvider
import com.sokamn.trovami.domain.model.MasterInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    masterProvider: MasterProvider,

) : ViewModel(){

    private var _masters = MutableStateFlow<List<MasterInfo>>(emptyList())
    val masters: StateFlow<List<MasterInfo>> = _masters

    init{
        _masters.value = masterProvider.getMasters()
    }
}