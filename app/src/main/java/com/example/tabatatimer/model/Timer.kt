package com.example.tabatatimer.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "timer_table")
data class Timer(
        @PrimaryKey(autoGenerate = true)
        var timerId: Long = 0,

        @ColumnInfo(name = "timer_name")
        val name: String,

        @ColumnInfo(name = "preparation")
        var preparation: Int,

        @ColumnInfo(name = "work_time")
        val worktime: Int,

        @ColumnInfo(name = "rest_teme")
        val restTime: Int,

        @ColumnInfo(name = "sets")
        val sets: Int,

        @ColumnInfo(name = "color")
        val color: Int
) : Parcelable