package com.app.hotelsys

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.hotelsys.models.Usuario
import com.app.hotelsys.ui.auth.AuthActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Si no hay usuario logeado, redirigir al AuthActivity
        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        // Referencias UI
        val txtBienvenida = findViewById<TextView>(R.id.txtBienvenida)
        val txtNombre = findViewById<TextView>(R.id.txtNombre)
        val txtApellido = findViewById<TextView>(R.id.txtApellido)
        val txtFechaNacimiento = findViewById<TextView>(R.id.txtFechaNacimiento)
        val txtEmail = findViewById<TextView>(R.id.txtEmail)
        val txtFechaRegistro = findViewById<TextView>(R.id.txtFechaRegistro)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Obtener datos del usuario desde Firestore
        val uid = user.uid
        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val usuario = document.toObject(Usuario::class.java)
                    if (usuario != null) {
                        txtBienvenida.text = "¡Bienvenido, ${usuario.nombre}!"
                        txtNombre.text = "Nombre: ${usuario.nombre}"
                        txtApellido.text = "Apellido: ${usuario.apellido}"
                        txtFechaNacimiento.text = "Fecha de nacimiento: ${usuario.fechaNacimiento}"
                        txtEmail.text = "Correo: ${usuario.email}"
                        txtFechaRegistro.text = "Fecha de registro: ${usuario.fechaRegistro}"
                    }
                } else {
                    txtBienvenida.text = "No se encontraron tus datos."
                }
            }
            .addOnFailureListener {
                txtBienvenida.text = "Error al obtener los datos del usuario."
            }

        // Botón cerrar sesión
        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }
}
