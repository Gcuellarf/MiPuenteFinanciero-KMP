# Registro de Pruebas de Persistencia, Integridad y Consistencia SQLite

**Estudiante:** José Luis Sánchez López  
**Rol / Responsabilidad:** Persistencia SQLite, integridad del ahorro y pruebas reproducibles  
**Rama de trabajo:** `feature/kmp-persistencia-jose`  
**Repositorio:** `Gcuellarf/MiPuenteFinanciero-KMP`  
**Fecha:** 27 de noviembre de 2026  
**Entorno de ejecución:**  
- **Sistema Operativo:** Windows 11 Home (x64)
- **JDK / Runtime:** Java 21 LTS (Oracle Corporation 21.0.2+13-LTS-58)
- **Gradle:** 8.14.3
- **Kotlin:** 2.2.20 / Compose Multiplatform (KMP)
- **Motor de persistencia:** SQLite nativo en Android y sql.js en Web, mediante la interfaz compartida `SqlStore` (no se utiliza Room)

---

## 1. Fundamentos Técnicos del Módulo de Persistencia

### ¿Qué es Atomicidad en el contexto de Mi Puente Financiero?
La atomicidad es una propiedad fundamental (ACID) que garantiza que un conjunto de operaciones sobre la base de datos se ejecute como una unidad indivisible: o se aplican todos los cambios con éxito (`COMMIT`), o no se aplica ninguno (`ROLLBACK`).  
En la aplicación, cuando un usuario abona a una meta de ahorro o realiza una transferencia entre cuentas, ocurren múltiples escrituras:
1. Se registra el movimiento financiero en la tabla `movements`.
2. Se actualizan los montos acumulados y las reservas por cuenta directamente en la propia tabla `goals` (`personal_reserved` y `business_reserved`).
3. En transferencias, se registran los dos movimientos enlazados (egreso en la cuenta origen e ingreso en la cuenta destino).

Si alguna de estas operaciones falla en el camino (por ejemplo, por corte de energía, error de validación o excepción inesperada), la transacción se revierte por completo (`ROLLBACK`), evitando que el dinero "desaparezca" o quede inconsistente.

### ¿Qué hace `SqlStore`?
`SqlStore` es la abstracción multiplataforma (KMP) que desacopla la lógica de negocio (`FinanceRepository`) de la implementación concreta de persistencia en cada plataforma: SQLite nativo en Android y `sql.js` en Web. Proporciona:
- `execute(sql: String, args: List<String>)`: Ejecución de sentencias DDL y DML (INSERT, UPDATE, DELETE).
- `query(sql: String, args: List<String>): List<List<String>>`: Consulta estructurada de registros.
- `transaction(block: () -> Unit)`: Control transaccional con manejo de `BEGIN`, `COMMIT` y `ROLLBACK`.

### ¿Cómo se conserva el saldo (Invariante del Ahorro)?
El saldo disponible de una cuenta (`balance`) y el dinero apartado para metas (`reserved`) satisfacen la ecuación fundamental de conservación, considerando ingresos, gastos operativos y transferencias entre cuentas (Personal y Negocio):
$$\text{Saldo Inicial} + \sum \text{Ingresos} - \sum \text{Gastos Operativos} \pm \sum \text{Transferencias} = \text{Saldo Disponible} + \text{Reservas de Metas}$$

Donde las transferencias suman en la cuenta destino y restan en la cuenta origen ($\pm \sum \text{Transferencias}$), garantizando que el patrimonio global consolidado de ambas cuentas se conserve estrictamente:
$$\text{Patrimonio Total} = \text{Saldo Disponible (Personal)} + \text{Saldo Disponible (Negocio)} + \text{Reservas Totales en Metas}$$

Cuando se aparta dinero para una meta, no se destruye el capital; se traslada de "saldo disponible" a "saldo reservado". Al retirar de la meta o eliminarla, el monto reservado regresa automáticamente al saldo disponible de la cuenta correspondiente (Personal o Negocio).

---

## 2. Pruebas Unitarias Automatizadas de Dominio (`SavingsInvariantTest.kt`)

Se implementaron pruebas reproducibles en `composeApp/src/commonTest/kotlin/co/edu/mipuente/kmp/SavingsInvariantTest.kt`:

1. **`reservationPreservesTotal`**:
   - **Objetivo:** Verificar que una reserva de ahorro descuente del saldo disponible pero conserve el balance total patrimonial de la cuenta `Account.PERSONAL`.
   - **Resultado:** **PASS**

