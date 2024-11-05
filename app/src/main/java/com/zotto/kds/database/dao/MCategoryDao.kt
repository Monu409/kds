package com.zotto.kds.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zotto.kds.database.m_table.MCategoryData

@Dao
interface MCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApiResponse(apiResponse: MCategoryData)

    @Query("SELECT * FROM m_category_table WHERE id = :id")
    suspend fun getApiResponse(id: Int): MCategoryData

    @Query("SELECT * FROM m_category_table")
    suspend fun getAllProducts(): List<MCategoryData>
}