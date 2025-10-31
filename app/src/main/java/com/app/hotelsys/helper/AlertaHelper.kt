package com.app.hotelsys.helper

import android.content.Context
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object AlertaHelper {

    fun mostrarAlerta(titulo: String, mensaje: String, context: Context) {
        MaterialAlertDialogBuilder(context)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun mostrarAlertaToast(mensaje: String, context: Context) {
        Toast.makeText(context, "$mensaje", Toast.LENGTH_SHORT).show()
    }

}