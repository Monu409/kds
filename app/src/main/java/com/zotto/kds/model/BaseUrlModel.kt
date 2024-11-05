package com.zotto.kds.model

data class BaseUrlModel(
    val id: String,
    val base_url: String,
    val status: Int,
    val type: String,
    val added_on: String,
    val updated_on: String,
)
