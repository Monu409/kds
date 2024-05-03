package com.zotto.kds.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zotto.kds.database.table.Base
import com.zotto.kds.database.table.Detour

@Dao
interface DetourDao {
    @Query("SELECT * FROM detour")
    fun getAllDetour(): LiveData<List<Detour>?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDetour(detour: Detour)

    @Query("DELETE FROM detour where fm_item_id=:fm_item_id")
    fun deleteDetour(fm_item_id: String)

    @Query("DELETE FROM detour")
    fun deleteAllDetour()

    @Query("SELECT * FROM detour WHERE fm_item_id = :fm_item_id")
    fun getDetour(fm_item_id: String): Detour
}