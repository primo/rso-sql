package pw.edu.elka.rso.storage;

import java.nio.ByteBuffer;

/**
 */
public class QueryResult {
    public long queryId;
    public boolean result;
    public ByteBuffer output;

    public QueryResult(long queryId) {
        this.queryId = queryId;
        this.result = false;
        this.output = null;
    }
    public QueryResult() {}
}
