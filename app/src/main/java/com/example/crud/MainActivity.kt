package com.example.crud

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.crud.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bind: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityMainBinding.inflate(layoutInflater)

        setContentView(bind.root)

        //si se presiona el boton crear se abre la actividad crear
        bind.crear.setOnClickListener {
            var intent = Intent(this, Crear::class.java)
            startActivity(intent)
        }

        //sirve para abrir la actividad ver
        bind.ver.setOnClickListener {
            var intent = Intent(this, Ver::class.java)
            startActivity(intent)
        }
    }

}