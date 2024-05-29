package com.zotto.kds.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zotto.kds.database.table.DisabledTable
import com.zotto.kds.database.table.ForcedModifier

@Dao
interface DisabledCategoryDao {
    @Query("SELECT * FROM disabledTable")
    fun getAllDisabledCategory(): LiveData<List<DisabledTable>?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDisabledCategory(disabledProduct: DisabledTable)

    @Query("DELETE FROM disabledTable where restaurant_id=:restId AND category_id=:categoryId")
    fun deleteDisabledCategory(restId: String, categoryId: String)

    @Query("SELECT EXISTS(SELECT * FROM disabledTable WHERE restaurant_id=:restId AND category_id=:categoryId)")
    fun isCategoryIsExist(restId: String, categoryId: String): Boolean

}