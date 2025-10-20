package com.app.hotelsys.ui.calificacion

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.hotelsys.adapters.calificacion.ResenaCalificarAdapter
import com.app.hotelsys.databinding.FragmentCalificacionBinding
import com.app.hotelsys.models.calificacion.CalificacionCalificar
import com.app.hotelsys.repository.CalificacionRepository
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class CalificacionFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCalificacionBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val calificacionRepository = CalificacionRepository()

    private var habitacionId: Int = 0
    private var habitacionNumero: String = ""
    private var habitacionTipo: String = ""
    private var habitacionImagenUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            habitacionId = it.getInt(ARG_HABITACION_ID)
            habitacionNumero = it.getString(ARG_HABITACION_NUMERO) ?: ""
            habitacionTipo = it.getString(ARG_HABITACION_TIPO) ?: ""
            habitacionImagenUrl = it.getString(ARG_HABITACION_IMAGEN_URL) ?: ""
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalificacionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setupForm()
        cargarDatosReales()
    }

    private fun cargarDatosReales() {
        // Header básico
        binding.textViewNombreHabitacionResena.text = "$habitacionTipo N° $habitacionNumero"
        Glide.with(this)
            .load(habitacionImagenUrl)
            .centerCrop()
            .into(binding.imageViewHabitacionResena)

        // Cargar calificaciones reales desde Firebase/Repo
        calificacionRepository.obtenerCalificacionesPorHabitacion(
            habitacionId = habitacionId,
            onSuccess = { calificaciones ->
                actualizarHeader(calificaciones)
                actualizarListaResenas(calificaciones)
            },
            onFailure = {
                Toast.makeText(context, "Error al cargar reseñas", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun actualizarHeader(calificaciones: List<CalificacionCalificar>) {
        if (calificaciones.isEmpty()) {
            binding.ratingBarPromedio.rating = 0f
            binding.textViewPromedio.text = "N/A"
            binding.textViewTotalResenas.text = "(0 reseñas)"
        } else {
            val promedio = calificaciones.map { it.calificacion }.average()
            binding.ratingBarPromedio.rating = promedio.toFloat()
            binding.textViewPromedio.text = String.format("%.1f", promedio)
            binding.textViewTotalResenas.text = "(${calificaciones.size} reseñas)"
        }
    }

    private fun actualizarListaResenas(calificaciones: List<CalificacionCalificar>) {
        binding.recyclerViewResenas.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewResenas.adapter = ResenaCalificarAdapter(calificaciones)
    }

    private fun setupForm() {
        val nombreUsuarioActual = auth.currentUser?.displayName ?: "Usuario Anónimo"
        binding.editTextNombreUsuario.setText(nombreUsuarioActual)
        binding.inputLayoutNombreUsuario.isEnabled = false

        binding.buttonEnviarCalificacion.setOnClickListener { enviarCalificacion() }
        binding.buttonCancelar.setOnClickListener { dismiss() }
    }

    private fun enviarCalificacion() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Snackbar.make(binding.root, "Error: No se ha podido identificar al usuario.", Snackbar.LENGTH_SHORT).show()
            return
        }

        val calificacionValor = binding.ratingBarCalificacion.rating
        if (calificacionValor == 0f) {
            Snackbar.make(binding.root, "Por favor, selecciona al menos media estrella", Snackbar.LENGTH_SHORT).show()
            return
        }

        binding.buttonEnviarCalificacion.isEnabled = false

        val nuevaCalificacion = CalificacionCalificar(
            habitacionId = this.habitacionId,
            usuarioId = currentUser.uid,
            calificacion = calificacionValor,
            comentario = binding.editTextComentario.text.toString().trim(),
            nombreUsuario = currentUser.displayName ?: "Anónimo"
        )

        calificacionRepository.guardarCalificacion(
            calificacion = nuevaCalificacion,
            onSuccess = {
                Toast.makeText(context, "¡Gracias por tu reseña!", Toast.LENGTH_LONG).show()
                dismiss()
            },
            onFailure = { exception ->
                Snackbar.make(binding.root, "Error al guardar: ${exception.message}", Snackbar.LENGTH_LONG).show()
                binding.buttonEnviarCalificacion.isEnabled = true
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_HABITACION_ID = "habitacion_id"
        private const val ARG_HABITACION_NUMERO = "habitacion_numero"
        private const val ARG_HABITACION_TIPO = "habitacion_tipo"
        private const val ARG_HABITACION_IMAGEN_URL = "habitacion_imagen_url"

        fun newInstance(
            habitacionId: Int,
            habitacionNumero: String,
            habitacionTipo: String,
            habitacionImagenUrl: String
        ) = CalificacionFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_HABITACION_ID, habitacionId)
                putString(ARG_HABITACION_NUMERO, habitacionNumero)
                putString(ARG_HABITACION_TIPO, habitacionTipo)
                putString(ARG_HABITACION_IMAGEN_URL, habitacionImagenUrl)
            }
        }
    }
}
