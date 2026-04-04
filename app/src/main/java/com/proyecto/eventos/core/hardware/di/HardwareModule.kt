package com.proyecto.eventos.core.hardware.di

import com.proyecto.eventos.core.hardware.camera.CameraManager
import com.proyecto.eventos.core.hardware.camera.CameraManagerImpl
import com.proyecto.eventos.core.hardware.location.LocationManager
import com.proyecto.eventos.core.hardware.location.LocationManagerImpl
import com.proyecto.eventos.core.hardware.vibration.VibrationManager
import com.proyecto.eventos.core.hardware.vibration.VibrationManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class HardwareModule {


    @Binds
    @Singleton
    abstract fun bindCameraManager(
        impl: CameraManagerImpl
    ): CameraManager


    @Binds
    @Singleton
    abstract fun bindLocationManager(
        impl: LocationManagerImpl
    ): LocationManager


    @Binds
    @Singleton
    abstract fun bindVibrationManager(
        impl: VibrationManagerImpl
    ): VibrationManager
}
