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
    var create by remember { mutableStateOf(false) }
    var edit by remember { mutableStateOf<Goal?>(null) }
    var contribution by remember { mutableStateOf<Goal?>(null) }
    var withdrawal by remember { mutableStateOf<Goal?>(null) }
    var delete by remember { mutableStateOf<Goal?>(null) }
    val active=vm.data.profile.account
    Heading("Metas de ahorro","Separa parte de tu plata para alcanzar tus objetivos")
    Panel("Tu dinero disponible") {
        Accounts(vm)
        Text(if(vm.data.profile.balanceVisible) cop(vm.data.balance(active)) else "••••••",style=MaterialTheme.typography.headlineSmall)
        Text("Reservado desde ${active.label}: ${if(vm.data.profile.balanceVisible) cop(vm.data.reserved(active)) else "••••••"}")
    }
    Button(onClick={create=true},modifier=Modifier.fillMaxWidth()) { Text("+ Nueva meta") }
    if(vm.data.goals.isEmpty()) Panel { Text("Crea tu primera meta.") }
    vm.data.goals.forEach { g -> Panel {
        GoalSummary(g)
        if(g.reservedTotal>0) Text("Reservado: ${cop(g.reservedTotal)}",style=MaterialTheme.typography.bodySmall)
        if(g.previousSavings>0) Text("Ahorro previo registrado: ${cop(g.previousSavings)} (sin cargo al saldo)",style=MaterialTheme.typography.bodySmall)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            Button(enabled=g.saved<g.target,onClick={contribution=g},modifier=Modifier.weight(1f)) { Text("Abonar") }
            OutlinedButton(enabled=g.reservedTotal>0,onClick={withdrawal=g},modifier=Modifier.weight(1f)) { Text("Retirar") }
        }
        Row(horizontalArrangement=Arrangement.spacedBy(5.dp)) {
            TextButton(onClick={edit=g}) { Text("Editar") }
            TextButton(onClick={delete=g}) { Text("Eliminar",color=MaterialTheme.colorScheme.error) }
        }
    } }
    if(create) GoalDialog(null,{create=false},{n,t->if(vm.addGoal(n,t))create=false})
    edit?.let { g->GoalDialog(g,{edit=null},{n,t->if(vm.editGoal(g,n,t))edit=null}) }
    contribution?.let { g-> SavingDialog(vm,g,false,{contribution=null}) { a,n -> if(vm.contribute(g,n,a)) contribution=null } }
    withdrawal?.let { g-> SavingDialog(vm,g,true,{withdrawal=null}) { a,n -> if(vm.withdraw(g,n,a)) withdrawal=null } }
    delete?.let { g->Confirm("Eliminar meta","Se devolverá ${cop(g.reservedTotal)} a las cuentas de origen. El ahorro previo sin cargo al saldo solo se eliminará del registro.",{delete=null},{if(vm.deleteGoal(g))delete=null}) }
}

@Composable private fun SavingDialog(vm:FinanceViewModel,goal:Goal,withdrawing:Boolean,onClose:()->Unit,onSave:(Account,Long)->Unit) {
    var account by remember(goal.id,withdrawing) { mutableStateOf(vm.data.profile.account) }
    var amount by remember(goal.id,withdrawing) { mutableStateOf("") }
    val g=vm.data.goals.firstOrNull { it.id==goal.id } ?: goal
    val balance=vm.data.balance(account)
    val limit=if(withdrawing) g.reserved(account) else minOf(balance,g.target-g.saved).coerceAtLeast(0L)
    val n=parseAmount(amount)
    val valid=n!=null && n<=limit
    AlertDialog(onDismissRequest=onClose,
        title={Text(if(withdrawing) "Retirar de ${g.name}" else "Abonar a ${g.name}")},
        text={Column(verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Text(if(withdrawing) "Devolver a la cuenta de origen" else "¿De qué cuenta sale tu ahorro?")
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) { Account.entries.forEach { a ->
                FilterChip(selected=account==a,onClick={account=a},label={Text(a.label)},modifier=Modifier.weight(1f))
            } }
            Text("Disponible: ${if(vm.data.profile.balanceVisible) cop(balance) else "••••••"}")
            Text(if(withdrawing) "Reservado desde esta cuenta: ${cop(limit)}" else "Faltan ${cop(g.target-g.saved)} para completar la meta.")
            AmountField(amount,{amount=it})
            if(limit==0L) Text(if(withdrawing) "No hay dinero reservado desde esta cuenta." else "No hay saldo disponible para abonar.",color=MaterialTheme.colorScheme.error)
            else if(amount.isNotEmpty()&&!valid) Text("Máximo permitido: ${cop(limit)}",color=MaterialTheme.colorScheme.error)
            if(valid && n!=null && vm.data.profile.balanceVisible) {
                Text("Nuevo saldo: ${cop(if(withdrawing) balance+n else balance-n)}",fontWeight=FontWeight.SemiBold)
                Text("Nuevo ahorro: ${cop(if(withdrawing) g.saved-n else g.saved+n)}")
            }
        }},
        confirmButton={TextButton(enabled=valid,onClick={if(n!=null&&valid)onSave(account,n)}){Text(if(withdrawing) "Devolver dinero" else "Confirmar abono")}},
        dismissButton={TextButton(onClick=onClose){Text("Cancelar")}})
}

@Composable internal fun GoalDialog(g:Goal?,onClose:()->Unit,onSave:(String,Long)->Unit) {
    var name by remember { mutableStateOf(g?.name?:"") };var target by remember { mutableStateOf(g?.target?.toString()?:"") };val n=parseAmount(target)
    AlertDialog(onDismissRequest=onClose,title={Text(if(g==null)"Nueva meta" else "Editar meta")},text={Column(verticalArrangement=Arrangement.spacedBy(10.dp)){OutlinedTextField(name,{name=it.take(100)},label={Text("Nombre")},singleLine=true);AmountField(target,{target=it},"Objetivo en COP")}},confirmButton={TextButton(enabled=name.isNotBlank()&&n!=null&&n>=(g?.saved?:0L),onClick={if(n!=null)onSave(name,n)}){Text("Guardar")}},dismissButton={TextButton(onClick=onClose){Text("Cancelar")}})
}
