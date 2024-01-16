package com.example.crud

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ProductoAdaptador(private val lista_producto: MutableList<Producto>) :
    RecyclerView.Adapter<ProductoAdaptador.ProductoViewHolder>(), Filterable {

    private lateinit var contexto: Context
    private var lista_filtrada = lista_producto

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagen: ImageView = itemView.findViewById(R.id.img)
        val nombre: TextView = itemView.findViewById(R.id.nombre)
        val calidad: RatingBar = itemView.findViewById(R.id.calidad)
        val descripcion: TextView = itemView.findViewById(R.id.descripcion)
        val editar: ImageView = itemView.findViewById(R.id.editar)
        val eliminar: ImageView = itemView.findViewById(R.id.borrar)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        contexto = parent.context
        return ProductoViewHolder(vista_item)
    }

    override fun getItemCount(): Int = lista_filtrada.size


    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val item_actual = lista_filtrada[position]
        holder.nombre.text = item_actual.nombre
        holder.calidad.rating = item_actual.calidad!!.toFloat()
        holder.descripcion.text = item_actual.descripcion

        val URL: String? = when (item_actual.imagen) {
            "" -> null
            else -> item_actual.imagen
        }

        Glide.with(contexto)
            .load(URL)
            .apply(Utilidades.opcionesGlide(contexto))
            .transition(Utilidades.transicion)
            .into(holder.imagen)

        holder.editar.setOnClickListener {
            val activity = Intent(contexto, EditarProducto::class.java)
            activity.putExtra("Producto", item_actual)
            contexto.startActivity(activity)
        }

        holder.eliminar.setOnClickListener {
            var builder = AlertDialog.Builder(contexto)
            builder.setTitle("Eliminar Producto")
            builder.setMessage("Estas seguro que quieres eliminar el producto: " + item_actual.nombre + "?")
            builder.setPositiveButton("Si") { _, _ ->
                val db = FirebaseDatabase.getInstance().reference
                val st = FirebaseStorage.getInstance().reference
                lista_filtrada.remove(item_actual)
                st.child("Productos").child(item_actual.id!!).delete()
                db.child("Productos").child(item_actual.id!!).removeValue()

                Toast.makeText(contexto, "Club borrado con exito", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("NO") { _, _ -> }
            builder.show()

        }

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val busqueda = p0.toString().lowercase()
                if (busqueda.isEmpty()) {
                    lista_filtrada = lista_producto
                } else {
                    lista_filtrada = (lista_producto.filter {
                        it.nombre.toString().lowercase().contains(busqueda)
                    }) as MutableList<Producto>
                }

                val filterResults = FilterResults()
                filterResults.values = lista_filtrada

                return filterResults

            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }
}


