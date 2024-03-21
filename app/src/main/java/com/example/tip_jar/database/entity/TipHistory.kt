package com.example.tip_jar.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "tip_history"
)
data class TipHistory(
    @PrimaryKey
    val timestamp: Long,
    val amount: String,
    val peopleCount: Int,
    val tipPercent: String,
    val totalTip: Double,
    val perPerson: Double,
    val imageData: String,
    val isTakePhotoChecked: Boolean = false
) : Serializable