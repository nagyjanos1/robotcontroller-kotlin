package com.example.robotcontroller.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "fbdlcommanditems")
data class FbdlCommandItem (
    @PrimaryKey(autoGenerate = true) var itemId: Long?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "fbdl") var fbdl: String,
    @ColumnInfo(name = "current") var isDefault: Boolean
) : Serializable

