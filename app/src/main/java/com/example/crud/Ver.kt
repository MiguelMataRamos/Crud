package com.example.crud

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Ver : AppCompatActivity() {
    private lateinit var db: DatabaseReference
    private lateinit var lista: MutableList<Producto>
    private lateinit var recycler: RecyclerView
    private lateinit var adaptador: ProductoAdaptador
    private lateinit var atras: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver)
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

    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}