package co.edu.mipuente.kmp.platform

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.withTimeout

actual val platformName: String = "Web"
actual fun todayLabel(): String = js("new Date().toLocaleString('es-CO')") as String
actual suspend fun requestRateJson(): String = withTimeout(15000) {
    val response = window.fetch("https://open.er-api.com/v6/latest/USD").await()
    require(response.ok) { "HTTP ${response.status}" }
    response.text().await()
}
