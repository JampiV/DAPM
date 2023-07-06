package com.example.registrocriminal.presentacion

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import java.util.*

private const val TAG = "registroCriminal"
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimenBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
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
//            btnHoraCrimen.text = crimen.fecha.hours.toString()
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
