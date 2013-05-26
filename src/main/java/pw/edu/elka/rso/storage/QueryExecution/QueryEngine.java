package pw.edu.elka.rso.storage.QueryExecution;

import pw.edu.elka.rso.storage.DataRepresentation.Table;
import pw.edu.elka.rso.storage.QueryResult;
import pw.edu.elka.rso.storage.SqlDescription;

import java.util.HashMap;

/** Interface of shard's data layer
 */
public class QueryEngine {
    private HashMap<String, Integer> name2TableId = null;
    private HashMap<Integer, Table> tables = null;


    public QueryResult query( SqlDescription query) {
        return new QueryResult(query.id);
    }

    protected QueryResult select(Table t, SqlDescription q){
        return new QueryResult(-1);
    }
    protected QueryResult insert(Table t, SqlDescription q){
        return new QueryResult(-1);
    }
    protected QueryResult drop(Table t, SqlDescription q){
        return new QueryResult(-1);
    }
    protected QueryResult update(Table t, SqlDescription q){
        return new QueryResult(-1);
    }
    protected QueryResult create(Table t, SqlDescription q){
        return new QueryResult(-1);
    }
    protected QueryResult delete(Table t, SqlDescription q){
        return new QueryResult(-1);
    }

}