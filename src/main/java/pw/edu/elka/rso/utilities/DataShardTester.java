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

    public static void main(String[] args) throws InterruptedException, JSQLParserException {
        List<Statement> statements = prepare();
        DataShard ds = new DataShard();
        DataShardTester dst = new DataShardTester();
        ds.registerQueryResultReceiver(dst) ;
        ds.start();
        for (Statement s : statements) {
            long query = ds.query(new SqlDescription(s));
            System.out.println(s.toString()+" given "+query+" ID");
        }

        synchronized (dst) {
            while (dst.received != 7) {
                dst.wait();
            }
        }
        ds.stop();
    }

    static List<Statement> prepare() throws JSQLParserException {
        List<Statement> statements = new Vector<Statement>();
        CCJSqlParserManager parser = new CCJSqlParserManager();
        Statement statement = null;
        statement = parser.parse(new StringReader("CREATE TABLE TEST (id INTEGER, test INTEGER);"));
        statements.add(statement);
        Reader reader = new StringReader("SELECT id, test from TEST;");
        statement = parser.parse(reader);
        statements.add(statement);
        statement = parser.parse(new StringReader("INSERT INTO TEST VALUES(1,2);"));
        statements.add(statement);
        statement = parser.parse(new StringReader("INSERT INTO TEST VALUES(2,2);"));
        statements.add(statement);
        statement = parser.parse(new StringReader("INSERT INTO TEST VALUES(3,2);"));
        statements.add(statement);
        statement = parser.parse(new StringReader("INSERT INTO TEST VALUES(4,2);"));
        statements.add(statement);
        statement = parser.parse(new StringReader("SELECT id, test from TEST;"));
        statements.add(statement);
        return statements;
    }


    @Override
    public void complete(QueryResult qr) {
        if (qr==null) {
            System.out.println("QR returned null");
            return;
        }
        System.out.println(Long.valueOf(qr.queryId).toString()+" resulted in: " + qr.result);
        if (null != qr.output) {
            Iterator<Record> it = qr.output.iterator();
            Set<String> columns = qr.output.getTableSchema().getColumnNames();
            while (it.hasNext()) {
                Record r = it.next();
                for (String column : columns) {
                    System.out.print(r.getValue(column));
                    System.out.print(",");
                }
                System.out.println();
            }
        }
        synchronized (this) {
            received++;
            notify();
        }
    }
}
