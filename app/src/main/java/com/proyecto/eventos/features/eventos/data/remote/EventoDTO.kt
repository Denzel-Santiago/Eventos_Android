package com.proyecto.eventos.features.eventos.data.remote

import com.google.gson.annotations.SerializedName
import com.proyecto.eventos.features.eventos.domain.entities.EventoEntidad

data class EventoDTO(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("location")
    val location: String,

    @SerializedName("date")
    val date: String,

    @SerializedName("available_tickets")
    val availableTickets: Int,

    @SerializedName("price")
    val price: Double,

    @SerializedName("created_at")
    val createdAt: String? = null
)

fun EventoDTO.toEntity(): EventoEntidad {
    return EventoEntidad(
        id = id,
        nombre = name,
        ubicacion = location,
        fecha = date,
        boletosDisponibles = availableTickets,
        precio = price
    )
}

data class CreateEventoRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("location")
    val location: String,

    @SerializedName("date")
    val date: String,

    @SerializedName("available_tickets")
    val availableTickets: Int,

    @SerializedName("price")
    val price: Double
)

data class CreateEventoResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("evento")
    val evento: EventoDTO
)

data class MessageResponse(
    @SerializedName("message")
    val message: String
)