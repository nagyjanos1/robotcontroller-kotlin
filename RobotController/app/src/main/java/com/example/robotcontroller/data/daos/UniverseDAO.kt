package com.example.robotcontroller.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.robotcontroller.data.entities.Universe

@Dao
interface UniverseDAO {
    @Query("SELECT * FROM universe")
    fun findAllItems(): LiveData<List<Universe>>

    @Insert
    fun insertItem(item: Universe): Long

    @Delete
    fun deleteItem(item: Universe)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateItem(item: Universe)

    @Query("SELECT * FROM universe WHERE id = :universeId")
    fun findItemById(universeId: Long): Universe?

    @Query("SELECT * FROM universe WHERE fbdlCommandItemId = :fbdlId")
    fun findAllByFbdlId(fbdlId: Long): List<Universe>
}

