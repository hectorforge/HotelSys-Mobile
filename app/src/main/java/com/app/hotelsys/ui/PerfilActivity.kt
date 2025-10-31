package com.app.hotelsys.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.hotelsys.databinding.ActivityPerfilBinding
import com.app.hotelsys.helper.AlertaHelper
import com.app.hotelsys.models.Usuario
import com.app.hotelsys.ui.auth.AuthActivity
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
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Mi Perfil"
        }
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun cargarDatosUsuario() {
        val user = auth.currentUser

        if (user == null) {
//        Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
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
}