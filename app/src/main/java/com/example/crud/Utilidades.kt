package com.example.crud

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.fido.fido2.api.common.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date

class Utilidades {
    companion object {
        private var st = FirebaseStorage.getInstance().reference
        var za = false
        var az = false
        var calidadmayor = false
        var calidadmenor = false

        fun existeProducto(productos: List<Producto>, nombre: String): Boolean {
            return productos.any {
                it.nombre!!.lowercase() == nombre.lowercase()
            }
        }

        fun obtenerListaProductos(db_ref: DatabaseReference): MutableList<Producto> {
            var lista = mutableListOf<Producto>()

            db_ref.child("Productos")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { hijo: DataSnapshot ->
                            val pojo_producto = hijo.getValue(Producto::class.java)
                            lista.add(pojo_producto!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })

            Log.d("TAM", lista.size.toString())
            return lista
        }

        fun crearProducto(db_ref: DatabaseReference, id: String, producto: Producto) {
            db_ref.child("Productos").child(id).setValue(producto)
        }

        fun fecha(): String {
            var fecha = Date()
            val formato = SimpleDateFormat("dd-MM-yyy HH:mm:ss")

            return formato.format(fecha)
        }

        suspend fun guardarEscudo(id: String, imagen: Uri): String {
            lateinit var urlimagenfirebase: Uri

            urlimagenfirebase = st.child("Productos").child(id)
                .putFile(imagen).await().storage.downloadUrl.await()

            return urlimagenfirebase.toString()
        }


        fun animacion_carga(contexto: Context): CircularProgressDrawable {
            val animacion = CircularProgressDrawable(contexto)
            animacion.strokeWidth = 5f
            animacion.centerRadius = 30f
            animacion.start()
            return animacion
        }


        val transicion = DrawableTransitionOptions.withCrossFade(500)

        fun opcionesGlide(context: Context): com.bumptech.glide.request.RequestOptions {
            val options = com.bumptech.glide.request.RequestOptions()
                .placeholder(animacion_carga(context))
                .fallback(R.drawable.fotodef)
                .error(R.drawable.fotodef)
            return options
        }


    }
}