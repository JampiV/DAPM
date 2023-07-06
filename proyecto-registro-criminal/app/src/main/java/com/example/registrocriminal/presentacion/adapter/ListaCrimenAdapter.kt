package com.example.registrocriminal.presentacion.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.registrocriminal.databinding.ItemCrimenBinding
import com.example.registrocriminal.databinding.ItemCrimenMayorBinding
import com.example.registrocriminal.modelo.Crimen
import java.util.UUID

class CrimenHolder(
    val binding: ItemCrimenBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun enlazar(crimen: Crimen, onCrimenPulsado: (crimenId: UUID) -> Unit) {
        binding.apply {
            tituloCrimen.text = crimen.titulo
            fechaCrimen.text = crimen.fecha.toString()
            root.setOnClickListener {
//                Toast.makeText(
//                    root.context,
//                    "${crimen.titulo}",
//                    Toast.LENGTH_LONG
//                ).show()
                onCrimenPulsado(crimen.id)
            }
        }
    }

}

class CrimenMayorHolder(
    val binding: ItemCrimenMayorBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun enlazar(crimen: Crimen) {
        binding.apply {
            tituloCrimen.text = crimen.titulo
            fechaCrimen.text = crimen.fecha.toString()
            root.setOnClickListener {
                Toast.makeText(
                    root.context,
                    "${crimen.titulo}",
                    Toast.LENGTH_LONG
                ).show()
            }
            this.botonContactar.setOnClickListener {
                Toast.makeText(
                    root.context,
                    "Crimen Mayor",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

class CrimenAdapter(
    private val crimenes: List<Crimen>,
    private val onCrimenPulsado: (crimenId: UUID) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val CRIMEN_NORMAL = 1
        private const val CRIMEN_MAYOR = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            CRIMEN_NORMAL -> {
                val binding = ItemCrimenBinding.inflate(inflater, parent, false)
                CrimenHolder(binding)
            }

            CRIMEN_MAYOR -> {
                val binding = ItemCrimenMayorBinding.inflate(inflater, parent, false)
                CrimenMayorHolder(binding)
            }

            else -> throw IllegalArgumentException("Unsupported view type: $viewType")
        }
//        val binding = ItemCrimenBinding.inflate(inflater, parent, false)
//        return CrimenHolder(binding)
    }

    override fun getItemCount() = crimenes.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val crimen = crimenes.get(position)
        when (holder.itemViewType) {
            CRIMEN_NORMAL -> (holder as CrimenHolder).enlazar(crimen, onCrimenPulsado)
            CRIMEN_MAYOR -> (holder as CrimenMayorHolder).enlazar(crimen)
            else -> throw IllegalArgumentException("Unsupported")
        }
//        holder.enlazar(crimen)
    }

    override fun getItemViewType(position: Int): Int {
        val crimen = crimenes[position]
        if (crimen.mayor) {
            return CRIMEN_MAYOR
        }
        return CRIMEN_NORMAL
    }
}
