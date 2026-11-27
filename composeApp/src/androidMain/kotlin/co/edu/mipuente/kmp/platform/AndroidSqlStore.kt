package co.edu.mipuente.kmp.platform

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import co.edu.mipuente.kmp.data.SqlStore

class AndroidSqlStore(context: Context) : SqlStore {
    private val db = context.openOrCreateDatabase("mi_puente_kmp.db", Context.MODE_PRIVATE, null)
    override fun execute(sql: String,args: List<String>) { db.execSQL(sql,args.toTypedArray()) }
    override fun query(sql: String,args: List<String>): List<List<String>> = db.rawQuery(sql,args.toTypedArray()).use { cursor ->
        buildList { while(cursor.moveToNext()) add(List(cursor.columnCount) { cursor.getString(it) ?: "" }) }
    }
    override fun transaction(block: () -> Unit) {
        db.beginTransaction()
        try { block(); db.setTransactionSuccessful() } finally { db.endTransaction() }
    }
    fun close() = db.close()
}
