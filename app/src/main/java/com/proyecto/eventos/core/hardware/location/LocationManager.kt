package com.proyecto.eventos.core.hardware.location


interface LocationManager {

    suspend fun getCurrentLocation(): Result<String>
}
