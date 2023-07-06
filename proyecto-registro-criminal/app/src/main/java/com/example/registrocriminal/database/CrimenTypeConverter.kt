package com.example.registrocriminal.database

import androidx.room.TypeConverter
import java.util.*

class CrimenTypeConverter {
    @TypeConverter
    fun fromDate(fecha: Date): Long {
        return fecha.time
    }
    @TypeConverter
    fun toDate(miliseg: Long): Date {
        return Date(miliseg)
    }
}
