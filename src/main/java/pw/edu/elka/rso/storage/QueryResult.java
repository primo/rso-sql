package pw.edu.elka.rso.storage;

import pw.edu.elka.rso.storage.DataRepresentation.Table;

import java.nio.ByteBuffer;
import java.util.List;

/**
 */
public class QueryResult {
    public long queryId;
    public boolean result;
    public Table output;

    public QueryResult(long queryId) {
        this.queryId = queryId;
        this.result = false;
        this.output = null;
    }
    public QueryResult() {}
}
