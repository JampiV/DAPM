package com.example.registrocriminal.presentacion

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import java.util.*

class FragmentoTimePicker : DialogFragment() {
    private val args: FragmentoTimePickerArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timeListener = TimePickerDialog.OnTimeSetListener {
                _: TimePicker, hourOfDay: Int, minute: Int ->
            val calendar = Calendar.getInstance()
            calendar.time = args.horaCrimen

            // Actualizar la hora seleccionada
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)

            setFragmentResult(
                CLAVE_HORA_SOLICITADA,
                bundleOf(CLAVE_HORA_BUNDLE to calendar.time)
            )
        }

        val reloj = Calendar.getInstance()
        reloj.time = args.horaCrimen
        val horaInicial = reloj.get(Calendar.HOUR_OF_DAY)
        val minutoInicial = reloj.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            timeListener,
            horaInicial,
            minutoInicial,
            true
        )
    }

    companion object {
        const val CLAVE_HORA_SOLICITADA = "Hora solicitada"
        const val CLAVE_HORA_BUNDLE = "Hora bundle"
    }

}
