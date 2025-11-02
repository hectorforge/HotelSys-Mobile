package com.app.hotelsys.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import com.app.hotelsys.FavoritosActivity
import com.app.hotelsys.MainActivity
import com.app.hotelsys.MainActivityIndex
import com.app.hotelsys.R
import com.app.hotelsys.databinding.ActivityPerfilBinding
import com.app.hotelsys.helper.AlertaHelper
import com.app.hotelsys.models.Usuario
import com.app.hotelsys.ui.auth.AuthActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()
    private var usuarioActual: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setupToolbar()
        cargarDatosUsuario()
        setupListeners()
        setupBottomNavigation()
    }

    private fun setupToolbar() {
        // ====== Menú del toolbar ======
//        val toolbar = binding.toolbar
//        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
//            val statusBarHeight = insets.getSystemWindowInsetTop()
//            view.updatePadding(top = statusBarHeight)
//           insets
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
                    cerrarSesion()
                    true
                }
                else -> false
            }
        }
    }

    private fun cargarDatosUsuario() {
        val user = auth.currentUser

        if (user == null) {
            AlertaHelper.mostrarAlerta("Sesión expirada", "Por favor, inicia sesión de nuevo.", this)
            irALogin()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE

        firestore.collection("usuarios")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                binding.progressBar.visibility = View.GONE
                binding.contentLayout.visibility = View.VISIBLE

                if (document.exists()) {
                    usuarioActual = document.toObject(Usuario::class.java)
                    mostrarDatosUsuario(usuarioActual)
                } else {
                    binding.textNombreCompleto.text = user.displayName ?: "Usuario"
                    binding.textEmail.text = user.email ?: "Sin email"
                    binding.textEmailValor.text = user.email ?: "Sin email"
                }
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                binding.contentLayout.visibility = View.VISIBLE
                AlertaHelper.mostrarAlerta("Error", "No se pudieron cargar los datos del usuario.\nError: ${e.localizedMessage}", this)
            }
    }

    private fun mostrarDatosUsuario(usuario: Usuario?) {
        usuario?.let {
            binding.textNombreCompleto.text = "${it.nombre} ${it.apellido}"
            binding.textEmail.text = it.email

            val iniciales = "${it.nombre.firstOrNull() ?: ""}${it.apellido.firstOrNull() ?: ""}"
            binding.textIniciales.text = iniciales.uppercase()

            binding.textNombreValor.text = it.nombre
            binding.textApellidoValor.text = it.apellido
            binding.textEmailValor.text = it.email
            binding.textTelefonoValor.text = it.telefono
            binding.textDniValor.text = it.dni
            binding.textFechaNacimientoValor.text = it.fechaNacimiento
            binding.textFechaRegistroValor.text = it.fechaRegistro
        }
    }

    private fun setupListeners() {
        binding.buttonCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }

        binding.cardEditarPerfil.setOnClickListener {
            AlertaHelper.mostrarAlertaToast("Función de editar perfil próximamente", this)
        }

        binding.cardCambiarPassword.setOnClickListener {
            enviarEmailRecuperacion()
        }
    }

    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                cerrarSesion()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cerrarSesion() {
        auth.signOut()
        AlertaHelper.mostrarAlerta("Sesión cerrada", "Has cerrado sesión exitosamente.", this)
        irALogin()
    }

    private fun enviarEmailRecuperacion() {
        val email = auth.currentUser?.email

        if (email.isNullOrEmpty()) {
            AlertaHelper.mostrarAlerta("Error", "No se encontró un correo electrónico asociado a tu cuenta.", this)
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Cambiar Contraseña")
            .setMessage("Se enviará un correo a $email para restablecer tu contraseña")
            .setPositiveButton("Enviar") { _, _ ->
                auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        AlertaHelper.mostrarAlerta("Correo enviado", "Se ha enviado un correo para restablecer tu contraseña. Revisa tu bandeja de entrada.", this)
                    }
                    .addOnFailureListener { e ->
                        AlertaHelper.mostrarAlerta("Error", "No se pudo enviar el correo de restablecimiento.\nError: ${e.localizedMessage}", this)
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    private fun irALogin() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    private fun setupBottomNavigation() {
        val bottomNav = binding.bottomNav
        bottomNav.selectedItemId = R.id.nav_perfil

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_habitaciones -> {
                    startActivity(Intent(this, MainActivityIndex::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_favoritos -> {
                    startActivity(Intent(this, FavoritosActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_reservas -> {
                    startActivity(Intent(this, ReservasActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_perfil -> true
                else -> false
            }
        }
    }
}