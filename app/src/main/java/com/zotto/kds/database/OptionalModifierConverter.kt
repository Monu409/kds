package com.zotto.kds.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zotto.kds.database.table.ForcedModifier
import com.zotto.kds.database.table.OptionalModifier
import java.lang.reflect.Type

class OptionalModifierConverter {

    @TypeConverter
    fun jsonToList(value: String?): ArrayList<OptionalModifier?>? {
        val listType: Type = object : TypeToken<ArrayList<OptionalModifier?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun listToJson(list: ArrayList<OptionalModifier?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
}