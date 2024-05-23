package com.zotto.kds.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zotto.kds.database.table.Restaurant

@Dao
interface RestaurantDao {
    @Query("SELECT * FROM restaurant")
    fun getAllRestaurant(): LiveData<List<Restaurant>?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRestaurant(restaurant: Restaurant)

    @Query("DELETE FROM restaurant where restaurantid=:restaurantid")
    fun deleteRestaurant(restaurantid: String)

    @Query("DELETE FROM restaurant")
    fun deleteAll()

    @Query("SELECT * FROM restaurant WHERE restaurantid = :restaurantid")
    fun getRestaurant(restaurantid: String):Restaurant

}