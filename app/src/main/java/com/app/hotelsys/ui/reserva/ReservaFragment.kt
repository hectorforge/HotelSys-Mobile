package com.app.hotelsys.ui.reserva

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.app.hotelsys.R
import com.app.hotelsys.databinding.FragmentReservaBinding
import com.app.hotelsys.models.ProductoReservaRequest
import com.app.hotelsys.models.ReservaRequest
import com.app.hotelsys.repository.ReservaRepository
import com.app.hotelsys.ui.auth.AuthActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import java.util.Calendar

class ReservaFragment : BottomSheetDialogFragment() {

    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentReservaBinding? = null
    private val binding get() = _binding!!
    private val repository = ReservaRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()

        _binding = FragmentReservaBinding.inflate(inflater, container, false)
        val view = binding.root

        configurarVista()
        configurarEventos()

        return view
    }

    private fun configurarVista(){
        usuarioFireBase()
    }

    private fun configurarEventos(){
        //Reservar
        binding.btnRealizarReserva.setOnClickListener {
            crearReservaEjemplo()
        }
        //Fecha Ingreso
        binding.editTextIngreso.setOnClickListener {
            mostrarCalendario(binding.editTextIngreso)
        }
        //Fecha Salida
        binding.editTextSalida.setOnClickListener {
            mostrarCalendario(binding.editTextSalida)
        }
        //Cancelar
        binding.btnCancelar.setOnClickListener {
            obtenerIdCliente()
            obtenerProductos()
        }
    }
    
    //VISTA
    private fun usuarioFireBase(){
        val usuario = auth.currentUser
        if (usuario != null) {
            val uid = usuario.uid
            val correo = usuario.email
            val nombre = usuario.displayName
            val telefono = usuario.phoneNumber

            Log.i("FIREBASE", "UID: $uid")
            Log.i("FIREBASE", "Correo: $correo")
            Log.i("FIREBASE", "Nombre: $nombre")

            binding.txtNombreCompleto.setText(nombre ?: "Sin nombre")
            binding.txtCorreo.setText(correo ?: "Sin Correo")
            binding.txtTelefono.setText(telefono ?: "Sin Telefono")
        } else {
            Log.w("FIREBASE", "No hay usuario autenticado")
        }
    }

    private fun mostrarCalendario(campo: TextInputEditText){
        val calendario = Calendar.getInstance()
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, y, m, d ->
                val fecha = String.format("%04d-%02d-%02d", y, m + 1, d)
                campo.setText(fecha)
            },
            year, month, day
        )
        datePicker.show()
    }

    private fun obtenerProductos(){
        viewLifecycleOwner.lifecycleScope.launch {
            val response = repository.obtenerProductos()
            if (response.isSuccessful){
                val listaProductos = response.body()
                listaProductos?.forEach {
                    Log.i("PROD", "Producto: ${it.nombreProducto} - S/.${it.precio}")
                }
            } else {
                Log.e("PROD", "Error código: ${response.code()}")
            }
        }
    }

    private fun obtenerIdCliente() {
        val usuarioActual = auth.currentUser?.email.toString()
        viewLifecycleOwner.lifecycleScope.launch {
            val response = repository.obtenerClientePorCorreo(usuarioActual)
            Log.i("CLI", "Código de respuesta: ${response.code()}")
            if (response.isSuccessful) {
                val cliente = response.body()
                Log.i("CLI", "Cliente: $cliente")
            } else {
                Log.e("CLI", "Error: ${response.errorBody()?.string()}")
            }
        }
    }

    // EVENTOS
    private fun crearReservaEjemplo() {
        val reserva = ReservaRequest(
            clienteId = 1,
            fechaCheckIn = "2025-10-25",
            fechaCheckOut = "2025-10-30",
            descuento = "15.5",
            estadoReservaId = 1,
            habitacionIds = listOf(5),
            productos = listOf(ProductoReservaRequest(1, 2))
        )

        val gson = GsonBuilder().setPrettyPrinting().create()
        Log.i("JSON_ENVIADO", gson.toJson(reserva))

        lifecycleScope.launch {
            val response = repository.crearReserva(reserva)
            if (response.isSuccessful) {
                Log.i("RESERVA", "Reserva creada con ID: ${response.body()?.id}")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("ERROR", "Falló la reserva: $errorBody")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}