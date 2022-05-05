package com.example.robotcontroller.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "ruleBases", foreignKeys = [
    ForeignKey(
        entity = FbdlCommandItem::class,
        parentColumns = arrayOf("itemId"),
        childColumns = arrayOf("fbdlCommandItemId")
    ),
    ForeignKey(
        entity = Universe::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("universeId")
    )
])
data class RuleBase (
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "fbdlCommandItemId") var fbdlCommandItemId : Long?,
    @ColumnInfo(name = "universeId") var universeId : Long?
): Serializable