# Validación

## Ejecutada en el entorno de generación

Prueba `verification/RepositorySmoke.kt`, ejecutada con Kotlin/JVM 1.9.0, JDK 21 y SQLite de Python 3. Se compiló y ejecutó el código Kotlin de `domain`, `navigation` y `data` sin cambios, con un adaptador de prueba que ejecuta cada consulta sobre SQLite real. No es una prueba de los adaptadores finales Android ni Web.

Resultado: PASS: seed, balances, CRUD movements, transfers, rollback, goals, preferences, reopening SQLite, validation, navigation.

Reproducir desde la raíz (Linux/macOS, Kotlin CLI y Python 3 instalados):

```bash
bash verification/run-smoke.sh
```

También se analizaron sintácticamente los 27 archivos Kotlin y Gradle Kotlin DSL mediante el parser de Kotlin 1.9.0: 0 errores de sintaxis. Esto no sustituye la resolución de tipos ni la compilación con Kotlin 2.2.20.

Gradle no se pudo descargar aquí: `java.net.UnknownHostException: services.gradle.org`. Las pruebas completas de Android, Kotlin/JS, UI y red deben hacerse en un equipo con Android SDK y acceso a Internet.

## Pruebas de aceptación (pendientes de ejecutar en ambos targets)

| Caso | Procedimiento | Resultado esperado |
|---|---|---|
| A01 | Abrir Inicio | Saldo personal $2.500.000 y negocio $2.365.000 en una instalación nueva |
| A02 | Añadir gasto $50.000 en Personal | Saldo personal $2.450.000 y nuevo movimiento |
| A03 | Cerrar Android o recargar la página en la misma URL | El gasto permanece |
| A04 | Editar el gasto a $20.000 | Saldo personal $2.480.000 |
| A05 | Eliminar el gasto | Saldo personal vuelve a $2.500.000 |
| A06 | Transferir $100.000 de Personal a Negocio | Saldos $2.400.000 y $2.465.000; total $4.865.000 |
| A07 | Eliminar uno de los dos movimientos de transferencia | Se eliminan ambos y se restauran saldos |
| A08 | Crear meta Reserva $1.000.000 y abonar $200.000 | 20 % de progreso; permanece tras reiniciar |
| A09 | Editar nombre y objetivo de la meta; intentar objetivo menor al aporte | Actualización válida; operación inválida deshabilitada |
| A10 | Cambiar nombre y activar tema oscuro | Preferencias permanecen al reiniciar |
| A11 | Buscar y filtrar ingresos/gastos | Lista correcta; no mezcla entornos |
| A12 | Actualizar USD/COP con Internet y sin Internet | Tasa o mensaje de error; las demás funciones siguen operativas |
| A13 | Abrir las siete pantallas y volver desde Créditos/Transferir | Flujo de navegación coherente en ambos tamaños |
| A14 | Web: recargar tras guardar y revisar DevTools > Application > Local Storage | Clave `mi-puente-financiero-kmp.sqlite.v1` con binario exportado en base64 |

Registrar resultados reales, dispositivo, navegador, fecha y capturas. Las cantidades esperadas presuponen datos de ejemplo y el orden indicado; en instalaciones ya usadas comparar contra el saldo previo.
