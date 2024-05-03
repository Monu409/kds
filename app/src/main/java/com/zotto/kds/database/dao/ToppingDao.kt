package com.zotto.kds.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zotto.kds.database.table.DetourOm
import com.zotto.kds.database.table.Topping

@Dao
interface ToppingDao {
    @Query("SELECT * FROM topping")
    fun getAllTopping(): LiveData<List<Topping>?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTopping(topping: Topping)

    @Query("DELETE FROM topping where name=:name")
    fun deleteTopping(name: String)

    @Query("DELETE FROM topping")
    fun deleteAllTopping()

    @Query("SELECT * FROM topping WHERE name = :name")
    fun getTopping(name: String): Topping
}