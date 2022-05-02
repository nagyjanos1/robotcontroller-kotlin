package com.example.robotcontroller.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.robotcontroller.data.Limit

@Dao
interface LimitsDAO {
    @Query("SELECT * FROM limits")
    fun findAllItems(): LiveData<List<Limit>>

    @Insert
    fun insertItem(item: Limit): Long

    @Delete
    fun deleteItem(item: Limit)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateItem(item: Limit)

    @Query("SELECT * FROM limits WHERE id = :limitId")
    fun findItemById(limitId: Long): Limit?
}