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

private val tabs = listOf(Screen.HOME,Screen.MOVEMENTS,Screen.ADD,Screen.GOALS,Screen.PROFILE)
@Composable fun App(vm: FinanceViewModel) {
    FinanceTheme(vm.data.profile.dark) {
        val snack = remember { SnackbarHostState() }
        LaunchedEffect(vm.message) { vm.message?.let { snack.showSnackbar(it); vm.clearMessage() } }
        BoxWithConstraints(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            val wide = maxWidth >= 780.dp
            Scaffold(containerColor=MaterialTheme.colorScheme.background, snackbarHost={SnackbarHost(snack)},
                bottomBar={ if(!wide) NavigationBar { tabs.forEach { tab ->
                    NavigationBarItem(selected=vm.screen==tab, onClick={vm.select(tab)}, icon={Text(tab.symbol,fontSize=22.sp)}, label={Text(tab.label,fontSize=10.sp)})
                } } }) { padding ->
                Row(Modifier.fillMaxSize().padding(padding)) {
                    if(wide) Surface(color=MaterialTheme.colorScheme.surface, modifier=Modifier.width(220.dp).fillMaxHeight()) {
                        Column(Modifier.padding(20.dp), verticalArrangement=Arrangement.spacedBy(10.dp)) {
                            Brand(); Spacer(Modifier.height(18.dp))
                            tabs.forEach { tab ->
                                if(vm.screen==tab) Button(onClick={vm.select(tab)},modifier=Modifier.fillMaxWidth()) { Text("${tab.symbol}  ${tab.label}") }
                                else TextButton(onClick={vm.select(tab)},modifier=Modifier.fillMaxWidth()) { Text("${tab.symbol}  ${tab.label}") }
                            }
                            HorizontalDivider(); TextButton(onClick={vm.open(Screen.TRANSFER)}) { Text("⇄  Transferir") }; TextButton(onClick={vm.open(Screen.ABOUT)}) { Text("i  Acerca de") }
                            Spacer(Modifier.weight(1f)); Text("KMP · $platformName",style=MaterialTheme.typography.labelMedium)
                        }
                    }
                    Box(Modifier.weight(1f).fillMaxHeight(),contentAlignment=Alignment.TopCenter) {
                        key(vm.screen) { Column(Modifier.widthIn(max=780.dp).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
                            when(vm.screen) {
                                Screen.HOME -> Home(vm)
                                Screen.MOVEMENTS -> Movements(vm)
                                Screen.ADD -> Add(vm)
                                Screen.GOALS -> Goals(vm)
                                Screen.PROFILE -> ProfileScreen(vm)
                                Screen.ABOUT -> About(vm)
                                Screen.TRANSFER -> Transfer(vm)
                            }
                            Spacer(Modifier.height(12.dp))
                        } }
                    }
                }
            }
        }
    }
}
