//features/login/data/remote/AuthApiService.kt
package com.proyecto.eventos.features.login.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.*

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun registro(@Body request: RegistroRequest): UsuarioDTO

    @GET("auth/users/{id}")
    suspend fun getUsuario(@Path("id") id: Int): UsuarioDTO

    @DELETE("auth/users/{id}")
    suspend fun deleteUsuario(@Path("id") id: Int): MessageResponse
}

data class MessageResponse(
    @SerializedName("message")
    val message: String
)