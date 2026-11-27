package co.edu.mipuente.kmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.mipuente.kmp.data.FinanceRepository
import co.edu.mipuente.kmp.platform.AndroidSqlStore
import co.edu.mipuente.kmp.presentation.FinanceViewModel
import co.edu.mipuente.kmp.navigation.Screen
import co.edu.mipuente.kmp.ui.App

class MainActivity : ComponentActivity() {
    private var store: AndroidSqlStore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val result = runCatching {
            val db = AndroidSqlStore(applicationContext)
            store = db
            FinanceViewModel(FinanceRepository(db))
        }
        setContent {
            result.fold(onSuccess = { vm ->
                BackHandler(enabled = vm.screen != Screen.HOME) { vm.back() }
                App(vm)
            }, onFailure = { e ->
                MaterialTheme { Column(Modifier.fillMaxSize().padding(24.dp)) {
                    Text("No se pudo abrir la base de datos", style=MaterialTheme.typography.headlineSmall)
                    Text(e.message ?: "Error de almacenamiento")
                } }
            })
        }
    }
    override fun onDestroy() { store?.close(); super.onDestroy() }
}
