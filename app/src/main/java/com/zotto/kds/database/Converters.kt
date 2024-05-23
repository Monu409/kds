package com.zotto.kds.database

import androidx.room.TypeConverter
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken
import com.zotto.kds.database.table.Product
import java.lang.reflect.Type

 class Converters {
    @TypeConverter
    fun jsonToList(value: String?): ArrayList<Product?>? {
        val listType: Type = object : TypeToken<ArrayList<Product?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun listToJson(list: ArrayList<Product?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
}