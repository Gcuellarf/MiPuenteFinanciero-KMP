package co.edu.mipuente.kmp.platform

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual val platformName: String = "Android"
actual fun todayLabel(): String = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es","CO")).format(Date())
actual suspend fun requestRateJson(): String = withContext(Dispatchers.IO) {
    val connection = URL("https://open.er-api.com/v6/latest/USD").openConnection() as HttpURLConnection
    connection.connectTimeout = 12000; connection.readTimeout = 12000
    try {
        require(connection.responseCode in 200..299) { "HTTP ${connection.responseCode}" }
        connection.inputStream.bufferedReader().use { it.readText() }
    } finally { connection.disconnect() }
}
