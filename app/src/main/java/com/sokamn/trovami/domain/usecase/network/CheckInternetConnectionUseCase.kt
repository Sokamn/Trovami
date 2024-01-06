package com.sokamn.trovami.domain.usecase.network

import com.sokamn.trovami.data.network.NetworkService
import javax.inject.Inject

class CheckInternetConnectionUseCase @Inject constructor(private val networkUtils: NetworkService) {
    operator fun invoke(): Boolean = networkUtils.isNetworkConnected()
}