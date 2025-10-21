package com.app.hotelsys.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.hotelsys.R
import com.app.hotelsys.ui.auth.AuthActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivityIndex : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var txtTitulo: TextView
    private lateinit var btnMenu: ImageButton
    private lateinit var logoHotel: ImageView
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_index)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        txtTitulo = findViewById(R.id.txtTitulo)
        btnMenu = findViewById(R.id.btnMenu)
        logoHotel = findViewById(R.id.logoHotel)
        bottomNav = findViewById(R.id.bottomNav)

        val uid = user.uid
        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nombre = document.getString("nombre") ?: "Cliente"
                    txtTitulo.text = "Hola, $nombre 👋"
                } else {
                    txtTitulo.text = "HotelSys App"
                }
            }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_buscar -> {
                    startActivity(Intent(this, BuscarHabitacionesActivity::class.java))
                    true
                }
                R.id.nav_favoritos -> {
                    startActivity(Intent(this, FavoritosActivity::class.java))
                    true
                }
                R.id.nav_reservas -> {
                    startActivity(Intent(this, ReservasActivity::class.java))
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    true
                }
                else -> false
            }
        }

        btnMenu.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }
}
