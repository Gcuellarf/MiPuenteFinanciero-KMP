package co.edu.mipuente.kmp.domain

enum class Account(val label: String, val opening: Long) {
    PERSONAL("Personal", 1_730_900), BUSINESS("Negocio", 2_115_000)
}
enum class Kind(val label: String) { INCOME("Ingreso"), EXPENSE("Gasto") }
data class Movement(val id: Long, val title: String, val category: String, val amount: Long,
    val kind: Kind, val account: Account, val date: String, val transfer: String = "") {
    val signed get() = if (kind == Kind.INCOME) amount else -amount
}
data class Goal(val id: Long, val name: String, val saved: Long, val target: Long) {
    val progress get() = if (target > 0) (saved.toDouble() / target).coerceIn(0.0, 1.0).toFloat() else 0f
}
data class Profile(val name: String = "Alejandra", val account: Account = Account.PERSONAL,
    val balanceVisible: Boolean = true, val dark: Boolean = false)
data class FinanceData(val movements: List<Movement>, val goals: List<Goal>, val profile: Profile) {
    fun balance(account: Account) = account.opening + movements.filter { it.account == account }.sumOf { it.signed }
    fun active() = movements.filter { it.account == profile.account }
}
const val MAX_AMOUNT = 999_999_999L
fun parseAmount(text: String): Long? = text.trim().toLongOrNull()?.takeIf { it in 1..MAX_AMOUNT }
fun cop(value: Long): String {
    val s = value.toString().removePrefix("-").reversed().chunked(3).joinToString(".").reversed()
    return (if (value < 0) "−" else "") + "$ " + s + " COP"
}
