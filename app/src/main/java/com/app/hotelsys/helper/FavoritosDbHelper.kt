package com.app.hotelsys.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.app.hotelsys.models.Habitacion

/**
 * Contract y helper para la tabla de "favoritos".
 *
 * - `FavoritosContract` define constantes (nombre BD, versión, nombres de tabla/columnas).
 * - `FavoritosDbHelper` extiende `SQLiteOpenHelper` y encapsula la creación/actualización
 *   de la base de datos y operaciones CRUD básicas sobre la tabla de favoritos.
 *
 * Uso típico:
 * val dbHelper = FavoritosDbHelper(context)
 * dbHelper.agregarFavorito(habitacion)
 * val lista = dbHelper.obtenerFavoritos()
 *
 * Notas:
 * - Estas rutinas abren y cierran la DB en cada operación para simplicidad. En escenarios de
 *   alta carga puede convenir mantener una instancia abierta o usar un patrón singleton.
 * - No se realiza manejo avanzado de errores (ej. transacciones) en este helper; añadir según sea necesario.
 */
object FavoritosContract {
    // Nombre del archivo de la base de datos y versión para onUpgrade
    const val DATABASE_NAME = "favoritos.db"
    const val DATABASE_VERSION = 1

    // Definición de la tabla y columnas para evitar literales dispersos por el código
    object FavoritoEntry {
        const val TABLE_NAME = "favoritos"
        const val COLUMN_ID_HABITACION = "idHabitacion"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_DESCRIPCION = "descripcion"
        const val COLUMN_PRECIO = "precio"
        const val COLUMN_IMAGEN_URL = "imagenUrl"
        const val COLUMN_FONDO_RES_ID = "fondoResId"
        const val COLUMN_CALIFICACIONES = "calificaciones"
    }
}

/**
 * Helper que gestiona la base de datos de favoritos.
 *
 * Implementa:
 * - onCreate: crea la tabla si no existe
 * - onUpgrade: elimina y recrea la tabla (estrategia simple de ejemplo)
 * - Métodos CRUD: agregarFavorito, eliminarFavorito, obtenerFavoritos, esFavorito
 */
class FavoritosDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    FavoritosContract.DATABASE_NAME,
    null,
    FavoritosContract.DATABASE_VERSION
) {

    /**
     * Crea la tabla de favoritos.
     *
     * Las columnas:
     * - idHabitacion: PRIMARY KEY (se asume id desde backend/servicio)
     * - nombre: texto obligatorio
     * - descripcion, precio, imagenUrl: texto opcional
     * - fondoResId: entero (id de recurso drawable si aplica)
     */
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE ${FavoritosContract.FavoritoEntry.TABLE_NAME} (
                ${FavoritosContract.FavoritoEntry.COLUMN_ID_HABITACION} INTEGER PRIMARY KEY,
                ${FavoritosContract.FavoritoEntry.COLUMN_NOMBRE} TEXT NOT NULL,
                ${FavoritosContract.FavoritoEntry.COLUMN_DESCRIPCION} TEXT,
                ${FavoritosContract.FavoritoEntry.COLUMN_PRECIO} TEXT,
                ${FavoritosContract.FavoritoEntry.COLUMN_IMAGEN_URL} TEXT,
                ${FavoritosContract.FavoritoEntry.COLUMN_FONDO_RES_ID} INTEGER,
                ${FavoritosContract.FavoritoEntry.COLUMN_CALIFICACIONES} TEXT
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    /**
     * Estrategia de actualización simple: borra y recrea la tabla.
     * En producción conviene usar migraciones que conserven datos.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${FavoritosContract.FavoritoEntry.TABLE_NAME}")
        onCreate(db)
    }

    // ----------------------- Operaciones CRUD -----------------------

    /**
     * Inserta una habitación en la tabla de favoritos.
     *
     * Nota: se utiliza `writableDatabase` y `ContentValues`.
     * La conexión se cierra al final para evitar fugas.
     */
    fun agregarFavorito(habitacion: Habitacion) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(FavoritosContract.FavoritoEntry.COLUMN_ID_HABITACION, habitacion.idHabitacion)
            put(FavoritosContract.FavoritoEntry.COLUMN_NOMBRE, habitacion.nombre)
            put(FavoritosContract.FavoritoEntry.COLUMN_DESCRIPCION, habitacion.descripcion)
            put(FavoritosContract.FavoritoEntry.COLUMN_PRECIO, habitacion.precio)
            put(FavoritosContract.FavoritoEntry.COLUMN_IMAGEN_URL, habitacion.imagenUrl)
            put(FavoritosContract.FavoritoEntry.COLUMN_FONDO_RES_ID, habitacion.fondoResId)
            put(FavoritosContract.FavoritoEntry.COLUMN_CALIFICACIONES, habitacion.calificacion)
        }
        db.insert(FavoritosContract.FavoritoEntry.TABLE_NAME, null, values)
        db.close()
    }

    /**
     * Elimina un favorito por idHabitacion.
     * Se usa `writableDatabase` porque modifica la tabla.
     */
    fun eliminarFavorito(idHabitacion: Int) {
        val db = this.writableDatabase
        val selection = "${FavoritosContract.FavoritoEntry.COLUMN_ID_HABITACION} = ?"
        val selectionArgs = arrayOf(idHabitacion.toString())
        db.delete(FavoritosContract.FavoritoEntry.TABLE_NAME, selection, selectionArgs)
        db.close()
    }

    /**
     * Devuelve la lista completa de favoritos.
     *
     * - Abre la DB en modo lectura, ejecuta una consulta y mapea cada fila a la clase Habitacion.
     * - Importante: se cierra el cursor y la conexión al final.
     */
    fun obtenerFavoritos(): List<Habitacion> {
        val favoritos = mutableListOf<Habitacion>()
        val db = this.readableDatabase
        val cursor = db.query(FavoritosContract.FavoritoEntry.TABLE_NAME, null, null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                val habitacion = Habitacion(
                    idHabitacion = getInt(getColumnIndexOrThrow(FavoritosContract.FavoritoEntry.COLUMN_ID_HABITACION)),
                    nombre = getString(getColumnIndexOrThrow(FavoritosContract.FavoritoEntry.COLUMN_NOMBRE)),
                    descripcion = getString(getColumnIndexOrThrow(FavoritosContract.FavoritoEntry.COLUMN_DESCRIPCION)),
                    precio = getString(getColumnIndexOrThrow(FavoritosContract.FavoritoEntry.COLUMN_PRECIO)),
                    imagenUrl = getString(getColumnIndexOrThrow(FavoritosContract.FavoritoEntry.COLUMN_IMAGEN_URL)),
                    fondoResId = getInt(getColumnIndexOrThrow(FavoritosContract.FavoritoEntry.COLUMN_FONDO_RES_ID)),
                    calificacion = getString(getColumnIndexOrThrow(FavoritosContract.FavoritoEntry.COLUMN_CALIFICACIONES))
                )
                favoritos.add(habitacion)
            }
        }
        cursor.close()
        db.close()
        return favoritos
    }

    /**
     * Comprueba si una habitación ya está marcada como favorita.
     *
     * Devuelve true si la consulta devuelve al menos una fila.
     */
    fun esFavorito(idHabitacion: Int): Boolean {
        val db = this.readableDatabase
        val query = "SELECT 1 FROM ${FavoritosContract.FavoritoEntry.TABLE_NAME} WHERE ${FavoritosContract.FavoritoEntry.COLUMN_ID_HABITACION} = ?"
        val cursor = db.rawQuery(query, arrayOf(idHabitacion.toString()))
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }

}
