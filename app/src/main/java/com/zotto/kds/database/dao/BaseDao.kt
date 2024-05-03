package com.zotto.kds.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zotto.kds.database.table.Base
import com.zotto.kds.database.table.ForcedModifier

@Dao
interface BaseDao {
    @Query("SELECT * FROM base")
    fun getAllBase(): LiveData<List<Base>?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBase(base: Base)

    @Query("DELETE FROM base where pizza_base_id=:pizza_base_id")
    fun deleteBase(pizza_base_id: String)

    @Query("DELETE FROM base")
    fun deleteAllBase()

    @Query("SELECT * FROM base WHERE pizza_base_id = :pizza_base_id")
    fun getBase(pizza_base_id: String): Base
}