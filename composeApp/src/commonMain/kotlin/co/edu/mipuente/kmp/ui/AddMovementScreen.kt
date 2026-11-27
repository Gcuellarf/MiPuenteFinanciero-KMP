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

@Composable internal fun Add(vm:FinanceViewModel) {
    var amount by remember { mutableStateOf("") }; var title by remember { mutableStateOf("") }; var category by remember { mutableStateOf("Comida") }
    Heading("Registrar movimiento","Anota lo que entró o salió sin formularios largos")
    Accounts(vm)
    Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.primaryContainer),shape=RoundedCornerShape(22.dp)) { Column(Modifier.padding(20.dp).fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally) { Text("Monto");Text(cop(amount.toLongOrNull()?:0),style=MaterialTheme.typography.headlineLarge,fontWeight=FontWeight.Bold) } }
    Text("Categoría",fontWeight=FontWeight.Medium);CategoryPicker(category){category=it}
    OutlinedTextField(title,{title=it.take(100)},label={Text("Descripción opcional")},singleLine=true,modifier=Modifier.fillMaxWidth())
    listOf(listOf("1","2","3"),listOf("4","5","6"),listOf("7","8","9"),listOf("C","0","⌫")).forEach { row -> Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) { row.forEach { k -> FilledTonalButton(onClick={amount=when(k){"C"->"";"⌫"->amount.dropLast(1);else->if(amount.length<9)(amount+k).trimStart('0') else amount}},modifier=Modifier.weight(1f).height(52.dp),shape=RoundedCornerShape(14.dp)) { Text(k,fontSize=21.sp) } } } }
    val n=parseAmount(amount)
    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        Button(enabled=n!=null,onClick={if(n!=null&&vm.add(title.ifBlank{category},category,n,Kind.INCOME)){amount="";title="";vm.select(Screen.MOVEMENTS)}},modifier=Modifier.weight(1f)) { Text("↑ Entró plata") }
        Button(enabled=n!=null,onClick={if(n!=null&&vm.add(title.ifBlank{category},category,n,Kind.EXPENSE)){amount="";title="";vm.select(Screen.MOVEMENTS)}},modifier=Modifier.weight(1f),colors=ButtonDefaults.buttonColors(containerColor=MaterialTheme.colorScheme.error)) { Text("↓ Salió plata") }
    }
}
