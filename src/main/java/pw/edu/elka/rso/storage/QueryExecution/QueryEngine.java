package pw.edu.elka.rso.storage.QueryExecution;

import pw.edu.elka.rso.storage.DataRepresentation.DefaultTables;
import pw.edu.elka.rso.storage.DataRepresentation.Table;
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
    }

    public QueryResult query( SqlDescription query) {

        RootQueryVisitor visitor = new RootQueryVisitor(this);
        query.statement.accept(visitor);
        QueryResult queryResult = visitor.getQueryResult();
        queryResult.queryId =  query.id;
        return queryResult;
    }

    Table getTable( String name) {
        Integer key = name2TableId.get(name);
        assert key != null;
        return tables.get(key);
    }
}