package com.zotto.kds.model

data class Data(
    val product_id: String,
    val category_id: String,
    val restaurant_id: Int,
    val status: String,
    val pname: String,
    val description: String,
    val cn_pname: String,
    val price: Int
)