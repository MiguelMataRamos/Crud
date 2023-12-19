package com.example.crud

import android.net.Uri
import java.io.Serializable


data class Producto(
    var nombre: String? = null,
    var descripcion: String? = null,
    var calidad: Double? = null,
    var imagen: String? = null
) : Serializable
