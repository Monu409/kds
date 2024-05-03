package com.zotto.kds.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zotto.kds.database.table.ForcedModifier
import com.zotto.kds.database.table.OptionalModifier

@Dao
interface OptionalModifierDao {

    @Query("SELECT * FROM optionalmodifier")
    fun getAllOptionalModifier(): LiveData<List<OptionalModifier>?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOptionalModifier(optionalModifier: OptionalModifier)

    @Query("DELETE FROM optionalmodifier where om_item_id=:om_item_id")
    fun deleteOptionalModifier(om_item_id: String)

    @Query("DELETE FROM optionalmodifier")
    fun deleteAllOptionalModifier()

    @Query("SELECT * FROM optionalmodifier WHERE om_item_id = :om_item_id")
    fun getOptionalModifier(om_item_id: String): OptionalModifier
}