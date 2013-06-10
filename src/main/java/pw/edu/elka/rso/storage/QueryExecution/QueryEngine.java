package pw.edu.elka.rso.storage.QueryExecution;

import pw.edu.elka.rso.storage.DataRepresentation.*;
import pw.edu.elka.rso.storage.QueryResult;
import pw.edu.elka.rso.storage.SqlDescription;

import java.util.HashMap;

/** Interface of shard's data layer
 */
public class QueryEngine {
    protected HashMap<String, Integer> name2TableId = null;
    protected HashMap<Integer, Table> tables = null;
    protected int freeTableId = DefaultTables.__MAX__.ordinal();

    public QueryEngine() {
        // 1. Setup metadata and statistic
        // TODO
        name2TableId = new HashMap<String, Integer>();
        tables = new HashMap<Integer,Table>();

        TableSchema ts = new TableSchema();
        ts.addColumn("id", ColumnType.INT, 0);
        ts.addColumn("test", ColumnType.INT, 0);

        Table table = new Table(ts);
        table.createIndex("id");

        Record record = table.newRecord();
        record.setValue("id", 0);
        record.setValue("test", 100);
        table.insert(record);

        record.setValue("id", 1);
        record.setValue("test", 101);
        table.insert(record);
        record.setValue("id", 2);
        record.setValue("test", 102);
        table.insert(record);
        record.setValue("id", 3);
        record.setValue("test", 103);
        table.insert(record);
        record.setValue("id", 4);
        record.setValue("test", 104);
        table.insert(record);
        record.setValue("id", 5);
        record.setValue("test", 105);
        table.insert(record);
        record.setValue("id", 6);
        record.setValue("test", 106);
        table.insert(record);
        record.setValue("id", 7);
        record.setValue("test", 107);
        table.insert(record);
        record.setValue("id", 8);
        record.setValue("test", 108);
        table.insert(record);
        record.setValue("id", 9);
        record.setValue("test", 109);
        table.insert(record);


        int tableid = freeTableId++;
        name2TableId.put("test", tableid);
        tables.put(tableid, table);

    }

    public QueryResult query( SqlDescription query) {

        RootQueryVisitor visitor = new RootQueryVisitor(this);
        query.statement.accept(visitor);
        QueryResult queryResult = visitor.getQueryResult();
        queryResult.queryId =  query.id;
        return queryResult;
    }

    Table getTable( String name) {
        Integer key = name2TableId.get(name.toLowerCase());
        assert key != null;
        return tables.get(key);
    }
}