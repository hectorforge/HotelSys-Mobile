package com.app.hotelsys.ui.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.hotelsys.R
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.hotelsys.MainActivity
import com.app.hotelsys.databinding.FragmentRegisterBinding
import com.app.hotelsys.models.ClienteRequest
import com.app.hotelsys.models.Usuario
import com.app.hotelsys.retrofit.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding.editTextFechaNacimiento.setOnClickListener {
            val calendario = Calendar.getInstance()
            val year = calendario.get(Calendar.YEAR)
            val month = calendario.get(Calendar.MONTH)
            val day = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    val fecha = String.format("%02d/%02d/%04d", d, m + 1, y)
                    binding.editTextFechaNacimiento.setText(fecha)
                },
                year, month, day
            )
            datePicker.show()
        }

        binding.buttonRegistrar.setOnClickListener {
            val nombre = binding.editTextNombre.text.toString().trim()
            val apellido = binding.editTextApellido.text.toString().trim()
            val fechaNacimiento = binding.editTextFechaNacimiento.text.toString().trim()
            val correo = binding.editTextCorreo.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            val telefono = binding.editTextTelefono.text.toString().trim()
            val dni = binding.editTextDni.text.toString().trim()

            if (nombre.isEmpty() || apellido.isEmpty() || fechaNacimiento.isEmpty() ||
                correo.isEmpty() || password.isEmpty() || telefono.isEmpty() || dni.isEmpty()
            ) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!dni.matches(Regex("^\\d{8}$"))) {
                Toast.makeText(requireContext(), "El DNI debe tener exactamente 8 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!telefono.matches(Regex("^9\\d{8}$"))) {
                Toast.makeText(requireContext(), "El número de teléfono debe tener 9 dígitos y empezar con 9", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(correo, password)
                .addOnSuccessListener {
                    val user = auth.currentUser ?: return@addOnSuccessListener

                    val usuario = Usuario(
                        id = user.uid,
                        nombre = nombre,
                        apellido = apellido,
                        fechaNacimiento = fechaNacimiento,
                        email = correo,
                        telefono = telefono,
                        dni = dni,
                        fechaRegistro = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    )

                    val profileUpdates = userProfileChangeRequest {
                        displayName = "$nombre $apellido"
                    }
                    user.updateProfile(profileUpdates)

                    firestore.collection("usuarios")
                        .document(user.uid)
                        .set(usuario)
                        .addOnSuccessListener {

                            lifecycleScope.launch {
                                try {
                                    val request = ClienteRequest(
                                        nombreCompleto = "${usuario.nombre} ${usuario.apellido}",
                                        numeroDocumento = usuario.dni,
                                        email = usuario.email,
                                        telefono = usuario.telefono,
                                        activo = true,
                                        tipoDocumentoId = 1
                                    )

                                    val response = RetrofitInstance.api.createCliente(request)

                                    if (response.isSuccessful) {
                                        Toast.makeText(requireContext(), "Cliente registrado en servidor", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(requireContext(), "Error al registrar en servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                                    }

                                    Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(requireContext(), MainActivity::class.java))
                                    requireActivity().finish()

                                } catch (e: Exception) {
                                    Log.i("TEST_CONEXION", "Error: ${e.localizedMessage}")
                                    Toast.makeText(requireContext(), "Error conexión API: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            }

                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Error al guardar: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.buttonCancelar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.textVolverLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
