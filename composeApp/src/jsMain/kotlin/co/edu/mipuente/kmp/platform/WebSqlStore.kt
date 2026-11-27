package co.edu.mipuente.kmp.platform

import co.edu.mipuente.kmp.data.SqlStore
import kotlinx.browser.window
import kotlin.js.Promise

// La distribución asm.js evita depender de la localización de un archivo .wasm externo.
@JsModule("sql.js/dist/sql-asm.js")
@JsNonModule
external fun initSqlJs(): Promise<dynamic>

class WebSqlStore(private val sqlModule: dynamic) : SqlStore {
    private val key = "mi-puente-financiero-kmp.sqlite.v1"
    private fun database(bytes: dynamic = null): dynamic = js("Reflect").construct(sqlModule.Database, if (bytes == null) emptyArray<Any>() else arrayOf(bytes))
    private fun decode(text: String): dynamic {
        val binary = window.atob(text)
        val bytes = js("Reflect").construct(js("Uint8Array"), arrayOf(binary.length))
        for(i in binary.indices) bytes[i] = binary[i].code
        return bytes
    }
    private fun encode(bytes: dynamic): String {
        val length = bytes.length as Int
        val binary = buildString(length) { for(i in 0 until length) append((bytes[i] as Int).toChar()) }
        return window.btoa(binary)
    }
    private var db: dynamic = window.localStorage.getItem(key)?.let { database(decode(it)) } ?: database()
    override fun execute(sql: String,args: List<String>) { db.run(sql,args.toTypedArray()) }
    override fun query(sql: String,args: List<String>): List<List<String>> {
        val stmt = db.prepare(sql)
        try {
            stmt.bind(args.toTypedArray())
            val rows = mutableListOf<List<String>>()
            while(stmt.step() as Boolean) {
                val values = stmt.get()
                rows.add(List(values.length as Int) { i -> if(values[i] == null) "" else values[i].toString() as String })
            }
            return rows
        } finally { stmt.free() }
    }
    override fun transaction(block: () -> Unit) {
        // sql.js guarda en RAM: exportar el binario SQLite después de cada transacción.
        val before = db.export()
        try {
            db.run("BEGIN TRANSACTION")
            block()
            db.run("COMMIT")
            window.localStorage.setItem(key,encode(db.export()))
        } catch(e: Throwable) {
            // También restaura RAM si el navegador deniega almacenamiento o supera su cuota.
            db.close(); db = database(before)
            throw e
        }
    }
}
