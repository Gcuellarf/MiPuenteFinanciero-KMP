package co.edu.mipuente.kmp.platform

expect fun todayLabel(): String
expect suspend fun requestRateJson(): String
expect val platformName: String
