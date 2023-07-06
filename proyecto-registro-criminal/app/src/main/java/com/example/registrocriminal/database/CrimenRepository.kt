package com.example.registrocriminal.database;

import android.content.Context;
import androidx.room.Room
import com.example.registrocriminal.modelo.Crimen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*

private const val DATABASE_NAME = "crimen-database"

class CrimenRepository private constructor(
    context: Context,
    private val coroutineScoupe: CoroutineScope = GlobalScope
) {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        CrimenDatabase::class.java,
        DATABASE_NAME
    ).addMigrations(migration_1_2).build()

    fun getCrimenes(): Flow<List<Crimen>> = database.crimenDAO().getCrimenes()
    suspend fun getCrimen(id: UUID): Crimen = database.crimenDAO().getCrimen(id)
    suspend fun ingresarCrimen(crimen: Crimen) {
        database.crimenDAO().ingresarCrimen(crimen)
    }

    suspend fun actualizarCrimen(crimen: Crimen) {
        coroutineScoupe.launch {
            database.crimenDAO().actualizarCrimen(crimen)
        }
    }

    suspend fun eliminarCrimen(crimen: Crimen) {
        database.crimenDAO().eliminarCrimen(crimen)
    }

    companion object {
        private var INSTANCIA: CrimenRepository? = null
        fun inicializar(context: Context) {
            if (INSTANCIA == null) {
                INSTANCIA = CrimenRepository(context)
            }
        }

        fun get(): CrimenRepository {
            return INSTANCIA ?: throw java.lang.IllegalStateException("Debe inicializar el repositorio")
        }
    }
}
