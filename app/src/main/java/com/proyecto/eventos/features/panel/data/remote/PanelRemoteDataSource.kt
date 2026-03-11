package com.proyecto.eventos.features.panel.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.proyecto.eventos.features.panel.domain.repositories.PanelRepository
import com.proyecto.eventos.features.auth.domain.entities.UsuarioEntidad
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PanelRemoteDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) : PanelRepository {

    override suspend fun getUsuarioActual(): UsuarioEntidad? {
        val firebaseUser = firebaseAuth.currentUser ?: return null

        return try {
            val snapshot = firebaseDatabase
                .getReference("usuarios")
                .child(firebaseUser.uid)
                .get()
                .await()

            val nombre = snapshot.child("nombre").getValue(String::class.java)
                ?: firebaseUser.displayName
                ?: "Usuario"
            val rol = snapshot.child("rol").getValue(String::class.java) ?: "usuario"

            UsuarioEntidad(
                uid = firebaseUser.uid,
                nombre = nombre,
                email = firebaseUser.email ?: "",
                rol = rol
            )
        } catch (e: Exception) {
            UsuarioEntidad(
                uid = firebaseUser.uid,
                nombre = firebaseUser.displayName ?: "Usuario",
                email = firebaseUser.email ?: "",
                rol = "usuario"
            )
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun estaAutenticado(): Boolean {
        return firebaseAuth.currentUser != null
    }
}
