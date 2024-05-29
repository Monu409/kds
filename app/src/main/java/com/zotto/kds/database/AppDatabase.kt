package com.zotto.kds.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zotto.kds.database.dao.*
import com.zotto.kds.database.table.*

@Database(entities = arrayOf(Restaurant::class,Order::class,Product::class,ForcedModifier::class,DisabledTable::class, CategoryTable::class,
    OptionalModifier::class,Base::class,Topping::class,Detour::class,DetourOm::class), version = 9, exportSchema = false)
@TypeConverters(Converters::class,ForcedModifierConverters::class,OptionalModifierConverter::class,
    BaseConverters::class,ToppingConverters::class,DetourConverters::class,DetourOmConverters::class)
abstract class AppDatabase: RoomDatabase() {
    companion object{
        val MIGRATION: Migration = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
    }
    abstract fun restaurantDao(): RestaurantDao?

    abstract fun productDao(): ProductDao?

    abstract fun orderDao(): OrderDao?

    abstract fun forcedModifierDao(): ForcedModifierDao?

    abstract fun optionalModifierDao(): OptionalModifierDao?

    abstract fun disableProductDao(): DisabledCategoryDao?
    abstract fun categoryDao(): CategoryDao?

    abstract fun baseDao(): BaseDao?

    abstract fun toppingDao(): ToppingDao?

    abstract fun detourDao(): DetourDao?

    abstract fun detouromDao(): DetourOmDao?
}