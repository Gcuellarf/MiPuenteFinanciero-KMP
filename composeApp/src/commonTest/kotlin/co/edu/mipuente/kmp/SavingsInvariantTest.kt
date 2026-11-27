package co.edu.mipuente.kmp
import co.edu.mipuente.kmp.domain.*
import kotlin.test.*
class SavingsInvariantTest {
    @Test fun reservationPreservesTotal() {
        val g = Goal(1, "Meta", 300000L, 500000L, 100000L, 0L)
        val m = Movement(1, "Abono", "Ahorro", 100000L,
            Kind.EXPENSE, Account.PERSONAL, "prueba", "goal-saving")
        val d = FinanceData(listOf(m), listOf(g), Profile())
        assertEquals(100000L, d.reserved(Account.PERSONAL))
        assertEquals(Account.PERSONAL.opening,
            d.balance(Account.PERSONAL) + d.reserved(Account.PERSONAL))
        assertEquals(200000L, g.previousSavings)
    }
    @Test fun reservationsAreSeparatedByAccount() {
        val g = Goal(1, "Meta", 300000L, 500000L, 100000L, 50000L)
        val d = FinanceData(emptyList(), listOf(g), Profile())
        assertEquals(100000L, d.reserved(Account.PERSONAL))
        assertEquals(50000L, d.reserved(Account.BUSINESS))
        assertEquals(150000L, g.reservedTotal)
        assertEquals(150000L, g.previousSavings)
    }
}
