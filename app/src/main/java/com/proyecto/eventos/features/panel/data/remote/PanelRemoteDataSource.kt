//com.proyecto.eventos.features.panel.data.remote.PanelRemoteDataSource.kt
package com.proyecto.eventos.features.panel.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.proyecto.eventos.core.network.NetworkMonitor
import com.proyecto.eventos.features.auth.data.local.AuthLocalDataSource
import com.proyecto.eventos.features.auth.domain.entities.UsuarioEntidad
import com.proyecto.eventos.features.panel.domain.repositories.PanelRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PanelRemoteDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    private val authLocalDataSource: AuthLocalDataSource,
    private val networkMonitor: NetworkMonitor
) : PanelRepository {

    override suspend fun getUsuarioActual(): UsuarioEntidad? {

        if (!networkMonitor.isConnected()) {
            return authLocalDataSource.getSesionGuardada()
        }

        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser == null) {
            return authLocalDataSource.getSesionGuardada()
        }

        return try {
            val snapshot = firebaseDatabase
                .getReference("usuarios")
                .child(firebaseUser.uid)
                .get()
                .await()

            val nombre = snapshot.child("nombre")
                .getValue(String::class.java)
                ?: firebaseUser.displayName
                ?: "Usuario"
            val rol = snapshot.child("rol")
                .getValue(String::class.java) ?: "usuario"

            val usuario = UsuarioEntidad(
                uid    = firebaseUser.uid,
                nombre = nombre,
                email  = firebaseUser.email ?: "",
                rol    = rol
            )

            authLocalDataSource.guardarSesion(usuario)
            usuario

        } catch (e: Exception) {
            authLocalDataSource.getSesionGuardada()
                ?: UsuarioEntidad(
                    uid    = firebaseUser.uid,
                    nombre = firebaseUser.displayName ?: "Usuario",
                    email  = firebaseUser.email ?: "",
                    rol    = "usuario"
                )
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override fun estaAutenticado(): Boolean {
        return firebaseAuth.currentUser != null
    }
}