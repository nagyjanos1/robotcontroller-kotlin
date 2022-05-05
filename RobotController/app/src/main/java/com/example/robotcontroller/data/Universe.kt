package com.example.robotcontroller.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "universe", foreignKeys = [
    ForeignKey(
        entity = FbdlCommandItem::class,
        parentColumns = arrayOf("itemId"),
        childColumns = arrayOf("fbdlCommandItemId")
    )
])
data class Universe (
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "fbdlCommandItemId") var fbdlCommandItemId : Long
) : Serializable