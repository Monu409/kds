package com.zotto.kds.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zotto.kds.database.table.ForcedModifier
import com.zotto.kds.database.table.Product
import java.lang.reflect.Type

class ForcedModifierConverters {
    @TypeConverter
    fun jsonToList(value: String?): ArrayList<ForcedModifier?>? {
        val listType: Type = object : TypeToken<ArrayList<ForcedModifier?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun listToJson(list: ArrayList<ForcedModifier?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
}