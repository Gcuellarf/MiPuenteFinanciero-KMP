package co.edu.mipuente.kmp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import co.edu.mipuente.kmp.data.FinanceRepository
import co.edu.mipuente.kmp.platform.*
import co.edu.mipuente.kmp.presentation.FinanceViewModel
import co.edu.mipuente.kmp.ui.App
import kotlinx.coroutines.await
import kotlinx.coroutines.CancellationException
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        var vm by remember { mutableStateOf<FinanceViewModel?>(null) }
        var error by remember { mutableStateOf<String?>(null) }
        LaunchedEffect(Unit) {
            try { vm = FinanceViewModel(FinanceRepository(WebSqlStore(initSqlJs().await()))) }
            catch(e: CancellationException) { throw e }
            catch(e: Throwable) { error = e.message ?: "No se pudo abrir SQLite. Revisa el almacenamiento del navegador." }
        }
        val model = vm
        if(model != null) App(model) else MaterialTheme {
            Box(Modifier.fillMaxSize().padding(24.dp),contentAlignment=Alignment.Center) {
                Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    Text("Mi Puente Financiero",style=MaterialTheme.typography.headlineMedium)
                    if(error==null) { CircularProgressIndicator(); Text("Abriendo base de datos SQLite…") }
                    else { Text("No se pudo iniciar la aplicación"); Text(error!!); Text("Recarga la página después de comprobar que el almacenamiento local está habilitado.") }
                }
            }
        }
    }
}
