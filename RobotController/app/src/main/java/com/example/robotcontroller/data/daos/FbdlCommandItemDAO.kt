package com.example.robotcontroller.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.robotcontroller.data.entities.FbdlCommandItem

@Dao
interface FbdlCommandItemDAO {
    @Query("SELECT * FROM fbdlcommanditems")
    fun findAllItems(): LiveData<List<FbdlCommandItem>>

    @Insert
    fun insertItem(item: FbdlCommandItem): Long

    @Delete
    fun deleteItem(item: FbdlCommandItem)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateItem(item: FbdlCommandItem)

    @Query("SELECT * FROM fbdlcommanditems WHERE itemId = :itemId")
    fun findItemById(itemId: Long): FbdlCommandItem?
}