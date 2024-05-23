package com.zotto.kds.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zotto.kds.database.table.Detour
import com.zotto.kds.database.table.DetourOm
import java.lang.reflect.Type

class DetourOmConverters {
    @TypeConverter
    fun jsonToList(value: String?): ArrayList<DetourOm?>? {
        val listType: Type = object : TypeToken<ArrayList<DetourOm?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun listToJson(list: ArrayList<DetourOm?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
}