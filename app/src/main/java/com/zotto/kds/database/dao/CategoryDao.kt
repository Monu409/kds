package com.zotto.kds.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zotto.kds.database.table.CategoryTable

@Dao
interface CategoryDao {
  @Query("SELECT * FROM categoryTable")
  fun getAllCategory(): List<CategoryTable>?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertCategory(device: CategoryTable)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertCategoryList(devices: List<CategoryTable>)

  @Query("SELECT (SELECT COUNT(*) FROM categoryTable) == 0")
  fun isCategoryEmpty(): Boolean

  @Query("SELECT *  FROM categoryTable c WHERE NOT EXISTS (SELECT id FROM disabledTable d WHERE c.category_id = d.category_id)")
  fun getAllAvailableCategory(): List<CategoryTable>

  @Query("SELECT *  FROM categoryTable c WHERE EXISTS (SELECT id FROM disabledTable d WHERE c.category_id = d.category_id)")
  fun getAllUnavailableCategory(): List<CategoryTable>

  @Query("DELETE FROM categoryTable where restaurant_id=:restId AND category_id=:category_id")
  fun deleteCategory(restId: String, category_id: String)

  @Query("DELETE FROM categoryTable")
  fun deleteAllCategory()

  @Query("SELECT EXISTS(SELECT * FROM categoryTable WHERE restaurant_id=:restId AND category_id=:category_id)")
  fun isCategoryExist(restId: String, category_id: String): Boolean
}