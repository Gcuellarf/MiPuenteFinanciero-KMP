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

@Composable internal fun Transfer(vm:FinanceViewModel) {
    var amount by remember { mutableStateOf("") };val a=vm.data.profile.account;val n=parseAmount(amount)
    TextButton(onClick={vm.back()}){Text("← Volver")};Heading("Puente de dinero","Registra un traspaso entre tus dos entornos")
    Panel("Origen y destino") { Accounts(vm);Text("${a.label} → ${if(a==Account.PERSONAL)"Negocio" else "Personal"}",style=MaterialTheme.typography.titleLarge);Text("Saldo disponible: ${if(vm.data.profile.balanceVisible)cop(vm.data.balance(a)) else "••••••"}");AmountField(amount,{amount=it});Button(enabled=n!=null&&n<=vm.data.balance(a),onClick={if(n!=null&&vm.transfer(n)){amount="";vm.select(Screen.MOVEMENTS)}},modifier=Modifier.fillMaxWidth()){Text("Registrar transferencia")} }
    Text("Se crean dos movimientos relacionados en una sola transacción SQLite. El saldo combinado no cambia. No se envía dinero real.")
}
