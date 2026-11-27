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
import org.jetbrains.compose.resources.painterResource
import mipuentefinancierokmp.composeapp.generated.resources.Res
import mipuentefinancierokmp.composeapp.generated.resources.logo

@Composable internal fun Brand() { Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
    Image(painterResource(Res.drawable.logo),contentDescription="Logo de Mi Puente Financiero",modifier=Modifier.size(45.dp))
    Text("Mi Puente\nFinanciero",fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleMedium)
} }
@Composable internal fun Heading(title:String,sub:String) { Column(verticalArrangement=Arrangement.spacedBy(4.dp)) {
    Text(title,style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.onBackground)
    Text(sub,style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
} }
@Composable internal fun Panel(title:String?=null, modifier:Modifier=Modifier, content:@Composable ColumnScope.()->Unit) {
    Card(modifier.fillMaxWidth(),shape=RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) { if(title!=null) Text(title,style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold); content() }
    }
}
@Composable internal fun Accounts(vm:FinanceViewModel) { Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) { Account.entries.forEach { a ->
    FilterChip(selected=vm.data.profile.account==a,onClick={vm.account(a)},label={Text(a.label)},modifier=Modifier.weight(1f))
} } }
@Composable internal fun MovementLine(m:Movement,visible:Boolean=true) { Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
    Surface(shape=RoundedCornerShape(12.dp),color=if(m.kind==Kind.INCOME) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer) { Text(if(m.kind==Kind.INCOME) "↑" else "↓",Modifier.padding(12.dp),fontSize=21.sp) }
    Column(Modifier.weight(1f)) { Text(m.title,fontWeight=FontWeight.SemiBold); Text("${m.category} · ${m.date}",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant) }
    Text(if(!visible) "••••" else (if(m.kind==Kind.INCOME) "+" else "−")+cop(m.amount),fontWeight=FontWeight.Bold,color=if(m.kind==Kind.INCOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,style=MaterialTheme.typography.bodyMedium)
} }
private val categories=listOf("Comida","Transporte","Súper","Vivienda","Trabajo","Ventas","Otros")
@Composable internal fun CategoryPicker(selected:String,onSelect:(String)->Unit) { Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)) { categories.forEach { c -> FilterChip(selected==c,{onSelect(c)},label={Text(c)}) } } }
@Composable internal fun AmountField(value:String,onChange:(String)->Unit,label:String="Monto en COP") { OutlinedTextField(value,{s->if(s.all{it in '0'..'9'} && s.length<=9) onChange(s)},label={Text(label)},singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number),modifier=Modifier.fillMaxWidth()) }
@Composable internal fun GoalSummary(g:Goal) { Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) { Text(g.name,Modifier.weight(1f),fontWeight=FontWeight.SemiBold); Text("${(g.progress*100).toInt()}%",fontWeight=FontWeight.Bold) }; LinearProgressIndicator(progress={g.progress},modifier=Modifier.fillMaxWidth().height(8.dp)); Text("${cop(g.saved)} de ${cop(g.target)}",style=MaterialTheme.typography.bodySmall) }
@Composable internal fun Confirm(title:String,body:String,close:()->Unit,accept:()->Unit) { AlertDialog(onDismissRequest=close,title={Text(title)},text={Text(body)},confirmButton={TextButton(onClick=accept){Text("Eliminar",color=MaterialTheme.colorScheme.error)}},dismissButton={TextButton(onClick=close){Text("Cancelar")}}) }
