//com.proyecto.eventos.features.auth.data.remote.FirebaseAuthDataSource.kt
package com.proyecto.eventos.features.auth.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.proyecto.eventos.features.auth.data.local.AuthLocalDataSource
import com.proyecto.eventos.features.auth.domain.entities.UsuarioEntidad
import com.proyecto.eventos.features.auth.domain.repositories.AuthRepository
import com.proyecto.eventos.core.network.NetworkMonitor
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    private val authLocalDataSource: AuthLocalDataSource,
    private val networkMonitor: NetworkMonitor
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<UsuarioEntidad> {
        return try {
            // Sin internet: usar sesión guardada en Room
            if (!networkMonitor.isConnected()) {
                val sesionLocal = authLocalDataSource.getSesionGuardada()
                return if (sesionLocal != null) {
                    Result.success(sesionLocal)
                } else {
                    Result.failure(Exception("Sin conexión y no hay sesión guardada"))
                }
            }

            // Con internet: Firebase normal
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
                ?: return Result.failure(Exception("Error al obtener usuario"))

            // Obtener datos adicionales desde Realtime Database
            val snapshot = firebaseDatabase
                .getReference("usuarios")
                .child(firebaseUser.uid)
                .get()
                .await()

            val nombre = snapshot.child("nombre").getValue(String::class.java) ?: ""
            val rol = snapshot.child("rol").getValue(String::class.java) ?: "usuario"

            val usuario = UsuarioEntidad(
                uid = firebaseUser.uid,
                nombre = nombre,
                email = firebaseUser.email ?: "",
                rol = rol
            )

            // Guardar en Room via AuthLocalDataSource
            authLocalDataSource.guardarSesion(usuario)

            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registro(
        nombre: String,
        email: String,
        password: String
    ): Result<UsuarioEntidad> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
                ?: return Result.failure(Exception("Error al crear usuario"))

            // Guardar datos adicionales en Realtime Database
            val usuarioData = mapOf(
                "nombre" to nombre,
                "email" to email,
                "rol" to "usuario"
            )

            firebaseDatabase
                .getReference("usuarios")
                .child(firebaseUser.uid)
                .setValue(usuarioData)
                .await()

            Result.success(
                UsuarioEntidad(
                    uid = firebaseUser.uid,
                    nombre = nombre,
                    email = email,
                    rol = "usuario"
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
        authLocalDataSource.cerrarSesion()
    }

    override fun getUsuarioActual(): UsuarioEntidad? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        return UsuarioEntidad(
            uid = firebaseUser.uid,
            nombre = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            rol = "usuario"
        )
    }

    override fun estaAutenticado(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun esAdmin(): Boolean {

        return firebaseAuth.currentUser != null
    }
}
