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
        ts.addColumn("ID", ColumnType.INT, 0);
        ts.addColumn("TEST", ColumnType.INT, 0);

        Table table = new Table(ts);
        table.createIndex("ID");

        Record record = table.newRecord();
        record.setValue("ID", 0);
        record.setValue("TEST", 100);
        table.insert(record);

        record.setValue("ID", 1);
        record.setValue("TEST", 101);
        table.insert(record);
        record.setValue("ID", 2);
        record.setValue("TEST", 102);
        table.insert(record);
        record.setValue("ID", 3);
        record.setValue("TEST", 103);
        table.insert(record);
        record.setValue("ID", 4);
        record.setValue("TEST", 104);
        table.insert(record);
        record.setValue("ID", 5);
        record.setValue("TEST", 105);
        table.insert(record);
        record.setValue("ID", 6);
        record.setValue("TEST", 106);
        table.insert(record);
        record.setValue("ID", 7);
        record.setValue("TEST", 107);
        table.insert(record);
        record.setValue("ID", 8);
        record.setValue("TEST", 108);
        table.insert(record);
        record.setValue("ID", 9);
        record.setValue("TEST", 109);
        table.insert(record);


        int tableId = freeTableId++;
        name2TableId.put("test", tableId);
        tables.put(tableId, table);

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