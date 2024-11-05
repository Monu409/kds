package com.zotto.kds.database.m_table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "m_category_table")
data class MCategoryData(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val status: Int,
    val data: List<ResCategory>
)
