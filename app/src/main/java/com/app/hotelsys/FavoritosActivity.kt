package com.app.hotelsys

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.hotelsys.adapters.HabitacionAdapter
import com.app.hotelsys.helper.AlertaHelper
import com.app.hotelsys.repository.FavoritosRepository
import com.app.hotelsys.ui.PerfilActivity
import com.app.hotelsys.ui.ReservasActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class FavoritosActivity : AppCompatActivity() {

    private lateinit var favoritosRepository: FavoritosRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)

        // === Configurar RecyclerView ===
        val recycler = findViewById<RecyclerView>(R.id.recyclerFavoritos)
        recycler.layoutManager = LinearLayoutManager(this)


        // === Configurar favoritosRepository ===
        favoritosRepository = FavoritosRepository(this)
        val favoritos = favoritosRepository.obtenerFavoritos()
        val favoritosIds = favoritos.map { it.idHabitacion }.toSet()

        if (favoritos.isEmpty()) {
            AlertaHelper.mostrarAlertaToast("No tienes habitaciones favoritas.", this)
        }

        recycler.adapter = HabitacionAdapter(
            favoritos.toMutableList(),
            onClickCalificar = { habitacion, imagen, position ->
                AlertaHelper.mostrarAlertaToast("Abrir reseñas de ${habitacion.nombre}", this)
            },
            onClickReservar = { habitacion ->
                AlertaHelper.mostrarAlertaToast("Reservar ${habitacion.nombre}", this)
            },
            onClickFavorito = { habitacion ->
                favoritosRepository.eliminarFavorito(habitacion.idHabitacion)
                AlertaHelper.mostrarAlertaToast("Eliminado de favoritos 💔", this)
                recreate()
            },
            favoritosIniciales = favoritosIds,
            mostrarBotones = false
        )


        // ====== Menú del toolbar ======
//        enableEdgeToEdge()
//        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
//
//        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
//            val statusBarHeight = insets.getSystemWindowInsetTop()
//            view.updatePadding(top = statusBarHeight)
//            insets
//        }

        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        val emailUsuario = usuarioActual?.email ?: "Invitado"

        val popupMenu = PopupMenu(this, btnMenu)
        popupMenu.menuInflater.inflate(R.menu.menu_toolbar, popupMenu.menu)
        popupMenu.menu.findItem(R.id.nav_micuenta).title = "Mi cuenta ($emailUsuario)"
        btnMenu.setOnClickListener { popupMenu.show() }

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.nav_micuenta -> {
                    AlertaHelper.mostrarAlertaToast("Usuario: $emailUsuario", this)
                    true
                }
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }


        // === Configurar Bottom Navigation ===
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_favoritos

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_habitaciones -> {
                    startActivity(Intent(this, MainActivityIndex::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_favoritos -> true
                R.id.nav_reservas -> {
                    startActivity(Intent(this, ReservasActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }

}
