package com.app.hotelsys.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.hotelsys.databinding.ActivityPerfilBinding
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
            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Error al cargar datos: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Función de editar perfil próximamente", Toast.LENGTH_SHORT).show()
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
        Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()
        irALogin()
    }

    private fun enviarEmailRecuperacion() {
        val email = auth.currentUser?.email

        if (email.isNullOrEmpty()) {
            Toast.makeText(this, "No se encontró email asociado", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Cambiar Contraseña")
            .setMessage("Se enviará un correo a $email para restablecer tu contraseña")
            .setPositiveButton("Enviar") { _, _ ->
                auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Correo enviado. Revisa tu bandeja de entrada", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
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