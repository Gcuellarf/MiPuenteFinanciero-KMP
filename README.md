# Mi Puente Financiero — Kotlin Multiplatform

Microproyecto 4 | Electiva Desarrollo de Aplicaciones Móviles | Universidad del Cauca

Una sola implementación de la interfaz y lógica financiera en Kotlin para **Android y Web**. Proyecto independiente de los microproyectos anteriores de Android nativo y SwiftUI.

## Inicio rápido en Windows

1. Extraer el ZIP completamente en una carpeta normal, por ejemplo `C:\Proyectos\MiPuenteFinanciero_KMP`. Evitar `System32`, OneDrive y carpetas protegidas.
2. Abrir Android Studio, seleccionar **Open** y elegir la carpeta que contiene `settings.gradle.kts`. No abrir solo `composeApp` ni crear otro proyecto.
3. En Settings > Build, Execution, Deployment > Build Tools > Gradle, seleccionar el JDK 17 o 21 (Embedded JDK/JBR sirve si corresponde). Instalar Android SDK Platform 36 en SDK Manager y, si se desea, el plugin Kotlin Multiplatform. El plugin IDE facilita el trabajo, pero las tareas Gradle no dependen de él.
4. Sincronizar Gradle. La primera sincronización descarga Gradle 8.14.3, dependencias Maven y, al compilar Web, Node/Yarn y paquetes npm. Requiere Internet. El wrapper JAR está incluido; no hace falta instalar Gradle manualmente.
5. Abrir la terminal del proyecto para ejecutar los siguientes comandos. En Linux/macOS usar `./gradlew` en lugar de `./gradlew.bat` o ` .\gradlew.bat` de Windows.

### Android

```powershell
.\gradlew.bat :composeApp:assembleDebug
```

APK esperado: `composeApp/build/outputs/apk/debug/composeApp-debug.apk`. También se puede seleccionar `composeApp` en las configuraciones de Android Studio, escoger un emulador API 35/36 o dispositivo API 24+, y pulsar Run. Si no aparece una configuración, Run > Edit Configurations > Android App, módulo `composeApp`.

### Web

```powershell
.\gradlew.bat :composeApp:jsBrowserDevelopmentRun
```

