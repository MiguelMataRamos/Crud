package com.example.crud

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
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
        bind.img.setImageURI(pojoproducto.imagen!!.toUri())

        Glide.with(applicationContext)
            .load(pojoproducto.imagen)
            .apply(Utilidades.opcionesGlide(applicationContext))
            .transition(Utilidades.transicion)
            .into(bind.img)


        bind.editar.setOnClickListener {
            if (validar()) {
                var nombre = bind.nombre.text.toString()
                var descripcion = bind.descripcion.text.toString()
                var calidad = bind.calidad.rating.toDouble()
                var url_escudo_firebase : String
                var estado_noti = Estado.MODIFICADO
                var androidId = Settings.Secure.getString(contentResolver,Settings.Secure.ANDROID_ID)
                launch {
                    if (urlimg == null){
                        url_escudo_firebase = pojoproducto.imagen!!
                    }else{
                        url_escudo_firebase = Utilidades.guardarEscudo(pojoproducto.id!!, urlimg!!)
                    }

                    var nuevoproducto = Producto(pojoproducto.id, nombre, descripcion, calidad, url_escudo_firebase , Utilidades.fecha(),estado_noti, androidId)
                    Utilidades.crearProducto(db, pojoproducto.id!!, nuevoproducto)

                }

                limpiar()

                Toast.makeText(this, "Producto editado con exito", Toast.LENGTH_LONG).show()

                var intent = Intent(this, Ver::class.java)
                startActivity(intent)

            }
        }

        bind.img.setOnClickListener {
            showImagePickerDialog()

        }


    }


    private fun showImagePickerDialog() {
        val items = arrayOf("Galería")

        AlertDialog.Builder(this)
            .setTitle("Elegir una fuente de imagen")
            .setItems(items) { _, posicion ->
                when (posicion) {
                    0 -> accesoGaleria.launch("image/*")

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
        listaproductos = Utilidades.obtenerListaProductos(db)
        var bnombre = false
        var bdescripcion = false
        var bexiste = false


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

        if (Utilidades.existeProducto(listaproductos, bind.nombre.text.toString().trim())) {
            Toast.makeText(applicationContext, "Ese Club ya existe", Toast.LENGTH_SHORT)
                .show()
        }else{
            bexiste = true
        }


        return bnombre && bdescripcion && bexiste

    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this, Ver::class.java)
        startActivity(intent)
    }


}