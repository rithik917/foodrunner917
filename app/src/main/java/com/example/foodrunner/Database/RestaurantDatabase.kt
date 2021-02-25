package com.example.foodrunner.Database

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(entities = [RestaurantEntity::class,OrderEntity::class],version=2,exportSchema = false)
abstract class RestaurantDatabase: RoomDatabase() {
    abstract fun orderDao():OrderDao
    abstract fun restaurantDao(): RestaurantDao
}