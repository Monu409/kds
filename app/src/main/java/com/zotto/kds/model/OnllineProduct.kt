package com.zotto.kds.model

import com.google.gson.annotations.Expose

data class OnllineProduct(
    @Expose(serialize = false, deserialize = false)
    val `data`: List<DataX>,
    val status: Int
)