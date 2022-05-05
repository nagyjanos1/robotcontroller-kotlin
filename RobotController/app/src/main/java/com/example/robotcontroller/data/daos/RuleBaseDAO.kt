package com.example.robotcontroller.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.robotcontroller.data.RuleBase

@Dao
interface RuleBaseDAO {
    @Query("SELECT * FROM rulebases")
    fun findAllItems(): LiveData<List<RuleBase>>

    @Insert
    fun insertItem(item: RuleBase): Long

    @Delete
    fun deleteItem(item: RuleBase)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateItem(item: RuleBase)

    @Query("SELECT * FROM ruleBases WHERE id = :ruleBaseId")
    fun findItemById(ruleBaseId: Long): RuleBase?
}

