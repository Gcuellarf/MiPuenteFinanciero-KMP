package co.edu.mipuente.kmp.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Teal = Color(0xFF006B70)
val Income = Color(0xFF147D4C)
val Expense = Color(0xFFB73D43)
private val light = lightColorScheme(primary=Teal, onPrimary=Color.White, primaryContainer=Color(0xFFC1F0EE), onPrimaryContainer=Color(0xFF003739), secondary=Color(0xFF365F83), secondaryContainer=Color(0xFFDCEAF9), onSecondaryContainer=Color(0xFF163850), tertiary=Color(0xFF8C4963), background=Color(0xFFF4F8F8), surface=Color.White, surfaceVariant=Color(0xFFE5EEEE), onSurface=Color(0xFF172D32))
private val dark = darkColorScheme(primary=Color(0xFF81D9D6), primaryContainer=Color(0xFF004F52), onPrimaryContainer=Color(0xFFC1F0EE), secondaryContainer=Color(0xFF254961), background=Color(0xFF112326), surface=Color(0xFF192E31))
@Composable fun FinanceTheme(darkMode:Boolean, content:@Composable ()->Unit) = MaterialTheme(colorScheme=if(darkMode) dark else light, content=content)
