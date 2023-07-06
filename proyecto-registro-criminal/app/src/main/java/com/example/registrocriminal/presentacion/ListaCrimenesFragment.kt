package com.example.registrocriminal.presentacion

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.registrocriminal.R
import com.example.registrocriminal.databinding.FragmentListaCrimenBinding
import com.example.registrocriminal.modelo.Crimen
import com.example.registrocriminal.presentacion.adapter.CrimenAdapter

import com.example.registrocriminal.presentacion.viewmodel.ListaCrimenesViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "ListaCrimenesFragment"

class ListaCrimenesFragment : Fragment() {
    private val listaCrimenesViewModel: ListaCrimenesViewModel by viewModels()
    private var _binding: FragmentListaCrimenBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
        }
    private var job: Job? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentListaCrimenBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)
        binding.crimenRecyclerView.layoutManager = LinearLayoutManager(context)
        Log.v("FRAGMENT", "HOLA")
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                listaCrimenesViewModel.crimenes.collect { crimenes ->
                    if (crimenes.isEmpty()) {
                        Log.d("CRIMENES", "VACIO")
                        binding.layoutEmpty.visibility = View.VISIBLE
                        binding.crimenRecyclerView.visibility = View.INVISIBLE
                        binding.buttonAdd.setOnClickListener {
                            mostrarNuevoCrimen()
                        }
                    } else {
                        Log.d("CRIMENES", "NO VACIO")
                        binding.layoutEmpty.visibility = View.INVISIBLE
                        binding.crimenRecyclerView.visibility = View.VISIBLE
                        binding.crimenRecyclerView.adapter = CrimenAdapter(crimenes) { crimenId ->
                            findNavController().navigate(ListaCrimenesFragmentDirections.mostrarCrimen(crimenId))
                        }
                    }
                }
            }
        }
        return binding.root;
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nuevo_crimen -> {
                mostrarNuevoCrimen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mostrarNuevoCrimen() {
        viewLifecycleOwner.lifecycleScope.launch{
            val nuevoCrimen = Crimen(
                id = UUID.randomUUID(),
                titulo = "",
                fecha = Date(),
                resuelto = false,
                mayor = false
            )
            listaCrimenesViewModel.ingresarCrimen(nuevoCrimen)
            findNavController().navigate(
                ListaCrimenesFragmentDirections.mostrarCrimen(nuevoCrimen.id)
            )
        }
    }
}
