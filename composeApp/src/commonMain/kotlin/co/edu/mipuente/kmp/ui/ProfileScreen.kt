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

@Composable internal fun ProfileScreen(vm:FinanceViewModel) {
    val p=vm.data.profile;var name by remember { mutableStateOf(p.name) }
    Heading("Perfil","Tu cuenta y las preferencias de Mi Puente Financiero")
    Panel("Cuenta principal") { OutlinedTextField(name,{name=it.take(60)},label={Text("Nombre de usuario")},singleLine=true,modifier=Modifier.fillMaxWidth());Button(enabled=name.isNotBlank(),onClick={vm.profile(p.copy(name=name))}){Text("Guardar nombre")} }
    Panel("Entorno activo") { Accounts(vm) }
    Panel("Preferencias") { Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) { Text("Mostrar saldo",Modifier.weight(1f));Switch(p.balanceVisible,{vm.profile(p.copy(balanceVisible=it))}) };HorizontalDivider();Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Text("Tema oscuro",Modifier.weight(1f));Switch(p.dark,{vm.profile(p.copy(dark=it))})} }
    Panel("Privacidad") { Text("Los datos permanecen en este dispositivo o navegador. No hay acceso bancario, sincronización ni cifrado adicional.");TextButton(onClick={vm.open(Screen.ABOUT)}){Text("Acerca de y créditos →")} }
}
