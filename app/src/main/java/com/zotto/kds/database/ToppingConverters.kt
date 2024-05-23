package com.zotto.kds.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zotto.kds.database.table.Base
import com.zotto.kds.database.table.Topping
import java.lang.reflect.Type

class ToppingConverters {
    @TypeConverter
    fun jsonToList(value: String?): ArrayList<Topping?>? {
        val listType: Type = object : TypeToken<ArrayList<Topping?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun listToJson(list: ArrayList<Topping?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
}