2. **`reservationsAreSeparatedByAccount`**:
   - **Objetivo:** Verificar que las reservas asignadas desde cuentas distintas (`Account.PERSONAL` y `Account.BUSINESS`) se segreguen correctamente y que el total reservado (`reservedTotal`) coincida con la suma de las partes.
   - **Resultado:** **PASS**

---

## 3. Verificación de Rollback SQLite (`verification/RepositorySmoke.kt`)

Se amplió la prueba de humo en `verification/RepositorySmoke.kt` inyectando un fallo controlado dentro de un bloque transaccional:
```kotlin
val beforeRollback = r.read()
check(runCatching {
    store.transaction {
        store.execute("INSERT OR REPLACE INTO preferences(key,value) VALUES (?,?)",
            listOf("name", "Temporal"))
        error("Rollback de prueba")
    }
}.isFailure)
check(r.read() == beforeRollback)
```
- **Entorno:** `verification/run-smoke.sh` (requiere entorno Bash, kotlinc y python3 bridge).
- **Estado:** Código implementado y verificado sintácticamente. En entornos Windows puros sin `kotlinc` nativo en PATH, se deja registrado como complementario a las pruebas de Gradle.

---

## 4. Matriz de Casos de Prueba Manuales y de Aceptación (Android / Web)

A continuación se registra la matriz de ejecución basada en los escenarios de prueba definidos en la guía:

| ID | Caso de Prueba | Saldo Inicial | Procedimiento | Resultado Esperado | Resultado Observado | Estado |
|---|---|---|---|---|---|---|
| **CP01** | **Abono desde dos cuentas (Personal y Negocio en COP)** | Personal: $2.500.000<br>Negocio: $2.365.000 | Crear meta "Ahorro Vacaciones" ($500.000). Abonar $100.000 desde Personal y $50.000 desde Negocio. | Personal disminuye a $2.400.000. Negocio disminuye a $2.315.000. Reserva total = $150.000 (30% meta). | Los saldos disminuyeron exactamente en la cuantía de sus respectivos abonos. La meta refleja $150.000 ahorrados. | **PASS** |
| **CP02** | **Retiro parcial a cuenta origen** | Personal: $2.400.000<br>Reserva: $150.000 | Retirar $25.000 desde Personal en la meta "Ahorro Vacaciones". | Personal recupera $25.000 (queda en $2.425.000). Reserva total queda en $125.000. | Saldo personal aumentó a $2.425.000. La reserva se redujo a $125.000 sin afectar el saldo de Negocio. | **PASS** |
| **CP03** | **Validación y rechazo de operación inválida** | Personal: $2.425.000<br>Negocio: $2.315.000 | Intentar retirar un monto mayor al reservado en la cuenta (ej. retirar $300.000) o monto negativo/cero. | La aplicación impide la transacción o emite mensaje de error. Ningún saldo o reserva cambia. | La validación del repositorio rechazó la operación; la base de datos no sufrió modificaciones. | **PASS** |
| **CP04** | **Persistencia tras reinicio / recarga** | Personal: $2.425.000<br>Negocio: $2.315.000 | Cerrar y volver a abrir la app en Android; recargar la página en navegador Web. | Todos los saldos, movimientos y metas persisten idénticos en SQLite / LocalStorage. | Los datos se leen correctamente del almacenamiento local persistente sin discrepancias. | **PASS** |
| **CP05** | **Eliminación de meta y restitución de reservas** | Personal: $2.425.000<br>Negocio: $2.315.000<br>Reserva: $125.000 | Eliminar la meta "Ahorro Vacaciones" (que tiene $75.000 de Personal y $50.000 de Negocio). | Se restituyen $75.000 a Personal (vuelve a $2.500.000) y $50.000 a Negocio (vuelve a $2.365.000). | Se eliminó la meta y las reservas asociadas. Ambos saldos regresaron a sus valores íntegros iniciales. | **PASS** |

---

## 5. Declaración de Asistencia de IA
Conforme a las pautas de transparencia académica e individual del microproyecto:
- Se utilizó asistencia de inteligencia artificial (Antigravity Agent) para el formateo y estructuración de la documentación técnica, verificación de sintaxis.
- La conceptualización de las invariantes de dominio, validación del modelo de datos y revisión de aserciones fueron supervisadas y ejecutadas por Jose Luis Sanchez Lopez responsable de la persistencia.
