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

@Composable internal fun Goals(vm:FinanceViewModel) {
    var create by remember { mutableStateOf(false) }; var edit by remember { mutableStateOf<Goal?>(null) }; var contribution by remember { mutableStateOf<Goal?>(null) }; var delete by remember { mutableStateOf<Goal?>(null) }
    Heading("Metas de ahorro","Convierte tus objetivos en avances visibles")
    Button(onClick={create=true},modifier=Modifier.fillMaxWidth()) { Text("+ Nueva meta") }
    if(vm.data.goals.isEmpty()) Panel { Text("Crea tu primera meta.") }
    vm.data.goals.forEach { g -> Panel { GoalSummary(g); Row(horizontalArrangement=Arrangement.spacedBy(5.dp)) { Button(enabled=g.saved<g.target,onClick={contribution=g}) { Text("Abonar") }; TextButton(onClick={edit=g}) { Text("Editar") }; TextButton(onClick={delete=g}) { Text("Eliminar",color=MaterialTheme.colorScheme.error) } } } }
    if(create) GoalDialog(null,{create=false},{n,t->if(vm.addGoal(n,t))create=false})
    edit?.let { g->GoalDialog(g,{edit=null},{n,t->if(vm.editGoal(g,n,t))edit=null}) }
    contribution?.let { g-> var amount by remember { mutableStateOf("") }; val n=parseAmount(amount); AlertDialog(onDismissRequest={contribution=null},title={Text("Abonar a ${g.name}")},text={Column { Text("Faltan ${cop(g.target-g.saved)}. Este registro no mueve el saldo de tus cuentas.");AmountField(amount,{amount=it}) }},confirmButton={TextButton(enabled=n!=null&&n<=g.target-g.saved,onClick={if(n!=null&&vm.contribute(g,n))contribution=null}){Text("Guardar aporte")}},dismissButton={TextButton(onClick={contribution=null}){Text("Cancelar")}}) }
    delete?.let { g->Confirm("Eliminar meta","Se eliminará el registro de ahorro de ${g.name}.",{delete=null},{vm.deleteGoal(g);delete=null}) }
}
@Composable internal fun GoalDialog(g:Goal?,onClose:()->Unit,onSave:(String,Long)->Unit) {
    var name by remember { mutableStateOf(g?.name?:"") };var target by remember { mutableStateOf(g?.target?.toString()?:"") };val n=parseAmount(target)
    AlertDialog(onDismissRequest=onClose,title={Text(if(g==null)"Nueva meta" else "Editar meta")},text={Column(verticalArrangement=Arrangement.spacedBy(10.dp)){OutlinedTextField(name,{name=it.take(100)},label={Text("Nombre")},singleLine=true);AmountField(target,{target=it},"Objetivo en COP")}},confirmButton={TextButton(enabled=name.isNotBlank()&&n!=null&&n>=(g?.saved?:0L),onClick={if(n!=null)onSave(name,n)}){Text("Guardar")}},dismissButton={TextButton(onClick=onClose){Text("Cancelar")}})
}
