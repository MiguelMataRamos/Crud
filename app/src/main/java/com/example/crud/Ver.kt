package com.example.crud

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Ver : AppCompatActivity() {
    private lateinit var db: DatabaseReference
    private  lateinit var lista:MutableList<Producto>
    private lateinit var recycler: RecyclerView
    private lateinit var adaptador: ProductoAdaptador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver)
        db = FirebaseDatabase.getInstance().reference

    }
}