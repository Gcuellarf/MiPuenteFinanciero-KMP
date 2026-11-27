package co.edu.mipuente.kmp.navigation

enum class Screen(val label: String, val symbol: String) {
    HOME("Inicio","⌂"), MOVEMENTS("Historial","≡"), ADD("Añadir","+"), GOALS("Metas","◎"), PROFILE("Perfil","●"), ABOUT("Créditos","i"), TRANSFER("Transferir","⇄")
}
class Navigator {
    private val stack = mutableListOf(Screen.HOME)
    val current get() = stack.last()
    val canGoBack get() = stack.size > 1
    fun select(screen: Screen) { stack.clear(); stack.add(Screen.HOME); if (screen != Screen.HOME) stack.add(screen) }
    fun push(screen: Screen) { if (current != screen) stack.add(screen) }
    fun pop() { if (canGoBack) stack.removeAt(stack.lastIndex) }
}
