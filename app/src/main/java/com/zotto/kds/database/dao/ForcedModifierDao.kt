package com.zotto.kds.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zotto.kds.database.table.ForcedModifier

@Dao
interface ForcedModifierDao {

    @Query("SELECT * FROM forcedmodifier")
    fun getAllForcedModifier(): LiveData<List<ForcedModifier>?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertForcedModifier(forcedModifier: ForcedModifier)

    @Query("DELETE FROM forcedmodifier where fm_item_id=:fm_item_id")
    fun deleteForcedModifier(fm_item_id: String)

    @Query("DELETE FROM optionalmodifier")
    fun deleteAllForcedModifier()

    @Query("SELECT * FROM forcedmodifier WHERE fm_item_id = :fm_item_id")
    fun getForcedModifier(fm_item_id: String): ForcedModifier

}