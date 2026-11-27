package co.edu.mipuente.kmp.data

import co.edu.mipuente.kmp.domain.*

class FinanceRepository(private val db: SqlStore) {
    init {
        db.transaction {
            db.execute("CREATE TABLE IF NOT EXISTS movements (id INTEGER PRIMARY KEY, title TEXT NOT NULL, category TEXT NOT NULL, amount INTEGER NOT NULL CHECK(amount > 0), kind TEXT NOT NULL CHECK(kind IN ('INCOME','EXPENSE')), account TEXT NOT NULL CHECK(account IN ('PERSONAL','BUSINESS')), date TEXT NOT NULL, transfer TEXT NOT NULL DEFAULT '')")
            db.execute("CREATE TABLE IF NOT EXISTS goals (id INTEGER PRIMARY KEY, name TEXT NOT NULL, saved INTEGER NOT NULL CHECK(saved >= 0), target INTEGER NOT NULL CHECK(target > 0 AND saved <= target))")
            db.execute("CREATE TABLE IF NOT EXISTS goal_reservations (goal_id INTEGER NOT NULL, account TEXT NOT NULL CHECK(account IN ('PERSONAL','BUSINESS')), amount INTEGER NOT NULL CHECK(amount >= 0), PRIMARY KEY(goal_id,account))")
            db.execute("CREATE TABLE IF NOT EXISTS preferences (key TEXT PRIMARY KEY, value TEXT NOT NULL)")
            if (setting("seeded") == null) {
                insert("Supermercado Líder", "Súper", 75_000, Kind.EXPENSE, Account.PERSONAL, "Ejemplo")
                insert("Transporte SITP", "Transporte", 5_900, Kind.EXPENSE, Account.PERSONAL, "Ejemplo")
                insert("Pago freelance", "Trabajo", 850_000, Kind.INCOME, Account.PERSONAL, "Ejemplo")
                insert("Venta de producto", "Ventas", 250_000, Kind.INCOME, Account.BUSINESS, "Ejemplo")
                db.execute("INSERT INTO goals(name,saved,target) VALUES (?,?,?)", listOf("Viaje a Cartagena","1625000","2500000"))
                db.execute("INSERT INTO goals(name,saved,target) VALUES (?,?,?)", listOf("Fondo de emergencia","1260000","3000000"))
                db.execute("INSERT INTO goals(name,saved,target) VALUES (?,?,?)", listOf("Computador nuevo","1900000","5000000"))
                put("seeded", "1")
            }
        }
    }
    private fun setting(key: String) = db.query("SELECT value FROM preferences WHERE key = ?", listOf(key)).firstOrNull()?.firstOrNull()
    private fun put(key: String, value: String) = db.execute("INSERT OR REPLACE INTO preferences(key,value) VALUES (?,?)", listOf(key,value))
    fun read(): FinanceData {
        val movements = db.query("SELECT id,title,category,amount,kind,account,date,transfer FROM movements ORDER BY id DESC").map {
            Movement(it[0].toLong(), it[1], it[2], it[3].toLong(), Kind.valueOf(it[4]), Account.valueOf(it[5]), it[6], it[7])
        }
        val goals = db.query("SELECT g.id,g.name,g.saved,g.target,COALESCE(p.amount,0),COALESCE(b.amount,0) FROM goals g LEFT JOIN goal_reservations p ON p.goal_id=g.id AND p.account='PERSONAL' LEFT JOIN goal_reservations b ON b.goal_id=g.id AND b.account='BUSINESS' ORDER BY g.id").map {
            Goal(it[0].toLong(),it[1],it[2].toLong(),it[3].toLong(),it[4].toLong(),it[5].toLong())
        }
        val profile = Profile(setting("name") ?: "Alejandra", Account.valueOf(setting("account") ?: "PERSONAL"),
            setting("visible") != "false", setting("dark") == "true")
        return FinanceData(movements,goals,profile)
    }
    private fun insert(title: String, category: String, amount: Long, kind: Kind, account: Account, date: String, transfer: String = "") {
        db.execute("INSERT INTO movements(title,category,amount,kind,account,date,transfer) VALUES (?,?,?,?,?,?,?)",
            listOf(title,category,amount.toString(),kind.name,account.name,date,transfer))
    }
    fun addMovement(title: String, category: String, amount: Long, kind: Kind, account: Account, date: String) {
        require(title.isNotBlank() && category.isNotBlank() && amount in 1..MAX_AMOUNT) { "Revisa los datos del movimiento." }
        db.transaction { insert(title.trim(),category,amount,kind,account,date) }
    }
    fun editMovement(movement: Movement, title: String, category: String, amount: Long, kind: Kind) {
        require(movement.transfer.isEmpty()) { "Este movimiento interno se gestiona desde su pantalla correspondiente." }
        require(title.isNotBlank() && category.isNotBlank() && amount in 1..MAX_AMOUNT) { "Revisa los datos del movimiento." }
        db.transaction { db.execute("UPDATE movements SET title=?,category=?,amount=?,kind=? WHERE id=? AND transfer=''", listOf(title.trim(),category,amount.toString(),kind.name,movement.id.toString())) }
    }
    fun deleteMovement(movement: Movement) {
        db.transaction {
            val stored = read().movements.firstOrNull { it.id == movement.id } ?: error("El movimiento ya no existe.")
            require(!stored.isGoalSaving) { "Gestiona los abonos y retiros desde Metas." }
            if (stored.transfer.isEmpty()) db.execute("DELETE FROM movements WHERE id=?", listOf(stored.id.toString()))
            else db.execute("DELETE FROM movements WHERE transfer=?", listOf(stored.transfer))
        }
    }
    fun transfer(from: Account, amount: Long, date: String) {
        require(amount in 1..MAX_AMOUNT) { "Monto inválido." }
        val to = if (from == Account.PERSONAL) Account.BUSINESS else Account.PERSONAL
        db.transaction {
            require(read().balance(from) >= amount) { "Saldo insuficiente para transferir." }
            val id = db.query("SELECT COALESCE(MAX(id),0)+1 FROM movements").first().first()
            insert("Transferencia a ${to.label}", "Transferencia", amount,Kind.EXPENSE,from,date,id)
            insert("Transferencia de ${from.label}", "Transferencia", amount,Kind.INCOME,to,date,id)
        }
    }
    fun addGoal(name: String,target: Long) {
        require(name.isNotBlank() && target in 1..MAX_AMOUNT) { "Nombre o meta inválidos." }
        db.transaction { db.execute("INSERT INTO goals(name,saved,target) VALUES (?,0,?)",listOf(name.trim(),target.toString())) }
    }
    private fun currentGoal(id: Long): Goal = read().goals.firstOrNull { it.id == id }
        ?: error("La meta ya no existe.")
    private fun setReserved(goal: Goal, account: Account, amount: Long) {
        db.execute("INSERT OR REPLACE INTO goal_reservations(goal_id,account,amount) VALUES (?,?,?)",
            listOf(goal.id.toString(),account.name,amount.toString()))
    }
    fun editGoal(goal: Goal,name: String,target: Long) {
        db.transaction {
            val current = currentGoal(goal.id)
            require(name.isNotBlank() && target in 1..MAX_AMOUNT && target >= current.saved) { "El objetivo no puede ser inferior al ahorro actual." }
            db.execute("UPDATE goals SET name=?,target=? WHERE id=?",listOf(name.trim(),target.toString(),current.id.toString()))
        }
    }
    fun contribute(goal: Goal,amount: Long,account: Account,date: String) {
        db.transaction {
            val current = currentGoal(goal.id)
            require(amount in 1..MAX_AMOUNT && amount <= current.target-current.saved) { "El aporte supera lo que falta por ahorrar o es inválido." }
            require(read().balance(account) >= amount) { "Saldo disponible insuficiente." }
            db.execute("UPDATE goals SET saved=saved+? WHERE id=?",listOf(amount.toString(),current.id.toString()))
            setReserved(current,account,current.reserved(account)+amount)
            insert("Abono a ${current.name}","Ahorro",amount,Kind.EXPENSE,account,date,"goal-saving")
        }
    }
    fun withdraw(goal: Goal,amount: Long,account: Account,date: String) {
        db.transaction {
            val current = currentGoal(goal.id)
            require(amount in 1..MAX_AMOUNT && amount <= current.reserved(account)) { "El retiro supera lo reservado desde esta cuenta." }
            db.execute("UPDATE goals SET saved=saved-? WHERE id=?",listOf(amount.toString(),current.id.toString()))
            setReserved(current,account,current.reserved(account)-amount)
            insert("Retiro de ${current.name}","Ahorro",amount,Kind.INCOME,account,date,"goal-saving")
        }
    }
    fun deleteGoal(goal: Goal,date: String) {
        db.transaction {
            val current = currentGoal(goal.id)
            Account.entries.forEach { account ->
                val amount = current.reserved(account)
                if (amount > 0) insert("Devolución de ${current.name}","Ahorro",amount,Kind.INCOME,account,date,"goal-saving")
            }
            db.execute("DELETE FROM goal_reservations WHERE goal_id=?",listOf(current.id.toString()))
            db.execute("DELETE FROM goals WHERE id=?",listOf(current.id.toString()))
        }
    }
    fun saveProfile(profile: Profile) {
        require(profile.name.isNotBlank()) { "El nombre no puede estar vacío." }
        db.transaction {
            put("name",profile.name.trim()); put("account",profile.account.name)
            put("visible",profile.balanceVisible.toString()); put("dark",profile.dark.toString())
        }
    }
}
