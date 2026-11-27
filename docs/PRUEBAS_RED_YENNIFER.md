# Pruebas de Red y Recuperación — Consulta USD/COP

* **Responsable:** Yennifer Yuliana Uni Itaz
* **Rama:** `feature/kmp-viewmodel-red-yennifer`
* **Fecha:** 2026-11-27
* **Endpoint consultado:** `https://open.er-api.com/v6/latest/USD`

## Resultados en Web (`jsBrowserDevelopmentRun` / DevTools Offline)

| Escenario | Resultado esperado | Resultado observado | Estado |
| :--- | :--- | :--- | :---: |
| Actualizar tasa con conexión | Se presenta una tasa positiva y mensaje de consulta exitosa. | Muestra tasa COP positiva válida y el mensaje *"Consulta en línea exitosa · referencia, no TRM oficial"*. | PASS |
| Intentar consultar sin conexión | Mensaje de error o timeout. La app continúa operando localmente. | Al activar *Offline* en DevTools, muestra *"No se pudo consultar. Revisa Internet y vuelve a intentar."* y la app sigue operando localmente. | PASS |
| Restablecer Internet y actualizar | Una nueva consulta puede completarse correctamente. | Al volver a *No throttling* y consultar, actualiza el valor de la tasa y restablece el estado exitoso. | PASS |
| Revisar la última tasa después de un error | Si se conserva el valor anterior, el estado debe informar el fallo de la consulta nueva. | Conserva el último valor numérico en pantalla mientras `rateStatus` informa el error de red de la nueva consulta. | PASS |

## Resultados en Android (Emulador / `assembleDebug`)

| Escenario | Resultado esperado | Resultado observado | Estado |
| :--- | :--- | :--- | :---: |
| Actualizar tasa con conexión | Se presenta una tasa positiva y mensaje de consulta exitosa. | Pendiente de ejecución en emulador (APK `assembleDebug` y `testDebugUnitTest` compilados y aprobados). | pendiente |
| Intentar consultar sin conexión | Mensaje de error o timeout. La app continúa operando localmente. | Pendiente de ejecución en emulador. | pendiente |
| Restablecer Internet y actualizar | Una nueva consulta puede completarse correctamente. | Pendiente de ejecución en emulador. | pendiente |
| Revisar la última tasa después de un error | Si se conserva el valor anterior, el estado debe informar el fallo de la consulta nueva. | Pendiente de ejecución en emulador. | pendiente |
