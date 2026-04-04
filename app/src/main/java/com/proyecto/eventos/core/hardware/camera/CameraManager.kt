package com.proyecto.eventos.core.hardware.camera


interface CameraManager {

    fun initialize()


    fun takePicture(onResult: (String) -> Unit)


    fun release()
}
