package com.zotto.kds.database.dao

import androidx.room.*
import com.zotto.kds.database.table.Order
import com.zotto.kds.database.table.Product
import com.zotto.kds.database.table.Restaurant

@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    fun getAllProduct(): List<Product>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductList(products: List<Product>)

    @Query("DELETE FROM product where order_product_id=:order_product_id")
    fun deleteProduct(order_product_id: String)

    @Query("DELETE FROM product")
    fun deleteAllProduct()

    @Query("SELECT * FROM product WHERE order_id = :order_id")
    fun getProduct(order_id: String): List<Product>

    @Query("SELECT * FROM product WHERE product_id = :product_id")
    fun getProductByProductId(product_id: String): Product

   /* @Query("SELECT SUM(quantity) as product FROM product WHERE product_id = :product_id")
    fun getProductCountById(product_id: String): Product*/

    @Query("SELECT * FROM product WHERE product_id = :product_id")
    fun isProductExist(product_id: String): Product

    @Query("SELECT * FROM product WHERE type = :type")
    fun getSummaryProduct(type: String): Product

    @Update
    fun updateProduct(product: Product)
}