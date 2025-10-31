package com.app.hotelsys.ui.reserva

import com.app.hotelsys.R
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.hotelsys.databinding.FragmentReservaBinding
import com.app.hotelsys.models.ProductoReservaRequest
import com.app.hotelsys.models.ProductoResponse
import com.app.hotelsys.models.ReservaRequest
import com.app.hotelsys.repository.ReservaRepository
import com.bumptech.glide.Glide
import com.example.repaso_recycler.adaptador.ProductosReservaAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReservaFragment : BottomSheetDialogFragment() {

    // --- Responsabilidad de la clase ---
    // Fragmento que muestra un formulario para crear reservas.
    // - Muestra datos de la habitación
    // - Rellena datos del usuario desde Firestore
    // - Permite seleccionar fechas y calcula montos
    // - Envía la reserva al repositorio

    // --- Propiedades: dependencias y estado ---
    private lateinit var auth: FirebaseAuth                      // FirebaseAuth para obtener usuario actual
    private var _binding: FragmentReservaBinding? = null         // ViewBinding (nulo cuando la vista no existe)
    private val binding get() = _binding!!                      // Acceso seguro al binding (NO null)
    private val repository = ReservaRepository()                // Repositorio para llamadas a API
    private var idCliente: Int = 0                              // ID del cliente obtenido desde backend
    private lateinit var productosAdapter: ProductosReservaAdapter // Declaración del adaptador

    private var listaProductosDisponibles = listOf<ProductoResponse>() // Guarda la lista completa de productos de la API
    private var listaProductosSeleccionados = mutableListOf<ProductoReservaRequest>() // Lista de productos seleccionados




    // --- Ciclo de vida del fragmento ---
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inicializar FirebaseAuth y ViewBinding
        auth = FirebaseAuth.getInstance()
        _binding = FragmentReservaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configuración inicial de la vista y eventos
        configurarVista()
        configurarEventos()
        obtenerProductos()
        // Inicializar recycler view de productos seleccionados
        setupProductosReservaRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Evitar fugas de memoria limpiando el binding
        _binding = null
    }




    // --- Setup de UI y eventos ---
    private fun configurarVista() {
        // Inicializar datos visibles y obtener idCliente
        usuarioFireBase()
        cargarDatosHabitaciones()
        inicializarIdCliente()
    }

    private fun configurarEventos() {
        // Registrar listeners de UI (botones e inputs)
        binding.btnRealizarReserva.setOnClickListener {
            crearReserva()
        }
        binding.editTextIngreso.setOnClickListener {
            mostrarCalendario(binding.editTextIngreso)
        }
        binding.editTextSalida.setOnClickListener {
            mostrarCalendario(binding.editTextSalida)
        }
        binding.btnCancelar.setOnClickListener {
            dismiss()
        }

        // Selección de productos para la reserva
        binding.btnProductoAgregar.setOnClickListener {
            agregarProductoSeleccionado()
        }
    }

    // --- Carga de datos en la vista ---
    private fun cargarDatosHabitaciones() {
        // Carga valores pasados por argumentos al fragmento y asigna a la UI.
        val nombreHabitacion = arguments?.getString("nombreHabitacion") ?: "Sin nombre"
        val descripcionHabitacion = arguments?.getString("descripcionHabitacion") ?: "Sin descripción"
        val precioHabitacion = arguments?.getString("precioHabitacion") ?: "S/. 0"
        val imagenHabitacion = arguments?.getString("imagenHabitacion")
        Log.d("DEBUG", "URL Imagen: $imagenHabitacion")

        // Cargar imagen con Glide y asignar textos
        Glide.with(this)
            .load(imagenHabitacion)
            .into(binding.imagenHabitacion)

        binding.tipoHabitacion.text = nombreHabitacion
        binding.descripcionHabitacion.text = descripcionHabitacion
        binding.costoHabitacion.text = precioHabitacion
    }

    // --- Usuario Firebase ---
    private fun usuarioFireBase() {
        // Obtiene datos del usuario autenticado desde Firestore y los coloca en la UI
        val usuarioAuth = FirebaseAuth.getInstance().currentUser
        if (usuarioAuth != null) {
            val uid = usuarioAuth.uid

            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val nombre = document.getString("nombre")
                        val apellido = document.getString("apellido")
                        val correo = document.getString("email")
                        val telefono = document.getString("telefono")

                        val nombreCompleto = "$nombre $apellido"

                        binding.txtNombreCompleto.setText(nombreCompleto)
                        binding.txtCorreo.setText(correo ?: "Sin correo")
                        binding.txtTelefono.setText(telefono ?: "Sin teléfono")

                        Log.i("FIRESTORE", "Nombre: $nombreCompleto")
                        Log.i("FIRESTORE", "Correo: $correo")
                        Log.i("FIRESTORE", "Teléfono: $telefono")
                    } else {
                        Log.w("FIRESTORE", "No se encontró el documento del usuario")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FIRESTORE", "Error al obtener usuario: ${e.localizedMessage}")
                }

        } else {
            Log.w("FIREBASE", "No hay usuario autenticado")
        }
    }

    // --- Helpers de fecha / UI ---
    private fun mostrarCalendario(campo: TextInputEditText) {
        // Muestra DatePickerDialog y formatea la fecha seleccionada en yyyy-MM-dd
        val calendario = Calendar.getInstance()
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, y, m, d ->
                val fecha = String.format("%04d-%02d-%02d", y, m + 1, d)
                campo.setText(fecha)
                calcularPago() // recalcular montos cuando cambie una fecha
            },
            year, month, day
        )
        datePicker.show()
    }

    // --- Cálculos de fechas y montos ---
    // Calcula días entre fechas (incluye ambos días)
    fun diasEntreFechas(): Int {
        val fechaInicioStr = binding.editTextIngreso.text.toString()
        val fechaFinStr = binding.editTextSalida.text.toString()

        if (fechaInicioStr.isEmpty() || fechaFinStr.isEmpty()) return 0

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaInicio = formatter.parse(fechaInicioStr)
        val fechaFin = formatter.parse(fechaFinStr)

        // Asegurarse de no usar `.time` sobre nullables
        if (fechaInicio == null || fechaFin == null) return 0

        val diff = fechaFin.time - fechaInicio.time
        val dias = (diff / (1000 * 60 * 60 * 24)).toInt() + 1  // +1 para incluir ambos días
        return if (dias > 0) dias else 0
    }

    // Calcula y actualiza los textos de monto, impuestos y total en la UI
    fun calcularPago() {
        val dias = diasEntreFechas()
        if (dias == 0) {
            binding.txtMonto.text = "Monto: S/ 0.00"
            binding.txtImpuesto.text = "Impuestos y tasas: S/ 0.00"
            binding.txtTotalPago.text = "Total: S/ 0.00"
            return
        }

        val precioString = arguments?.getString("precioHabitacion") ?: "0"
        val precioLimpio = precioString.replace(Regex("[^\\d.]"), "")
        val precioHabitacionDouble = precioLimpio.toDoubleOrNull() ?: 0.0

        val precioProductos = listaProductosSeleccionados.sumOf { (it.precioUnitarioGrabado ?: 0.0) * it.cantidad }

        val pago = precioHabitacionDouble * dias + precioProductos

        val impuestos = pago * 0.10
        val totalPago = pago + impuestos

        binding.txtCantidadDias.text = "${getString(R.string.numero_de_noches)} $dias"
        binding.txtMonto.text = "Monto: S/ %.2f".format(pago)
        binding.txtImpuesto.text = "Impuestos y tasas: S/ %.2f".format(impuestos)
        binding.txtTotalPago.text = "Total: S/ %.2f".format(totalPago)
    }

    // --- Llamadas a red / repositorio ---
    private fun obtenerProductos() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = repository.obtenerProductos()
                if (response.isSuccessful) {
                    listaProductosDisponibles = response.body() ?: emptyList()

                    val productosDisplay = listaProductosDisponibles.map { producto ->
                        "${producto.nombreProducto} - S/ ${"%.2f".format(producto.precio)}"
                    }

                    val arrayAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        productosDisplay
                    )

                    binding.comboProducto.setAdapter(arrayAdapter)

                } else {
                    Log.e("PROD_ERROR", "Error al obtener productos: ${response.code()}")
                    Toast.makeText(context, "No se pudieron cargar los productos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("PROD_EXCEPTION", "Excepción al obtener productos: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun obtenerIdClienteSuspend(): Int {
        // Obtiene el ID del cliente a partir del correo del usuario actual (suspend)
        val usuarioActual = auth.currentUser?.email.toString()
        val response = repository.obtenerClientePorCorreo(usuarioActual)
        return if (response.isSuccessful) response.body() ?: 0 else 0
    }

    private fun inicializarIdCliente() {
        // Lanza coroutine para inicializar idCliente de forma asíncrona y registra resultado en logs
        viewLifecycleOwner.lifecycleScope.launch {
            idCliente = obtenerIdClienteSuspend()
            Log.i("CLI", "ID Cliente inicializado: $idCliente")
        }
    }

    // --- Acción principal: crear reserva ---
    private fun crearReserva() {
        // Construye objeto ReservaRequest y lo envía via repositorio; muestra mensajes según resultado
        val idHabitacion = arguments?.getInt("idHabitacion") ?: 0
        val fechaIngreso = binding.editTextIngreso.text.toString()
        val fechaSalida = binding.editTextSalida.text.toString()

        val productosParaBackend = listaProductosSeleccionados.map { producto ->
            ProductoReservaRequest(
                productoId = producto.productoId,
                cantidad = producto.cantidad
            )
        }

        val reserva = ReservaRequest(
            clienteId = idCliente,
            fechaCheckIn = fechaIngreso,
            fechaCheckOut = fechaSalida,
            descuento = "0",
            estadoReservaId = 1,
            habitacionIds = listOf(idHabitacion),
            productos = productosParaBackend
        )

        val gson = GsonBuilder().setPrettyPrinting().create()
        Log.i("JSON_ENVIADO", gson.toJson(reserva))

        viewLifecycleOwner.lifecycleScope.launch {
            val response = repository.crearReserva(reserva)
            if (response.isSuccessful) {
                // Limpiar campos y notificar al usuario
                binding.editTextIngreso.setText("")
                binding.editTextSalida.setText("")
                binding.txtHuespedes.setText("")

                Toast.makeText(
                    requireContext(),
                    "Reserva agregada correctamente",
                    Toast.LENGTH_SHORT
                ).show()

                Log.i("RESERVA", "Reserva creada con ID: ${response.body()?.id}")
                dismiss()
            } else {
                val errorBody = response.errorBody()?.string()
                Toast.makeText(
                    requireContext(),
                    "La habitación se encuentra ocupada",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("ERROR", "Falló la reserva: $errorBody")
            }
        }
    }

    // --- RecyclerView de productos seleccionados ---

    private fun setupProductosReservaRecyclerView() {
        productosAdapter = ProductosReservaAdapter(
            listaProductosSeleccionados,
            onDisminuirClick = { producto ->
                val index = listaProductosSeleccionados.indexOf(producto)
                if (index != -1) {
                    if (listaProductosSeleccionados[index].cantidad > 1) {
                        listaProductosSeleccionados[index].cantidad--
                        productosAdapter.notifyItemChanged(index)
                    } else {
                        listaProductosSeleccionados.removeAt(index)
                        productosAdapter.notifyItemRemoved(index)
                    }
                    calcularPago()
                }
            },
            onAumentarClick = { producto ->
                val index = listaProductosSeleccionados.indexOf(producto)
                if (index != -1) {
                    listaProductosSeleccionados[index].cantidad++
                    productosAdapter.notifyItemChanged(index)
                    calcularPago()
                }
            },
            onEliminarClick = { producto ->
                val index = listaProductosSeleccionados.indexOf(producto)
                if (index != -1) {
                    listaProductosSeleccionados.removeAt(index)
                    productosAdapter.notifyItemRemoved(index)
                    calcularPago()
                }
            }
        )
        binding.rvProductosReserva.adapter = productosAdapter
        binding.rvProductosReserva.layoutManager = LinearLayoutManager(context)
    }

    private fun agregarProductoSeleccionado() {
        val textoSeleccionado = binding.comboProducto.text.toString()

        if (textoSeleccionado.isBlank()) {
            Toast.makeText(context, "Por favor, seleccione un producto", Toast.LENGTH_SHORT).show()
            return
        }

        val productoEncontrado = listaProductosDisponibles.find { producto ->
            "${producto.nombreProducto} - S/ ${"%.2f".format(producto.precio)}" == textoSeleccionado
        }

        if (productoEncontrado != null) {
            val nuevoProductoParaReserva = ProductoReservaRequest(
                productoId = productoEncontrado.id,
                cantidad = 1, // Siempre empezamos agregando 1
                nombreProducto = productoEncontrado.nombreProducto,
                precioUnitarioGrabado = productoEncontrado.precio
            )

            listaProductosSeleccionados.add(nuevoProductoParaReserva)

            productosAdapter.notifyItemInserted(listaProductosSeleccionados.size - 1)

            binding.comboProducto.text.clear()

            calcularPago()

            Toast.makeText(context, "${productoEncontrado.nombreProducto} agregado.", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(context, "Producto no válido. Por favor, seleccione uno de la lista.", Toast.LENGTH_LONG).show()
        }
    }

}


