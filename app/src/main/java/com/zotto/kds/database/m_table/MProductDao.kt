package com.zotto.kds.database.m_table

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: MProduct)

//    @Query("SELECT * FROM m_products WHERE productId = :productId")
//    suspend fun getProductById(productId: String): MProduct?

    @Query("SELECT * FROM m_products WHERE categoryId = :categoryId")
    suspend fun getProductById(categoryId: String): List<MProduct?>

    @Query("SELECT * FROM m_products")
    suspend fun getAllProducts(): List<MProduct>
}
