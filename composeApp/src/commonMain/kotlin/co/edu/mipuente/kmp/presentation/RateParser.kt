package co.edu.mipuente.kmp.presentation

import kotlinx.serialization.json.*

internal fun parseCopRate(json: String): Double {
    val obj = Json.parseToJsonElement(json) as? JsonObject
    val rates = obj?.get("rates") as? JsonObject
    val value = (rates?.get("COP") as? JsonPrimitive)?.doubleOrNull
    require(value != null && value.isFinite() && value > 0) {
        "Respuesta sin tasa COP válida"
    }
    return value
}
