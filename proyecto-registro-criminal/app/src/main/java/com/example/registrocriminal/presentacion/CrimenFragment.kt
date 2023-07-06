package com.example.registrocriminal.presentacion

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.registrocriminal.R
import com.example.registrocriminal.databinding.FragmentCrimenBinding
import com.example.registrocriminal.modelo.Crimen
import com.example.registrocriminal.presentacion.viewmodel.CrimenViewModel
import com.example.registrocriminal.presentacion.viewmodel.CrimenViewModelFactory
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

private const val TAG = "registroCriminal"
private const val FORMATO_FECHA="EEE, MMM, dd"
class CrimenFragment : Fragment() {
    private lateinit var crimen: Crimen
    private var _binding: FragmentCrimenBinding? = null
    private val binding
        get() = checkNotNull(_binding) {

        }
    private val args: CrimenFragmentArgs by navArgs()
    private val crimenViewModel: CrimenViewModel by viewModels {
        CrimenViewModelFactory(args.crimenId)
    }

   // @SuppressLint("StringFormatInvalid") //si no funciona se va a la .
    private fun getReporteCrimen(crimen: Crimen):String{
        val stringCrimenResuelto=if(crimen.resuelto){
            getString(R.string.reporte_resuelto)
        }else{
            getString(R.string.reporte_no_resuelto)
        }
        val stringFecha= android.text.format.DateFormat.format(FORMATO_FECHA, crimen.fecha).toString()
        val textoSospechoso=if(crimen.sospechoso.isBlank()){
            getString(R.string.reporte_sin_sospechoso)
        }else{
            getString(R.string.reporte_con_sospechoso, crimen.sospechoso)
        }
        return getString(
            R.string.reporte_Crimen,
            crimen.titulo,stringFecha,stringCrimenResuelto,textoSospechoso
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimenBinding.inflate(
            layoutInflater,
            container,
            false
        )
        binding.btnSospechoso.apply {
            setOnClickListener{
                selectorSospechoso.launch(null)
            }
        }
        return binding.root
    }

    private val selectorSospechoso=registerForActivityResult(
        ActivityResultContracts.PickContact()
    ){uri:Uri? ->
        uri?.let { obtenerContactosSeleccionado(it)}
    }
    private fun obtenerContactosSeleccionado(UriContacto: Uri){
        val campoConsulta = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val cursoConsulta = requireActivity().contentResolver.query(
            UriContacto, campoConsulta, null, null, null
        )
        cursoConsulta?.use { cursor ->
            if(cursor.moveToFirst()){
                val culpable=cursor.getString(0)
                crimenViewModel.actualizarCrimen { anterior ->
                    anterior.copy(sospechoso = culpable)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.apply {
            txtTituloCrimen.doOnTextChanged { texto, _, _, _ ->
//                crimen = crimen.copy(titulo = texto.toString())
                crimenViewModel.actualizarCrimen { anterior ->
                    anterior.copy(titulo = texto.toString())
                }
            }
            btnFechaCrimen.apply {
//                text = crimen.fecha.toString()
//                isEnabled = false
            }
            chkCrimenResuelto.setOnCheckedChangeListener { _, seleccionado ->
                crimenViewModel.actualizarCrimen { anterior ->
                    anterior.copy(resuelto = seleccionado)
                }
//                crimen = crimen.copy(resuelto = seleccionado)
            }
            btnSospechoso.setOnClickListener{
             selectorSospechoso.launch(null)
            }

        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimenViewModel.crimen.collect { crimen ->
                    crimen?.let { actualizarUI(crimen) }
                }
            }
        }
        setFragmentResultListener(DatePickerFragment.CLAVE_FECHA_SOLICITADA){ _, bundle ->
            val nuevaFecha = bundle.getSerializable(DatePickerFragment.CLAVE_FECHA_BUNDLE) as Date
            crimenViewModel.actualizarCrimen {it.copy(fecha = Date(nuevaFecha.time))}
        }
        setFragmentResultListener(FragmentoTimePicker.CLAVE_HORA_SOLICITADA) { _, bundle ->
            val nuevaHora = bundle.getSerializable(FragmentoTimePicker.CLAVE_HORA_BUNDLE) as Date
            crimenViewModel.actualizarCrimen {it.copy(fecha = Date(nuevaHora.time))}
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val crimeTitle = binding.txtTituloCrimen.text.toString()
                if (crimeTitle.isBlank()) {
                    Toast.makeText(
                        requireContext(),
                        "El título del crimen no puede estar en blanco",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    crimenViewModel.actualizarCrimen { anterior ->
                        anterior.copy(titulo = crimeTitle)
                    }
                    findNavController().navigateUp()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun actualizarUI(crimen: Crimen) {
        binding.apply {
            if (txtTituloCrimen.text.toString() != crimen.titulo) {
                txtTituloCrimen.setText(crimen.titulo)
            }
            btnFechaCrimen.text = crimen.fecha.toString()
            btnHoraCrimen.text = "Actualizar Hora"
            btnHoraCrimen.setOnClickListener{
                findNavController().navigate(
                    CrimenFragmentDirections.actionCrimenFragmentToFragmentoTimePicker(crimen.fecha)
                )
            }
            btnFechaCrimen.setOnClickListener {
                findNavController().navigate(
                    CrimenFragmentDirections.actionCrimenFragmentToDatePickerFragment2(crimen.fecha)
                )
            }
            chkCrimenResuelto.isChecked = crimen.resuelto

            btnReporte.setOnClickListener{
                val intentReporte=Intent(Intent.ACTION_SEND).apply {
                    type="text/plain"
                    putExtra(Intent.EXTRA_TEXT, getReporteCrimen(crimen))
                    putExtra(Intent.EXTRA_SUBJECT, R.string.reporte_asunto)
                }
                val intentSelector=Intent.createChooser(intentReporte,
                getString(R.string.btn_reporte))
                startActivity(intentSelector)
            }
            btnSospechoso.text=crimen.sospechoso.ifEmpty {
                getString(R.string.btn_sospechoso)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragmento_crimen_menu, menu)
    }

    //el item del crimen
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.eliminar_crimen -> {
                eliminarCrimen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun eliminarCrimen() {
        viewLifecycleOwner.lifecycleScope.launch{
            crimenViewModel.eliminarCrimen()
            crimenViewModel.crimenEliminado.observe(viewLifecycleOwner) {
                findNavController().popBackStack()
            }
        }
    }

}
