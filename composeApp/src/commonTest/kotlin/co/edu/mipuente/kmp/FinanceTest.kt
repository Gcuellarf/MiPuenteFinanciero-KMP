package co.edu.mipuente.kmp

import kotlin.test.*
import co.edu.mipuente.kmp.domain.*
import co.edu.mipuente.kmp.navigation.*

class FinanceTest {
    @Test fun amounts() { assertNull(parseAmount("0")); assertNull(parseAmount("-10")); assertNull(parseAmount("1.5")); assertEquals(50000L,parseAmount("50000")); assertEquals("$ 2.500.000 COP",cop(2500000L)) }
    @Test fun savings() { assertEquals(0.65f,Goal(1,"Viaje",1625000,2500000).progress); assertEquals(1f,Goal(1,"Viaje",2500000,2500000).progress) }
    @Test fun accounts() {
        val d=FinanceData(listOf(Movement(1,"Comida","Comida",50000,Kind.EXPENSE,Account.PERSONAL,"hoy"),Movement(2,"Venta","Ventas",100000,Kind.INCOME,Account.BUSINESS,"hoy")),emptyList(),Profile())
        assertEquals(1680900L,d.balance(Account.PERSONAL)); assertEquals(2215000L,d.balance(Account.BUSINESS)); assertEquals(1,d.active().size)
    }
    @Test fun navigation() { val n=Navigator(); n.select(Screen.PROFILE); n.push(Screen.ABOUT); n.pop(); assertEquals(Screen.PROFILE,n.current); n.pop(); assertEquals(Screen.HOME,n.current); n.pop(); assertEquals(Screen.HOME,n.current) }
}
