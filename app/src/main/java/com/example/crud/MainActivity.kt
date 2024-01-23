package com.example.crud

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.crud.databinding.ActivityMainBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {

    private lateinit var bind: ActivityMainBinding
    private lateinit var generador: AtomicInteger
    private lateinit var androidId: String
    private lateinit var db_ref: DatabaseReference

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

        crearCanalNotificacion()
        androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        db_ref = FirebaseDatabase.getInstance().reference
        generador = AtomicInteger(0)

        db_ref.child("Productos")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val pojo_producto = snapshot.getValue(Producto::class.java)
                    if (!pojo_producto!!.user_noti.equals(androidId) && pojo_producto!!.estado_noti == Estado.CREADO) {
                        db_ref.child("Productos").child(pojo_producto.id!!).child("estado_noti").setValue(Estado.NOTIFICADO)
                        lanzarNotificacion(generador.incrementAndGet(), pojo_producto,
                            "Se ha creado un nuevo producto " + pojo_producto.nombre,
                            "Nuevos datos en la app",
                            Ver::class.java
                        )
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val pojo_producto = snapshot.getValue(Producto::class.java)
                    if (!pojo_producto!!.user_noti.equals(androidId) && pojo_producto!!.estado_noti == Estado.MODIFICADO) {
                        db_ref.child("Productos").child(pojo_producto.id!!)
                            .child("estado_noti").setValue(Estado.NOTIFICADO)
                        lanzarNotificacion(generador.incrementAndGet(), pojo_producto,
                            "Se ha editado el club " + pojo_producto.nombre,
                            "Datos modificados en la app",
                            EditarProducto::class.java
                        )
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val pojo_producto = snapshot.getValue(Producto::class.java)
                    if (!pojo_producto!!.user_noti.equals(androidId)){
                        lanzarNotificacion(generador.incrementAndGet(), pojo_producto,
                            "Se ha eliminado el club " + pojo_producto.nombre,
                            "Datos eliminados en la app",
                            Ver::class.java
                        )
                    }

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

    fun crearCanalNotificacion(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Notificacion"
            val descripcion = "Canal de notificacion"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel("Canal", nombre, importancia).apply {
                this.description = descripcion
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }

    fun lanzarNotificacion(id_noti: Int, pojo: Parcelable, contenido: String, titulo: String, destino: Class<*>){
        val actividad = Intent(this, destino)
        actividad.putExtra("Producto", pojo)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            actividad,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Crea la notificación
        val builder = NotificationCompat.Builder(this, "Canal")
            .setSmallIcon(R.drawable.recetas)
            .setContentTitle(titulo)
            .setContentText(contenido)
            .setSubText("sistema de informacion")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Muestra la notificación
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
            }
            notify(id_noti, builder)
        }
    }

}