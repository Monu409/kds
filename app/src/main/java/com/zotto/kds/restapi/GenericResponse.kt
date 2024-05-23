package com.zotto.kds.restapi

import com.fasterxml.jackson.annotation.JsonProperty


class GenericResponse<T> {
    @JsonProperty("status")
    private var status: String? = null

    @JsonProperty("message")
    private var message: String? = null

    @JsonProperty("data")
    private var data: T? = null

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getData(): T? {
        return data
    }

    fun setData(data: T) {
        this.data = data
    }
}