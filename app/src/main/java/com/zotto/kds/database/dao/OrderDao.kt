package com.zotto.kds.database.dao

import androidx.paging.DataSource
import androidx.room.*
import com.zotto.kds.database.table.Order

@Dao
interface OrderDao {
  @Query("SELECT * FROM `order` WHERE order_status ='Confirm' OR order_status ='New' ORDER BY order_time DESC LIMIT 1")
  fun getAllActiveOrder(): List<Order>?

  @Query("SELECT * FROM `order` WHERE order_status ='Confirm' OR order_status ='New' ORDER BY order_time DESC ")
  fun getAllActiveOrderNew(): List<Order>?

  @Query("SELECT * FROM `order`  ORDER BY order_time DESC LIMIT 1")
  fun getAllOrder(): List<Order>?

  @Query("SELECT * FROM `order` WHERE order_status ='Ready' ORDER BY order_time DESC LIMIT 1")
  fun getAllCompletedOrder(): List<Order>?

  @Query("SELECT * FROM `order`  WHERE order_status ='Confirm' OR order_status ='New' ORDER BY order_time DESC")
  @Transaction
  fun getAllDatasourceActiveOrder(): DataSource.Factory<Int, Order>

  @Query("SELECT * FROM `order` WHERE order_status ='Ready' ORDER BY order_time DESC")
  @Transaction
  fun getAllDatasourceCompletedOrder(): DataSource.Factory<Int, Order>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertOrder(order: Order)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertOrders(order: List<Order>)

  @Query("DELETE FROM `order` where order_id=:order_id")
  fun deleteOrder(order_id: String)

  @Query("DELETE FROM `order` ")
  fun deleteAllOrder()

  @Delete
  fun deleteOrder(order: Order)

  @Query("DELETE FROM `order`  WHERE order_status ='Confirm' OR order_status ='New'")
  fun deleteAllActiveOrder()

  @Query("SELECT * FROM `order` WHERE order_id = :order_id")
  fun getOrder(order_id: String): Order

  @Update
  fun updateOrder(order: Order)

  @Query("UPDATE `order` SET order_status=:orderstatus WHERE order_id = :order_id")
  fun updateOrder(order_id: String, orderstatus: String)

  @Query("SELECT * FROM `order` WHERE order_id = :order_id")
  fun isOrderExist(order_id: String): Order

  @Query("SELECT order_status FROM `order` WHERE order_id = :order_id")
  fun getOrderStatus(order_id: String): String
}