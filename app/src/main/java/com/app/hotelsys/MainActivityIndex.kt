package com.app.hotelsys

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

class MainActivityIndex : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_index)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)

        // Recuperar rol enviado desde el login
        val rol = intent.getStringExtra("rol")
        Toast.makeText(this, "Has iniciado sesión como: $rol", Toast.LENGTH_LONG).show()

        // Popup menú
        val popupMenu = PopupMenu(this, btnMenu)
        popupMenu.menuInflater.inflate(R.menu.menu_toolbar, popupMenu.menu)

        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.nav_micuenta -> {
                    Toast.makeText(this, "Mi cuenta", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_favoritos -> {
                    Toast.makeText(this, "Favoritos", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_logout -> {
                    Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }

        btnMenu.setOnClickListener {
            popupMenu.show()
        }

        val btnFavorito = findViewById<ImageButton>(R.id.btnFavorito)
        val btnFavorito2 = findViewById<ImageButton>(R.id.btnFavorito2)
        val btnFavorito3 = findViewById<ImageButton>(R.id.btnFavorito3)

        // Función para alternar color del corazón
        fun configurarFavorito(boton: ImageButton) {
            var esFavorito = false
            boton.setOnClickListener {
                esFavorito = !esFavorito
                val color = if (esFavorito) {
                    ContextCompat.getColor(this, android.R.color.black) // activado
                } else {
                    ContextCompat.getColor(this, android.R.color.white) // desactivado
                }
                boton.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
        }

        // Asignar función a cada botón
        configurarFavorito(btnFavorito)
        configurarFavorito(btnFavorito2)
        configurarFavorito(btnFavorito3)
    }
}
