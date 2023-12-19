package com.example.crud

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.crud.databinding.ActivityCrearBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

class Crear : AppCompatActivity() {
    private lateinit var bind: ActivityCrearBinding

    private lateinit var db: DatabaseReference
    private lateinit var st: StorageReference
    private var urlimg: Uri? = null
    private lateinit var listaproductos: MutableList<Producto>
    override fun onCreate(savedInstanceState: Bundle?) {
        bind = ActivityCrearBinding.inflate(layoutInflater)
        db = FirebaseDatabase.getInstance().reference
        st = FirebaseStorage.getInstance().reference

        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        bind.enviar.setOnClickListener {
            if (validar()) {

                var urlimgfirebase = null
//                var urlimgfirebase = guardarEscudo(urlimg!!)
                var nombre = bind.nombre.text.toString()
                var descripcion = bind.descripcion.text.toString()
                var calidad = bind.calidad.rating.toDouble()
                var nuevoproducto = Producto(nombre, descripcion, calidad, urlimgfirebase)

                Utilidades.crearProducto(nuevoproducto)

                limpiar()

                Toast.makeText(this, "Producto guardado con exito", Toast.LENGTH_LONG).show()

            }
        }

        bind.img.setOnClickListener {
            accesoGaleria.launch("image/*")
        }
    }

    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        if (uri != null) {
            urlimg = uri
            bind.img.setImageURI(uri)
        }

    }


    private fun limpiar() {
        bind.nombre.text = null
        bind.descripcion.text = null
        bind.calidad.rating = 0.0F
        bind.img.setImageResource(R.drawable.fotodef)
    }

    private fun validar(): Boolean {
        var bnombre = false
        var bdescripcion = false

        if (!bind.nombre.text.isNullOrBlank()) {
            bnombre = true
            bind.nombre.error = null
        } else {
            bind.nombre.error = "Este campo es obligatorio"
        }

        if (!bind.descripcion.text.isNullOrBlank()) {
            bdescripcion = true
            bind.descripcion.error = null
        } else {
            bind.descripcion.error = "Este campo es obligatorio"
        }

        if (bind.calidad.rating == 0.0F) {
            bind.calidad.rating = 0.5F
        }


        return bnombre && bdescripcion

    }

}