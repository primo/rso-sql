package pw.edu.elka.rso.storage;

import java.nio.ByteBuffer;
import java.util.List;

/**
 */
public class QueryResult {
    public long queryId;
    public boolean result;
    public List<ByteBuffer> output;

    public QueryResult(long queryId) {
        this.queryId = queryId;
        this.result = false;
        this.output = null;
    }
    public QueryResult() {}
}
