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

@Composable internal fun Movements(vm:FinanceViewModel) {
    var search by remember { mutableStateOf("") }; var filter by remember { mutableStateOf<Kind?>(null) }; var savingsOnly by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Movement?>(null) }; var deleting by remember { mutableStateOf<Movement?>(null) }
    Heading("Movimientos","Busca, filtra y revisa en qué se mueve tu plata")
    Accounts(vm); OutlinedTextField(search,{search=it},label={Text("Buscar movimiento o categoría")},singleLine=true,modifier=Modifier.fillMaxWidth())
    Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)) { listOf(null,Kind.INCOME,Kind.EXPENSE).forEach { k -> FilterChip(!savingsOnly&&filter==k,{filter=k;savingsOnly=false},label={Text(k?.label ?: "Todos")}) }; FilterChip(savingsOnly,{savingsOnly=true;filter=null},label={Text("Ahorro")}) }
    val rows=vm.data.active().filter { (if(savingsOnly) it.isGoalSaving else filter==null||(!it.isGoalSaving&&it.kind==filter)) && (it.title.contains(search,true)||it.category.contains(search,true)) }
    Text("${rows.size} movimientos",style=MaterialTheme.typography.labelLarge)
    if(rows.isEmpty()) Panel { Text("No hay resultados para este filtro.") }
    rows.forEach { m -> Panel { MovementLine(m,vm.data.profile.balanceVisible); Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
        if(m.isGoalSaving) Text("Gestionar desde Metas",style=MaterialTheme.typography.bodySmall) else { if(m.transfer.isEmpty()) TextButton(onClick={editing=m}) { Text("Editar") }; TextButton(onClick={deleting=m}) { Text("Eliminar",color=MaterialTheme.colorScheme.error) } }
    } } }
    editing?.let { m -> MovementDialog(m,onClose={editing=null},onSave={title,cat,amount,kind -> if(vm.edit(m,title,cat,amount,kind)) editing=null}) }
    deleting?.let { m -> Confirm("Eliminar movimiento",if(m.transfer.isEmpty()) "Se recalculará el saldo." else "Se eliminarán ambos registros de la transferencia.",{deleting=null},{vm.delete(m);deleting=null}) }
}
@Composable internal fun MovementDialog(m:Movement,onClose:()->Unit,onSave:(String,String,Long,Kind)->Unit) {
    var title by remember { mutableStateOf(m.title) }; var cat by remember { mutableStateOf(m.category) }; var amount by remember { mutableStateOf(m.amount.toString()) }; var kind by remember { mutableStateOf(m.kind) }
    AlertDialog(onDismissRequest=onClose,title={Text("Editar movimiento")},text={Column(verticalArrangement=Arrangement.spacedBy(10.dp)) { OutlinedTextField(title,{title=it.take(100)},label={Text("Descripción")},singleLine=true); CategoryPicker(cat){cat=it}; AmountField(amount,{amount=it}); Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Kind.entries.forEach { k->FilterChip(kind==k,{kind=k},label={Text(k.label)}) }} }},confirmButton={TextButton(enabled=title.isNotBlank()&&parseAmount(amount)!=null,onClick={parseAmount(amount)?.let { onSave(title,cat,it,kind) }}){Text("Guardar")}},dismissButton={TextButton(onClick=onClose){Text("Cancelar")}})
}
