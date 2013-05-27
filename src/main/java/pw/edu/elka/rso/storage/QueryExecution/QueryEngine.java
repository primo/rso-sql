package pw.edu.elka.rso.storage.QueryExecution;

import pw.edu.elka.rso.storage.DataRepresentation.Table;
import pw.edu.elka.rso.storage.QueryResult;
import pw.edu.elka.rso.storage.SqlDescription;

import java.util.HashMap;

/** Interface of shard's data layer
 */
public class QueryEngine {
    protected HashMap<String, Integer> name2TableId = null;
    protected HashMap<Integer, Table> tables = null;

    public QueryEngine() {
        // TODO
    }

    public QueryResult query( SqlDescription query) {

        query.statement.accept( new RootQueryVisitor() );
        return new QueryResult(query.id);
    }

    protected QueryResult select(Table t, SqlDescription q){
        return new QueryResult(q.id);
    }
    protected QueryResult insert(Table t, SqlDescription q){
        return new QueryResult(q.id);
    }
    protected QueryResult drop(Table t, SqlDescription q){
        return new QueryResult(q.id);
    }
    protected QueryResult update(Table t, SqlDescription q){
        return new QueryResult(q.id);
    }
    protected QueryResult create(Table t, SqlDescription q){
        return new QueryResult(q.id);
    }
    protected QueryResult delete(Table t, SqlDescription q){
        return new QueryResult(q.id);
    }

}