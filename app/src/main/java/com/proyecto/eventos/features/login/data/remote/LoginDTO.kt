//features/login/data/remote/AuthApiService.kt
package com.proyecto.eventos.features.login.data.remote

import com.google.gson.annotations.SerializedName
import com.proyecto.eventos.features.login.domain.entities.UsuarioEntidad

data class LoginRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("user")
    val user: UsuarioDTO
)

data class UsuarioDTO(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null
)

fun UsuarioDTO.toEntity(): UsuarioEntidad {
    return UsuarioEntidad(
        id = id,
        username = username,
        email = email,
        role = role
    )
}

data class RegistroRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("role")
    val role: String = "user"
)