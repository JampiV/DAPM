package com.example.registrocriminal.database

import androidx.room.*
import com.example.registrocriminal.modelo.Crimen
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface CrimenDAO {
    @Query("SELECT * FROM crimen")
    fun getCrimenes(): Flow<List<Crimen>>
    @Query("SELECT * FROM crimen WHERE id=(:id)")
    suspend fun getCrimen(id: UUID): Crimen
    @Insert
    suspend fun ingresarCrimen(crimen: Crimen)
    @Update
    suspend fun actualizarCrimen(crimen: Crimen)
    @Delete
    suspend fun eliminarCrimen(crimen: Crimen)
}