Abrir la URL que muestra Gradle (normalmente http://localhost:8080). Usar Chrome o Edge actualizado, almacenamiento local habilitado y WebGL disponible. Mantener la terminal abierta para que el servidor siga funcionando. `Ctrl+C` lo detiene.

Para generar la distribución estática:

```powershell
.\gradlew.bat :composeApp:jsBrowserDistribution
```

Salida esperada: `composeApp/build/dist/js/productionExecutable/`. Servir **toda** la carpeta por HTTP, no abrir `index.html` directamente con `file://`. Por ejemplo, si Python está instalado:

```powershell
cd composeApp\build\dist\js\productionExecutable
python -m http.server 8080
```

La persistencia del navegador depende del origen (protocolo, host y puerto), por lo que debe mantenerse la misma URL al verificarla. La app Android y la web tienen bases independientes; no hay sincronización entre dispositivos.

## Funcionalidades

- Inicio con saldo de Personal y Negocio, entradas/salidas, meta principal, historial reciente y consulta USD/COP.
- Movimientos con filtros, búsqueda, edición y eliminación.
- Añadir con teclado numérico, categorías y registro de ingresos/gastos.
- Metas con creación, cambio de nombre y objetivo, abonos y eliminación. Los abonos son seguimiento del ahorro, no cargos sobre el saldo.
- Perfil con edición de nombre, tema oscuro y visibilidad del saldo, persistidos.
- Acerca de y créditos de los tres integrantes.
- Puente de dinero: traspasos **contables**, no bancarios, entre los entornos. Se guardan dos movimientos en una transacción y se eliminan juntos.
- Diseño adaptativo: navegación inferior en pantallas estrechas y menú lateral en pantallas anchas.

Datos iniciales demostrativos: saldo personal de $2.500.000 COP y saldo negocio de $2.365.000 COP, con cuatro movimientos de ejemplo ya incluidos en el cálculo y tres metas. Se inicializan una sola vez. Las bases nuevas empiezan con estos datos; no se migran bases de Android/iOS anteriores.

## Arquitectura

```text
composeApp/src/
  commonMain/
    kotlin/co/edu/mipuente/kmp/
      domain/       Modelos y cálculo de saldos
      data/         Contrato SqlStore y repositorio con SQL compartido
      navigation/   Rutas y pila común (select, push, pop)
      presentation/ FinanceViewModel con estado observable de Compose
      platform/     Funciones expect para fecha, nombre de plataforma y HTTP
      ui/           Tema Material 3, componentes y siete vistas
    composeResources/ Logo compartido
  androidMain/      Activity, SQLite nativo, HTTP y funciones actual
  jsMain/           ComposeViewport, sql.js, navegador, HTTP y funciones actual
  commonTest/       Pruebas de lógica y navegación
```

La navegación está implementada en `commonMain` con una pila pequeña de rutas, sin Voyager. Es una decisión de diseño para reducir dependencias y evitar problemas de compatibilidad de librerías con Web. El taller exige navegación compartida, no una biblioteca particular. Si el docente impone Voyager de manera adicional, será necesario adaptar esta capa.

La clase `FinanceViewModel` es el modelo de presentación compartido, no una subclase de AndroidX ViewModel. Su estado utiliza `mutableStateOf`, las vistas se recomponen y el repositorio ejecuta consultas SQL parametrizadas. Los cambios se guardan antes de publicar el estado. Android usa `SQLiteDatabase`; Web usa sql.js (distribución asm.js del paquete npm, SQLite compilado a JavaScript) y exporta el binario SQLite a `localStorage` tras cada transacción. No requiere Firebase, servidor ni credenciales. Está orientado a una demostración académica de pocos datos, no a almacenamiento de información financiera sensible. En Web, borrar datos del sitio, usar incógnito o abrir otro origen puede causar pérdida de datos; no usar varias pestañas simultáneamente. No hay cifrado adicional.

La consulta HTTP usa `https://open.er-api.com/v6/latest/USD` (ExchangeRate-API, sin clave) con lectura de `rates.COP`, manejo de errores y timeout. La tasa es informativa, no una TRM oficial. Se presenta redondeada al peso en la interfaz. La tasa no bloquea el uso offline y no se persiste. Su respuesta real depende del servicio y la conexión; no se probó extremo a extremo.

## Versiones fijadas

| Herramienta | Versión |
|---|---|
| Gradle Wrapper | 8.14.3 |
| Android Gradle Plugin | 8.11.1 |
| Kotlin | 2.2.20 |
| Compose Multiplatform | 1.9.1 |
| Android compileSdk / targetSdk / minSdk | 36 / 35 / 24 |
| JDK | 17 o 21 |
| sql.js (npm) | 1.13.0 |

Se tomó como referencia el ZIP de clase `KotlinProject4.zip`, conservando el wrapper y el enfoque de un módulo `composeApp`. Se ajustaron AGP y targets para Android y Kotlin/JS. No se incluyeron cachés, SDK ni archivos específicos de otro equipo.

## Validación y entrega

- `docs/PRUEBAS.md`: procedimiento y resultado real de las pruebas disponibles.
- `docs/ENTREGA.md`: rúbrica, guía de evidencia, arquitectura para dibujar a mano y ramas Git.
- `verification/`: prueba reproducible del repositorio común contra SQLite real mediante un adaptador JVM/Python exclusivo de verificación.

**Estado:** las pruebas de dominio y SQL común pasaron con Kotlin/JVM y SQLite. La compilación completa Android y JS está pendiente: el entorno de generación no dispone de Android SDK y no puede resolver `services.gradle.org`. No se incluyen APK, distribución web precompilada ni capturas simuladas como evidencia.

## Referencias

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform/get-started.html)
- [Ejemplo del profesor](https://github.com/sismovil/HolaMultiplataforma)
- [Voyager](https://voyager.adriel.cafe/)
- [Persistencia con Room KMP](https://developer.android.com/kotlin/multiplatform/room?hl=es-419)
- [Guía oficial Ktor y SQLDelight](https://kotlinlang.org/docs/multiplatform/multiplatform-ktor-sqldelight.html)
- [sql.js y persistencia](https://github.com/sql-js/sql.js/blob/master/examples/persistent.html)
- [Compose Multiplatform: ejecución](https://kotlinlang.org/docs/multiplatform/build-and-run-kmp.html)
- Material del curso: Taller 4 - Microproyecto KMP, KMP 1 Introducción, KMP 2 Navegación, KMP 3 Persistencia y guía Cinefy con SQLDelight. Flutter se revisó como contexto de alternativas, pero el proyecto usa KMP.
