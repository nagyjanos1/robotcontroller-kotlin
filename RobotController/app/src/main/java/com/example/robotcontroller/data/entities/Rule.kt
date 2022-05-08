package com.example.robotcontroller.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "rules")
data class Rule (
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "baseUniverseId") var baseUniverseId: Long?,
    @ColumnInfo(name = "baseLimitId") var baseLimitId: Long?,
    @ColumnInfo(name = "antecedentUniverseId") var antecedentUniverseId: Long?,
    @ColumnInfo(name = "antecedentLimitId") var antecedentLimitId: Long?
): Serializable