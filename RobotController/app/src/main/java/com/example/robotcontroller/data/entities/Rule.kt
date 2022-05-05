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
    @ColumnInfo(name = "baseLimitId") var baseLimitId: Long?,
    @ColumnInfo(name = "baseUniverseId") var baseUniverseId: Long?,
    @ColumnInfo(name = "ruleLimitId") var ruleLimitId: Long?,
    @ColumnInfo(name = "ruleUniverseId") var ruleUniverseId: Long?,
): Serializable