package com.example.robotcontroller.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "limits", foreignKeys = [
    ForeignKey(
        entity = Universe::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("universeId")
    )
])
data class Limit (
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "minValue") var minValue: Long?,
    @ColumnInfo(name = "maxValue") var maxValue: Long?,
    @ColumnInfo(name = "universeId") var universeId: Long?
) : Serializable