package co.edu.mipuente.kmp

import kotlin.test.*
import co.edu.mipuente.kmp.presentation.parseCopRate

class RateParserTest {
    @Test fun validRate() {
        assertEquals(3577.5, parseCopRate("""{"rates":{"COP":3577.5}}"""))
    }
    @Test fun missingRate() {
        assertFails { parseCopRate("""{"rates":{}}""") }
        assertFails { parseCopRate("""{}""") }
    }
    @Test fun invalidRate() {
        assertFails { parseCopRate("""{"rates":{"COP":0}}""") }
        assertFails { parseCopRate("""{"rates":{"COP":-1}}""") }
        assertFails { parseCopRate("""{"rates":{"COP":null}}""") }
        assertFails { parseCopRate("""{"rates":[]}""") }
        assertFails { parseCopRate("respuesta inválida") }
    }
}