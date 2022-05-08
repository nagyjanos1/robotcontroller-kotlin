package com.example.robotcontroller.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.robotcontroller.data.entities.Limit

@Dao
interface LimitsDAO {

    @Insert
    fun insertItem(item: Limit): Long

    @Delete
    fun deleteItem(item: Limit)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateItem(item: Limit)

    @Query("SELECT * FROM limits")
    fun findAllItems(): LiveData<List<Limit>>

    @Query("SELECT * FROM limits WHERE id = :limitId")
    fun findItemById(limitId: Long): Limit?

    @Query("SELECT * FROM limits WHERE id IN (:limitIds)")
    fun findItemByIds(limitIds: List<Long>): Array<Limit>?

    @Query("SELECT * FROM limits WHERE universeId = :universeId")
    abstract fun findItemByUniverseId(universeId: Long): List<Limit>
}