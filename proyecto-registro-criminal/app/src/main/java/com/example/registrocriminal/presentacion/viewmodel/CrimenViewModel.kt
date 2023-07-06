package com.example.registrocriminal.presentacion.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.registrocriminal.database.CrimenRepository
import com.example.registrocriminal.modelo.Crimen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.annotations.Nullable
import java.util.UUID

class CrimenViewModel(crimenId: UUID) : ViewModel() {
    private val repositorio = CrimenRepository.get()
    private val _crimen: MutableStateFlow<Crimen?> = MutableStateFlow(null)
    private val _crimenEliminado = MutableLiveData<Unit>()
    val crimenEliminado: LiveData<Unit> = _crimenEliminado
    val crimen: StateFlow<Crimen?> = _crimen.asStateFlow()

    init {
        viewModelScope.launch {
            _crimen.value = repositorio.getCrimen(crimenId)
        }
    }

    fun actualizarCrimen(onUpdate: (Crimen) -> Crimen) {
        _crimen.update { anterior ->
            anterior?.let { onUpdate(it) }
        }
    }

    fun eliminarCrimen() {
        viewModelScope.launch {
            _crimen.value?.let { crimen ->
                repositorio.eliminarCrimen(crimen)
                _crimenEliminado.postValue(Unit)
            }
        }
    }

    override fun onCleared() {
        Log.d("CRIMEN VIEWMODEL", "ON CLEARED")
        super.onCleared()
        viewModelScope.launch {
            _crimen.value?.let {
                repositorio.actualizarCrimen(it)
            }

        }
    }
}

class CrimenViewModelFactory(
    private val crimenId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClasee: Class<T>): T {
        return CrimenViewModel(crimenId) as T
    }
}
