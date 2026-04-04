package com.proyecto.eventos.core.hardware.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationManager {
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val geocoder by lazy {
        Geocoder(context, Locale.getDefault())
    }

    override suspend fun getCurrentLocation(): Result<String> {
        return try {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                return Result.failure(Exception("Permiso de ubicación no concedido"))
            }

            val location = fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .await()
                ?: return Result.failure(Exception("No se pudo obtener la ubicación"))

            return try {
                @Suppress("DEPRECATION")
                val address = Geocoder(context, Locale.getDefault())
                    .getFromLocation(location.latitude, location.longitude, 1)
                    ?.firstOrNull()
                    ?.getAddressLine(0)
                    ?: "Lat: ${location.latitude}, Lng: ${location.longitude}"
                Result.success(address)
            } catch (e: Exception) {
                Result.success(
                    "Lat: ${location.latitude}, Lng: ${location.longitude}"
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
