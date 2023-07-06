package com.example.registrocriminal.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Crimen(
    @PrimaryKey
    val id: UUID,
    val titulo: String,
    val fecha: Date,
    val resuelto: Boolean,
    val mayor: Boolean
)
