package com.zotto.kds.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zotto.kds.database.table.Base
import com.zotto.kds.database.table.ForcedModifier
import java.lang.reflect.Type

class BaseConverters {
    @TypeConverter
    fun jsonToList(value: String?): ArrayList<Base?>? {
        val listType: Type = object : TypeToken<ArrayList<Base?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun listToJson(list: ArrayList<Base?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
}