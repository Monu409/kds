package com.zotto.kds.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zotto.kds.database.table.Detour
import com.zotto.kds.database.table.Topping
import java.lang.reflect.Type

class DetourConverters {
    @TypeConverter
    fun jsonToList(value: String?): ArrayList<Detour?>? {
        val listType: Type = object : TypeToken<ArrayList<Detour?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun listToJson(list: ArrayList<Detour?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
}