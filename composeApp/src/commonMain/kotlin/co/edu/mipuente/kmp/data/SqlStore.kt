package co.edu.mipuente.kmp.data

// Las dos plataformas ejecutan las mismas consultas y transacciones SQLite.
interface SqlStore {
    fun execute(sql: String, args: List<String> = emptyList())
    fun query(sql: String, args: List<String> = emptyList()): List<List<String>>
    fun transaction(block: () -> Unit)
}
