package com.zotto.kds

interface LoginResultCallBacks {
    fun onSuccess(message:String)
    fun onError(message: String)
}