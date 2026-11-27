package co.edu.mipuente.kmp.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.mipuente.kmp.domain.*
import co.edu.mipuente.kmp.navigation.Screen
import co.edu.mipuente.kmp.platform.platformName
import co.edu.mipuente.kmp.presentation.FinanceViewModel
import kotlinx.coroutines.launch

@Composable internal fun About(vm:FinanceViewModel) {
    TextButton(onClick={vm.back()}){Text("← Volver")};Brand();Heading("Acerca de","Microproyecto KMP · Universidad del Cauca")
    Panel("Mi Puente Financiero") { Text("Control de ingresos, gastos y metas personales y de negocio desde una aplicación compartida para Android y Web.");Text("Versión 1.0 · Kotlin Multiplatform / Compose Material 3") }
    Panel("Arquitectura") { Text("UI y navegación compartidas · FinanceViewModel · repositorio con SQL común · SQLite nativo y sql.js · consulta HTTPS USD/COP.") }
    Panel("Créditos") { Text("Guillermo Adolfo Cuéllar Fonseca");Text("José Luis Sánchez López");Text("Yennifer Yuliana Uni Itaz");HorizontalDivider();Text("Electiva Desarrollo de Aplicaciones Móviles\nProfesor Cristhian Figueroa · 2026") }
    Panel("Alcance") { Text("Prototipo académico. Los valores iniciales son demostrativos; no ejecuta transferencias bancarias ni ofrece asesoría financiera.") }
}
