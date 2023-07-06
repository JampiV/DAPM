package com.example.registrocriminal.presentacion.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrocriminal.database.CrimenRepository
import com.example.registrocriminal.modelo.Crimen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ListaCrimenesViewModel: ViewModel() {
//    val crimenes = crimenRepository.getCrimenes()
    private val _crimenes: MutableStateFlow<List<Crimen>> = MutableStateFlow(emptyList())
    val crimenRepository = CrimenRepository.get()
    val crimenes: StateFlow<List<Crimen>>
        get() = _crimenes.asStateFlow()
        init {
            viewModelScope.launch {
    //            crimenes += cargarCrimenes()
                crimenRepository.getCrimenes().collect{
                    _crimenes.value = it
                }
            }
        }
    suspend fun cargarCrimenes(): MutableList<Crimen> {
        val resultado = mutableListOf<Crimen>()
        delay(1000)
        for (i in 0 until 100) {
            val esCrimenMayor = i % 2 == 0
            val crimen = Crimen(
                id = UUID.randomUUID(),
                titulo = "crimen # $i",
                fecha = Date(),
                resuelto = i % 2 == 0,
                false
            )
            resultado += crimen
        }
        return resultado
        Log.v("CRIMENES", "resultado.toString()")
    }

    suspend fun ingresarCrimen(crimen: Crimen) {
        crimenRepository.ingresarCrimen(crimen)
    }

//    init {
//        for (i in 0 until 100) {
//            val esCrimenMayor = i % 2 == 0
//            val crimen = Crimen(
//                id = UUID.randomUUID(),
//                titulo = "crimen # $i",
//                fecha = Date(),
//                resuelto = i % 2 == 0,
//                esCrimenMayor
//            )
//            crimenes.add(crimen)
//        }
//    }
}
