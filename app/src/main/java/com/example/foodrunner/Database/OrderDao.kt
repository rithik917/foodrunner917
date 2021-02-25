package com.example.foodrunner.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
@Dao
interface OrderDao {
@Insert
fun insertOrder(orderEntity: OrderEntity)
    @Delete
    fun deleteOrder(orderEntity: OrderEntity)
    @Query("Select * from orders")
    fun getAllOrders():List<OrderEntity>
    @Query("Delete from orders where resId=:resId")
    fun deleteOrders(resId:String)

}