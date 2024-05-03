package com.zotto.kds.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zotto.kds.database.table.Detour
import com.zotto.kds.database.table.DetourOm

@Dao
interface DetourOmDao {
    @Query("SELECT * FROM detourom")
    fun getAllDetourOm(): LiveData<List<DetourOm>?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDetourOm(detourOm: DetourOm)

    @Query("DELETE FROM detourom where om_item_id=:om_item_id")
    fun deleteDetourOm(om_item_id: String)

    @Query("DELETE FROM detourom")
    fun deleteAllDetourOm()

    @Query("SELECT * FROM detourom WHERE om_item_id = :om_item_id")
    fun getDetourOm(om_item_id: String): DetourOm
}