package com.example.crud

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RatingBar
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crud.databinding.ActivityVerBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Ver : AppCompatActivity() {
    private lateinit var bind : ActivityVerBinding
    private lateinit var db: DatabaseReference
    private lateinit var lista: MutableList<Producto>
    private lateinit var recycler: RecyclerView
    private lateinit var adaptador: ProductoAdaptador
    private lateinit var atras: Button
    private var az : Boolean = false
    private var filtrocalidad : Float? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityVerBinding.inflate(layoutInflater)
        setContentView(bind.root)
        db = FirebaseDatabase.getInstance().reference
        lista = mutableListOf()
        atras = findViewById(R.id.volver)
        var botonaz = findViewById<RadioButton>(R.id.radio_az)
        var botonza = findViewById<RadioButton>(R.id.radio_za)


        atras.setOnClickListener {
            val activity = Intent(applicationContext, MainActivity::class.java)
            startActivity(activity)
        }

        db.child("Productos").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach { hijo: DataSnapshot?
                    ->
                    val pojoproducto = hijo!!.getValue(Producto::class.java)
                    lista.add(pojoproducto!!)
                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })

        adaptador = ProductoAdaptador(lista)
        recycler = findViewById(R.id.lista_productos)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext)
        recycler.setHasFixedSize(true)


        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        bind.lupa.setOnClickListener {
            bind.lupa.visibility = View.INVISIBLE
            bind.close.visibility = View.VISIBLE
            bind.titulo.visibility = View.GONE
            bind.buscador.visibility = View.VISIBLE
            bind.buscador.requestFocus()
            inputMethodManager.showSoftInput(bind.buscador, InputMethodManager.SHOW_IMPLICIT)
        }


        bind.close.setOnClickListener {
            bind.close.visibility = View.INVISIBLE
            bind.lupa.visibility = View.VISIBLE
            bind.titulo.visibility = View.VISIBLE
            bind.buscador.visibility = View.INVISIBLE
            adaptador.filter.filter("")
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
            bind.buscador.setText("")
        }

        bind.filtro.setOnClickListener {
            bind.desplegable.visibility = View.VISIBLE
        }

        bind.radioAz.setOnClickListener {
            comprobarFiltroCalidad()
        }
        bind.radioZa.setOnClickListener {
            comprobarFiltroCalidad()
        }

        bind.radioMayor.setOnClickListener {
            comprobarFiltroNombre()
        }
        bind.radioMenor.setOnClickListener {
            comprobarFiltroNombre()
        }


        bind.aplicar.setOnClickListener {
            Utilidades.az = botonaz.isChecked
            Utilidades.za = botonza.isChecked
            Utilidades.calidadmayor = bind.radioMayor.isChecked
            Utilidades.calidadmenor = bind.radioMenor.isChecked
            bind.desplegable.visibility = View.GONE

            if (Utilidades.az){
                lista.sortBy {
                    it.nombre
                }
            }else if (Utilidades.za){
                lista.sortBy {
                    it.nombre
                }
                lista.reverse()
            }

            if (Utilidades.calidadmayor){
                lista.sortBy { it.calidad }
                lista.reverse()
            }else if (Utilidades.calidadmenor) {
                lista.sortBy { it.calidad }
            }

            recycler.adapter?.notifyDataSetChanged()
        }

        bind.restart.setOnClickListener {
            quitarFiltroCalidad()
            quitarFiltroNombre()
        }

        bind.buscador.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Este método se llama cuando el texto cambia
                val textoIngresado = s.toString()
                // Puedes hacer algo con el texto ingresado aquí
                // Por ejemplo, puedes imprimirlo en la consola
                adaptador.filter.filter((textoIngresado))
            }

            override fun afterTextChanged(p0: Editable?) {
                null
            }


        })



    }


    fun comprobarFiltroNombre(){
        if (bind.radioAz.isChecked || bind.radioZa.isChecked){
            quitarFiltroNombre()
        }
    }

    fun comprobarFiltroCalidad(){
        if (bind.radioMayor.isChecked || bind.radioMenor.isChecked){
            quitarFiltroCalidad()
        }
    }
    fun quitarFiltroNombre(){
        Utilidades.az = false
        Utilidades.za = false
        bind.radioGroup.clearCheck()
    }
    fun quitarFiltroCalidad(){
        Utilidades.calidadmayor = false
        Utilidades.calidadmenor = false
        bind.radioGroup2.clearCheck()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}