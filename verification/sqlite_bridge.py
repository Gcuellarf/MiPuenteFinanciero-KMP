"""Solo para la prueba local JVM: adaptador SQLite real de Python por stdin/stdout."""
import base64, sqlite3, sys

def dec(x): return base64.b64decode(x).decode('utf-8')
def enc(x): return base64.b64encode(str(x).encode()).decode()
con=sqlite3.connect(sys.argv[1],isolation_level=None)
for line in sys.stdin:
    try:
        fields=line.rstrip('\n').split('\t'); op=fields[0]; sql=dec(fields[1]); args=[dec(x) for x in fields[2:]]
        cur=con.execute(sql,args)
        rows=cur.fetchall() if op=='Q' else []
        print('OK\t'+str(len(rows)),flush=True)
        for row in rows: print('\t'.join(enc(x) for x in row),flush=True)
    except Exception as e: print('ERROR\t'+enc(e),flush=True)
con.close()
