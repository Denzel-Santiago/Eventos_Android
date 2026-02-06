//features/eventos/data/remote/EventosApiService.kt
package com.proyecto.eventos.features.eventos.data.remote

import retrofit2.http.*

interface EventosApiService {

    @GET("events/")
    suspend fun getEventos(): List<EventoDTO>

    @GET("events/{id}")
    suspend fun getEvento(@Path("id") id: Int): EventoDTO

    @POST("events/")
    suspend fun createEvento(@Body evento: CreateEventoRequest): CreateEventoResponse

    @PUT("events/{id}")
    suspend fun updateEvento(
        @Path("id") id: Int,
        @Body evento: CreateEventoRequest
    ): MessageResponse

    @DELETE("events/{id}")
    suspend fun deleteEvento(@Path("id") id: Int): MessageResponse
}