package com.example.crud

import android.app.AlertDialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.crud.databinding.ActivityEditarProductoBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EditarProducto : AppCompatActivity(), CoroutineScope {
    private lateinit var bind : ActivityEditarProductoBinding
    private lateinit var db: DatabaseReference
    private var urlimg: Uri? = null
    private lateinit var listaproductos: MutableList<Producto>

    private lateinit var job: Job
    override fun onCreate(savedInstanceState: Bundle?) {
        bind = ActivityEditarProductoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        db = FirebaseDatabase.getInstance().reference
        job = Job()

        var pojoproducto = intent.getParcelableExtra<Producto>("Producto")!!

        bind.nombre.setText(pojoproducto.nombre)
        bind.calidad.rating = pojoproducto.calidad!!.toFloat()
        bind.descripcion.setText(pojoproducto.descripcion)

        urlimg = pojoproducto.imagen!!.toUri()

        Glide.with(applicationContext)
            .load(urlimg)
            .apply(Utilidades.opcionesGlide(applicationContext))
            .transition(Utilidades.transicion)
            .into(bind.img)


        bind.enviar.setOnClickListener {
            if (validar()) {
                var nombre = bind.nombre.text.toString()
                var descripcion = bind.descripcion.text.toString()
                var calidad = bind.calidad.rating.toDouble()
                launch {
                    var urlimgfirebase = Utilidades.guardarEscudo(nombre, urlimg!!)

                    var nuevoproducto = Producto(nombre, descripcion, calidad, urlimgfirebase)

                    Utilidades.crearProducto(nuevoproducto)

                }

                limpiar()

                Toast.makeText(this, "Producto guardado con exito", Toast.LENGTH_LONG).show()

            }
        }

        bind.img.setOnClickListener {
            showImagePickerDialog()

        }


    }


    private fun showImagePickerDialog() {
        val items = arrayOf("Cámara", "Galería")

        AlertDialog.Builder(this)
            .setTitle("Elegir una fuente de imagen")
            .setItems(items) { _, posicion ->
                when (posicion) {
                    0 -> "print"
                    1 -> accesoGaleria.launch("image/*")

                }
            }
            .show()
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
        listaproductos = Utilidades.obtenerListaClubs()
        var bnombre = false
        var bdescripcion = false
        var bexiste = false
        var bimagen = false

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

        if (Utilidades.existeClub(listaproductos, bind.nombre.text.toString().trim())) {
            Toast.makeText(applicationContext, "Ese Club ya existe", Toast.LENGTH_SHORT)
                .show()
        }else{
            bexiste = true
        }

        if (urlimg != null){
            bimagen = true
        }else{
            Toast.makeText(applicationContext, "Debes insertar una imagen", Toast.LENGTH_SHORT)
                .show()
        }


        return bnombre && bdescripcion && bexiste && bimagen

    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}