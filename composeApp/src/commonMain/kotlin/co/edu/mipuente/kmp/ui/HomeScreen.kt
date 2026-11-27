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

@Composable internal fun Home(vm:FinanceViewModel) {
    val d=vm.data; val a=d.profile.account; val scope=rememberCoroutineScope()
    Heading("Hola, ${d.profile.name}","Así se ve tu plata hoy · $platformName")
    Accounts(vm)
    Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.padding(22.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
                Text("Saldo total · ${a.label}",style=MaterialTheme.typography.titleMedium); TextButton(onClick={vm.profile(d.profile.copy(balanceVisible=!d.profile.balanceVisible))}) { Text(if(d.profile.balanceVisible) "Ocultar" else "Mostrar") }
            }
            Text(if(d.profile.balanceVisible) cop(d.balance(a)) else "••••••••",style=MaterialTheme.typography.headlineLarge,fontWeight=FontWeight.Bold)
            Text("Información almacenada localmente",style=MaterialTheme.typography.bodySmall)
        }
    }
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
        Panel("Entradas",Modifier.weight(1f)) { Text(if(d.profile.balanceVisible) cop(d.active().filter{it.kind==Kind.INCOME}.sumOf{it.amount}) else "••••••",color=MaterialTheme.colorScheme.primary,fontWeight=FontWeight.Bold) }
        Panel("Salidas",Modifier.weight(1f)) { Text(if(d.profile.balanceVisible) cop(d.active().filter{it.kind==Kind.EXPENSE}.sumOf{it.amount}) else "••••••",color=MaterialTheme.colorScheme.error,fontWeight=FontWeight.Bold) }
    }
    d.goals.firstOrNull()?.let { g -> Panel("Meta de ahorro") { GoalSummary(g); TextButton(onClick={vm.select(Screen.GOALS)}) { Text("Ver todas las metas →") } } }
    Panel("Acciones rápidas") { Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        Button(onClick={vm.select(Screen.ADD)},modifier=Modifier.weight(1f)) { Text("+ Registrar") }; OutlinedButton(onClick={vm.open(Screen.TRANSFER)},modifier=Modifier.weight(1f)) { Text("⇄ Transferir") }
    }; TextButton(onClick={vm.select(Screen.MOVEMENTS)}) { Text("Ver historial →") } }
    Panel("Tasa USD/COP") {
        Text(vm.rate?.let { cop(it.toLong()) + " / USD" } ?: "Sin consultar",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
        Text(vm.rateStatus,style=MaterialTheme.typography.bodySmall)
        if(vm.loadingRate) LinearProgressIndicator(Modifier.fillMaxWidth())
        OutlinedButton(enabled=!vm.loadingRate,onClick={scope.launch { vm.refreshRate() }}) { Text("Actualizar tasa") }
    }
    Panel("Movimientos recientes") { if(d.active().isEmpty()) Text("Todavía no hay movimientos."); d.active().take(3).forEach { MovementLine(it,d.profile.balanceVisible); HorizontalDivider() } }
}
