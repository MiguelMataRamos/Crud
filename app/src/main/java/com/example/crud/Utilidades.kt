package com.example.crud

import android.net.Uri
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class Utilidades {
    companion object{
        private var db = FirebaseDatabase.getInstance().reference
        private var st= FirebaseStorage.getInstance().reference

        fun crearProducto(producto: Producto) {
            db.child("Productos").child(producto.nombre!!).setValue(producto)
        }

        suspend fun guardarEscudo(imagen: Uri): String {
            lateinit var urlimagenfirebase: Uri

            urlimagenfirebase = st.child("Productos").child("Imagenes")
                .putFile(imagen).await().storage.downloadUrl.await()

            return urlimagenfirebase.toString()
        }
    }
}