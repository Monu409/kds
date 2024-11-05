package com.zotto.kds.database.m_table

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MCatConverters {

    @TypeConverter
    fun fromCategoryList(categoryList: List<ResCategory>?): String {
        if (categoryList == null) return ""
        val gson = Gson()
        return gson.toJson(categoryList)
    }

    @TypeConverter
    fun toCategoryList(categoryListString: String): List<ResCategory>? {
        if (categoryListString.isEmpty()) return listOf()
        val listType = object : TypeToken<List<ResCategory>>() {}.type
        return Gson().fromJson(categoryListString, listType)
    }
}
