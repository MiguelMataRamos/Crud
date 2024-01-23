package com.example.crud

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Producto(
    var id: String? = null,
    var nombre: String? = null,
    var descripcion: String? = null,
    var calidad: Double? = null,
    var imagen: String? = null,
    var fecha: String? = null,
    var estado_noti: Int? = null,
    var user_noti: String? = null
) : Parcelable
