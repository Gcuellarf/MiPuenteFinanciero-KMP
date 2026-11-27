package verification
import co.edu.mipuente.kmp.data.*
import co.edu.mipuente.kmp.domain.*
import co.edu.mipuente.kmp.navigation.*
import java.util.Base64
import java.io.File
class Bridge(path:String): SqlStore, AutoCloseable {
    private val process=ProcessBuilder("python3","verification/sqlite_bridge.py",path).start()
    private val out=process.outputStream.bufferedWriter()
    private val input=process.inputStream.bufferedReader()
    private fun enc(s:String)=Base64.getEncoder().encodeToString(s.toByteArray())
    private fun dec(s:String)=String(Base64.getDecoder().decode(s))
    private fun send(op:String,sql:String,args:List<String>):List<List<String>> {
        out.write((listOf(op,enc(sql))+args.map(::enc)).joinToString("\t"));out.newLine();out.flush()
        val head=input.readLine().split('\t');check(head[0]=="OK"){dec(head[1])}
        return List(head[1].toInt()){input.readLine().split('\t').map(::dec)}
    }
    override fun execute(sql:String,args:List<String>){send("E",sql,args)}
    override fun query(sql:String,args:List<String>)=send("Q",sql,args)
    override fun transaction(block:()->Unit){execute("BEGIN");try{block();execute("COMMIT")}catch(e:Throwable){execute("ROLLBACK");throw e}}
    override fun close(){out.close();process.waitFor();input.close()}
}
fun main(){
    val file=File.createTempFile("mipuente-test-", ".sqlite")
    try {
        Bridge(file.path).use { store ->
            val r=FinanceRepository(store);val initial=r.read();check(initial.balance(Account.PERSONAL)==2500000L);check(initial.balance(Account.BUSINESS)==2365000L);check(initial.movements.size==4)
            r.addMovement("Compra de prueba","Comida",50000,Kind.EXPENSE,Account.PERSONAL,"hoy")
            check(r.read().balance(Account.PERSONAL)==2450000L);val m=r.read().movements.first()
            r.editMovement(m,"Compra editada","Otros",20000,Kind.EXPENSE);check(r.read().balance(Account.PERSONAL)==2480000L)
            r.deleteMovement(m);check(r.read().balance(Account.PERSONAL)==2500000L)
            r.transfer(Account.PERSONAL,100000,"hoy");val t=r.read();check(t.balance(Account.PERSONAL)==2400000L&&t.balance(Account.BUSINESS)==2465000L)
            check(runCatching{r.transfer(Account.PERSONAL,999999999,"hoy")}.isFailure);check(r.read()==t)
            r.deleteMovement(t.movements.first());check(r.read().balance(Account.PERSONAL)==2500000L&&r.read().balance(Account.BUSINESS)==2365000L)
            r.addGoal("Reserva",1000000);var g=r.read().goals.last();r.contribute(g,200000);g=r.read().goals.last();check(g.progress==0.2f)
            check(runCatching{r.contribute(g,900000)}.isFailure);check(runCatching{r.editGoal(g,"Inválida",100000)}.isFailure)
            r.editGoal(g,"Reserva nueva",1200000);r.saveProfile(Profile("Prueba",Account.BUSINESS,false,true));check(r.read().profile.dark)
            r.addMovement("Venta persistente","Ventas",100000,Kind.INCOME,Account.BUSINESS,"hoy")
        }
        Bridge(file.path).use { store -> val r=FinanceRepository(store);val d=r.read();check(d.movements.size==5);check(d.balance(Account.BUSINESS)==2465000L);check(d.goals.last().name=="Reserva nueva"&&d.goals.last().saved==200000L);check(d.profile==Profile("Prueba",Account.BUSINESS,false,true));r.deleteGoal(d.goals.last());check(r.read().goals.size==3) }
        check(parseAmount("0")==null&&parseAmount("-1")==null&&parseAmount("50000")==50000L);check(cop(2500000)=="$ 2.500.000 COP")
        val n=Navigator();n.select(Screen.PROFILE);n.push(Screen.ABOUT);n.pop();check(n.current==Screen.PROFILE);n.pop();check(n.current==Screen.HOME)
        println("PASS: seed, balances, CRUD movements, transfers, rollback, goals, preferences, reopening SQLite, validation, navigation")
    } finally { file.delete() }
}
