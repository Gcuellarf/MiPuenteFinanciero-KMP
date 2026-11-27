package co.edu.mipuente.kmp.presentation

import androidx.compose.runtime.*
import co.edu.mipuente.kmp.domain.*
import co.edu.mipuente.kmp.data.FinanceRepository
import co.edu.mipuente.kmp.navigation.*
import co.edu.mipuente.kmp.platform.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException

class FinanceViewModel(private val repository: FinanceRepository) {
    var data by mutableStateOf(repository.read()); private set
    private val navigator = Navigator()
    var screen by mutableStateOf(navigator.current); private set
    var message by mutableStateOf<String?>(null); private set
    var rate by mutableStateOf<Double?>(null); private set
    var loadingRate by mutableStateOf(false); private set
    var rateStatus by mutableStateOf("Consulta voluntaria de referencia"); private set
    fun select(s: Screen) { navigator.select(s); screen = navigator.current }
    fun open(s: Screen) { navigator.push(s); screen = navigator.current }
    fun back() { navigator.pop(); screen = navigator.current }
    fun clearMessage() { message = null }
    private fun change(success: String, action: () -> Unit): Boolean = try {
        action(); data = repository.read(); message = success; true
    } catch (e: Exception) { message = e.message ?: "No se pudo guardar."; false }
    fun account(a: Account) = change("Entorno: ${a.label}") { repository.saveProfile(data.profile.copy(account=a)) }
    fun profile(p: Profile) = change("Preferencias guardadas") { repository.saveProfile(p) }
    fun add(title: String,cat: String,amount: Long,kind: Kind) = change("Movimiento guardado") { repository.addMovement(title,cat,amount,kind,data.profile.account,todayLabel()) }
    fun edit(m: Movement,title: String,cat: String,amount: Long,kind: Kind) = change("Movimiento actualizado") { repository.editMovement(m,title,cat,amount,kind) }
    fun delete(m: Movement) = change("Movimiento eliminado") { repository.deleteMovement(m) }
    fun transfer(amount: Long) = change("Transferencia registrada") { repository.transfer(data.profile.account,amount,todayLabel()) }
    fun addGoal(name: String,target: Long) = change("Meta creada") { repository.addGoal(name,target) }
    fun editGoal(g: Goal,name: String,target: Long) = change("Meta actualizada") { repository.editGoal(g,name,target) }
    fun contribute(g: Goal,amount: Long,account: Account) = change("Ahorro reservado desde ${account.label}") { repository.contribute(g,amount,account,todayLabel()) }
    fun withdraw(g: Goal,amount: Long,account: Account) = change("Dinero devuelto a ${account.label}") { repository.withdraw(g,amount,account,todayLabel()) }
    fun deleteGoal(g: Goal) = change("Meta eliminada; aportes reservados devueltos") { repository.deleteGoal(g,todayLabel()) }
    suspend fun refreshRate() {
        if (loadingRate) return
        loadingRate = true
        try {
            val value = parseCopRate(requestRateJson())
            rate = value; rateStatus = "Consulta en línea exitosa · referencia, no TRM oficial"
        } catch (e: TimeoutCancellationException) { rateStatus = "Tiempo de espera agotado. Vuelve a intentar." }
        catch (e: CancellationException) { throw e }
        catch (e: Exception) { rateStatus = "No se pudo consultar. Revisa Internet y vuelve a intentar." }
        finally { loadingRate = false }
    }
}