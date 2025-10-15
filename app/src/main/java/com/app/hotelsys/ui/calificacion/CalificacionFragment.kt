package com.app.hotelsys.ui.calificacion

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.hotelsys.databinding.FragmentCalificacionBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class CalificacionFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCalificacionBinding? = null
    private val binding get() = _binding!!

    private var habitacionId: Int = 0
    private var habitacionNumero: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            habitacionId = it.getInt(ARG_HABITACION_ID)
            habitacionNumero = it.getString(ARG_HABITACION_NUMERO) ?: ""
        }
    }

    // Esta función asegura que el BottomSheet se expanda completamente y el teclado funcione bien
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true // Evita que se pueda colapsar a la mitad
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

        binding.textViewTituloCalificacion.text = "Calificar Habitación $habitacionNumero"

        // Poner el foco en el RatingBar al iniciar para una mejor UX
        binding.ratingBar.requestFocus()

        binding.buttonEnviarCalificacion.setOnClickListener {
            enviarCalificacion()
        }
    }

    private fun enviarCalificacion() {
        // Ocultar el teclado para evitar problemas visuales al mostrar errores
        val inputMethodManager = requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)

        val calificacion = binding.ratingBar.rating
        val comentario = binding.editTextComentario.text.toString().trim()

        if (calificacion == 0f) {
            Snackbar.make(binding.root, "Por favor, selecciona al menos media estrella", Snackbar.LENGTH_SHORT).show()
            return
        }

        // TODO: Lógica para guardar en Firebase

        Toast.makeText(context, "Gracias por tu reseña!", Toast.LENGTH_LONG).show()
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_HABITACION_ID = "habitacion_id"
        private const val ARG_HABITACION_NUMERO = "habitacion_numero"

        fun newInstance(habitacionId: Int, habitacionNumero: String) =
            CalificacionFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_HABITACION_ID, habitacionId)
                    putString(ARG_HABITACION_NUMERO, habitacionNumero)
                }
            }
    }
}
