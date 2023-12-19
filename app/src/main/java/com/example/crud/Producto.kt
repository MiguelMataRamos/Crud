package com.example.crud

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Producto(
    var nombre: String? = null,
    var descripcion: String? = null,
    var calidad: Double? = null,
    var imagen: String? = null
) : Parcelable
