package com.app.hotelsys.ui.calificacion

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.hotelsys.adapters.ResenaCalificarAdapter
import com.app.hotelsys.databinding.FragmentCalificacionBinding
import com.app.hotelsys.models.calificacion.ResenaCalificar
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import java.util.Date
import com.google.firebase.auth.FirebaseAuth

class CalificacionFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCalificacionBinding? = null
    private val binding get() = _binding!!

    private var habitacionId: Int = 0
    private var habitacionNumero: String = ""
    private var habitacionTipo: String = ""
    private var habitacionImagenUrl: String = ""

    private lateinit var auth: FirebaseAuth

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
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                val behavior = BottomSheetBehavior.from(bottomSheet)
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

        auth = FirebaseAuth.getInstance() // Inicializa FirebaseAuth

        setupHeader()
        setupForm()
        setupResenasList()
    }

    private fun setupHeader() {
        binding.textViewNombreHabitacionResena.text = "$habitacionTipo N° $habitacionNumero"
        Glide.with(this).load(habitacionImagenUrl).centerCrop().into(binding.imageViewHabitacionResena)

        binding.ratingBarPromedio.rating = 4.8f
        binding.textViewPromedio.text = "4.8"
        binding.textViewTotalResenas.text = "(124 reseñas)"
    }

    private fun setupForm() {
        // Rellenamos el nombre del usuario y lo deshabilitamos
        val nombreUsuarioActual = auth.currentUser?.displayName ?: "Usuario Anónimo"
        binding.editTextNombreUsuario.setText(nombreUsuarioActual)
        binding.inputLayoutNombreUsuario.isEnabled = false

        binding.buttonEnviarCalificacion.setOnClickListener { enviarCalificacion() }
        binding.buttonCancelar.setOnClickListener { dismiss() }
    }

    private fun setupResenasList() {
        val dummyResenas = listOf(
            ResenaCalificar("María González", 5f, "Excelente habitación, muy limpia y cómoda.", Date()),
            ResenaCalificar("Carlos Ruiz", 3.5f, "Muy buena ubicación y amenidades.", Date(System.currentTimeMillis() - 86400000 * 7))
        )
        binding.recyclerViewResenas.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewResenas.adapter = ResenaCalificarAdapter(dummyResenas)
    }

    private fun enviarCalificacion() {
        val inputMethodManager = requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)

        val calificacion = binding.ratingBarCalificacion.rating
        val nombre = auth.currentUser?.displayName ?: "Usuario Anónimo"
        val comentario = binding.editTextComentario.text.toString().trim()

        if (calificacion == 0f) {
            Snackbar.make(binding.root, "Por favor, selecciona al menos media estrella", Snackbar.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(context, "Gracias por tu reseña, $nombre!", Toast.LENGTH_LONG).show()
        dismiss()
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

        fun newInstance(habitacionId: Int, habitacionNumero: String, habitacionTipo: String, habitacionImagenUrl: String) =
            CalificacionFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_HABITACION_ID, habitacionId)
                    putString(ARG_HABITACION_NUMERO, habitacionNumero)
                    putString(ARG_HABITACION_TIPO, habitacionTipo)
                    putString(ARG_HABITACION_IMAGEN_URL, habitacionImagenUrl)
                }
            }
    }
}