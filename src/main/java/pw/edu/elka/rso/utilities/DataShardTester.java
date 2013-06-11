package pw.edu.elka.rso.utilities;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import pw.edu.elka.rso.storage.DataRepresentation.Record;
import pw.edu.elka.rso.storage.DataShard;
import pw.edu.elka.rso.storage.QueryResult;
import pw.edu.elka.rso.storage.QueryResultReceiver;
import pw.edu.elka.rso.storage.SqlDescription;

import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 */
public class DataShardTester implements QueryResultReceiver{

    private int received = 0;
    private int nextId = 0;


    public static void main(String[] args) throws InterruptedException, JSQLParserException {
        List<Statement> statements = prepare();
        DataShard ds = new DataShard();
        DataShardTester dst = new DataShardTester();
        ds.registerQueryResultReceiver(dst) ;
        ds.start();
        for (Statement s : statements) {
            SqlDescription job = new SqlDescription(s);
            job.id = dst.nextId++;
            ds.query(job, null);
            System.out.println(s.toString()+" given "+(dst.nextId-1)+" ID");
        }

        synchronized (dst) {
            while (dst.received != statements.size()) {
                dst.wait();
            }
        }
        ds.stop();
    }

    static List<Statement> prepare() throws JSQLParserException {
        List<Statement> statements = new Vector<Statement>();
        CCJSqlParserManager parser = new CCJSqlParserManager();
        Statement statement = null;
        statement = parser.parse(new StringReader("CREATE TABLE TEST2 (id INTEGER, test INTEGER);"));
        statements.add(statement);
        Reader reader = new StringReader("SELECT id, test from TEST2;");
        statement = parser.parse(reader);
        statements.add(statement);
        statement = parser.parse(new StringReader("INSERT INTO TEST2 VALUES(1,2);"));
        statements.add(statement);
        statement = parser.parse(new StringReader("INSERT INTO TEST2 VALUES(2,2);"));
        statements.add(statement);
        statement = parser.parse(new StringReader("INSERT INTO TEST2 VALUES(3,2);"));
        statements.add(statement);
        statement = parser.parse(new StringReader("INSERT INTO TEST2 VALUES(4,2);"));
        statements.add(statement);
        statement = parser.parse(new StringReader("SELECT id, test from TEST2;"));
        statements.add(statement);
        statement = parser.parse(new StringReader("select ID, TEST from Test2;"));
        statements.add(statement);
        statement = parser.parse(new StringReader("select TEST, ID from Test2;"));
        statements.add(statement);
        statement = parser.parse(new StringReader("select * from Test2;"));
        statements.add(statement);
        statement = parser.parse(new StringReader("select Test2.* from Test2;"));
        statements.add(statement);
        statement = parser.parse(new StringReader("select Test2.* from Test2 WHERE ID > 2;"));
        statements.add(statement);
        statement = parser.parse(new StringReader("select Test2.* from Test2 WHERE ID >= 2;"));
        statements.add(statement);
        statement = parser.parse(new StringReader("select Test2.* from Test2 WHERE ID < 2;"));
        statements.add(statement);
        statement = parser.parse(new StringReader("select Test2.* from Test2 WHERE ID <= 2;"));
        statements.add(statement);
        statement = parser.parse(new StringReader("select Test2.* from Test2 WHERE ID = 2;"));
        statements.add(statement);
        statement = parser.parse(new StringReader("select Test2.* from Test2 WHERE ID != 2 AND ID != 3;"));
        statements.add(statement);
        statement = parser.parse(new StringReader("select Test2.* from Test2 WHERE ID != 2 OR ID != 3;"));
        statements.add(statement);
        statement = parser.parse(new StringReader("CREATE TABLE TEST3 (id INTEGER, test CHAR(20), PRIMARY KEY(id));"));
        statements.add(statement);
        statement = parser.parse(new StringReader("INSERT INTO TEST3 VALUES(1,'TEST44444');"));
        statements.add(statement);
        statement = parser.parse(new StringReader("INSERT INTO TEST3 VALUES(2,'TEST444');"));
        statements.add(statement);
        statement = parser.parse(new StringReader("INSERT INTO TEST3 VALUES(4,'TEST3');"));
        statements.add(statement);
        statement = parser.parse(new StringReader("select Test3.* from Test3 WHERE test3.id > 1;"));
        statements.add(statement);
        statement = parser.parse(new StringReader("CREATE TABLE TEST4 (id INTEGER, test DOUBLE, lala CHAR(10));"));
        statements.add(statement);
        statement = parser.parse(new StringReader("INSERT INTO TEST4 VALUES(1,0.44, 'HELLO');"));
        statements.add(statement);
        statement = parser.parse(new StringReader("INSERT INTO TEST4 VALUES(2,0.57, 'HELLO');"));
        statements.add(statement);
        statement = parser.parse(new StringReader("INSERT INTO TEST4 VALUES(13,0.78, 'HELLO');"));
        statements.add(statement);
        statement = parser.parse(new StringReader("select * from Test3 join Test2 on Test3.id = Test2.id WHERE Test3.id > 1 AND Test3.id < 6;"));
        statements.add(statement);
        statement = parser.parse(new StringReader("select * from Test3 ;"));
        statements.add(statement);
        return statements;
    }


    @Override
    public void complete(QueryResult qr, Object queryContext) {
        if (qr==null) {
            System.out.println("QR returned null");
            return;
        }
        System.out.println(Long.valueOf(qr.queryId).toString()+" resulted in: " + qr.result);
        if (null != qr.output) {
            System.out.println(qr.output.toString());
        }
        if (null != qr.information) {
            System.out.println(qr.information);
        }
        synchronized (this) {
            received++;
            notify();
        }
    }
}
