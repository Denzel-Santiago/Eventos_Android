//com.proyecto.eventos.features.auth.data.remote.FirebaseAuthDataSource.kt
package com.proyecto.eventos.features.auth.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.proyecto.eventos.features.auth.domain.entities.UsuarioEntidad
import com.proyecto.eventos.features.auth.domain.repositories.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<UsuarioEntidad> {
        return try {
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

            Result.success(
                UsuarioEntidad(
                    uid = firebaseUser.uid,
                    nombre = nombre,
                    email = firebaseUser.email ?: "",
                    rol = rol
                )
            )
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
        // El rol real se obtiene desde Realtime Database en el login
        // Aquí solo verificamos si hay sesión activa
        return firebaseAuth.currentUser != null
    }
}
