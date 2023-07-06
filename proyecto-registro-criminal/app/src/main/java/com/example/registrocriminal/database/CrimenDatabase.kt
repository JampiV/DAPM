package com.example.registrocriminal.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.registrocriminal.modelo.Crimen

@Database (entities = [Crimen::class], version = 2)
@TypeConverters (CrimenTypeConverter::class)
abstract class CrimenDatabase:RoomDatabase() {
    abstract fun crimenDAO(): CrimenDAO
}

val migration_1_2 = object : Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Crimen ADD COLUMN sospechoso TEXT NOT NULL DEFAULT ''"
        )
    }
}
