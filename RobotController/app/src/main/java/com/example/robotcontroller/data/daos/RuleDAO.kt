package com.example.robotcontroller.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.robotcontroller.data.entities.Limit
import com.example.robotcontroller.data.entities.Rule

@Dao
interface RuleDAO {
    @Query("SELECT * FROM rules")
    fun findAllItems(): LiveData<List<Rule>>

    @Insert
    fun insertItem(item: Rule): Long

    @Delete
    fun deleteItem(item: Rule)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateItem(item: Rule)

    @Query("SELECT * FROM rules WHERE id = :ruleId")
    fun findItemById(ruleId: Long): Rule?

    @Query("SELECT * FROM rules WHERE id IN (:ruleIds)")
    fun findItemByIds(ruleIds: List<Long>): List<Rule>?

    @Query("SELECT * FROM rules WHERE fbdlId = :fbdlId")
    fun getAllByFbdlId(fbdlId: Long): List<Rule>?
}