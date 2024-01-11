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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityVerBinding.inflate(layoutInflater)
        setContentView(bind.root)
        db = FirebaseDatabase.getInstance().reference
        lista = mutableListOf()
        atras = findViewById(R.id.volver)

        atras.setOnClickListener {
            val activity = Intent(applicationContext, MainActivity::class.java)
            startActivity(activity)
        }

        db.child("Productos").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach { hijo: DataSnapshot?
                    ->
                    val pojo_producto = hijo?.getValue(Producto::class.java)
                    lista.add(pojo_producto!!)
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

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}