package com.proyecto.eventos.features.admin.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.proyecto.eventos.features.admin.domain.entities.UsuarioAdminEntidad
import com.proyecto.eventos.features.admin.domain.repositories.AdminRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AdminRemoteDataSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : AdminRepository {

    override fun getUsuarios(): Flow<List<UsuarioAdminEntidad>> = callbackFlow {
        val ref = firebaseDatabase.getReference("usuarios")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = snapshot.children.mapNotNull { child ->
                    val uid = child.key ?: return@mapNotNull null
                    val nombre = child.child("nombre").getValue(String::class.java) ?: "Sin nombre"
                    val email = child.child("email").getValue(String::class.java) ?: ""
                    val rol = child.child("rol").getValue(String::class.java) ?: "usuario"
                    UsuarioAdminEntidad(uid = uid, nombre = nombre, email = email, rol = rol)
                }
                trySend(lista)
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun eliminarUsuario(uid: String): Result<Unit> {
        return try {
            firebaseDatabase.getReference("usuarios").child(uid).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
