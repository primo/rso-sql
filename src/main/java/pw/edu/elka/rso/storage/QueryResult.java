package pw.edu.elka.rso.storage;

import pw.edu.elka.rso.storage.DataRepresentation.Table;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.List;

/**
 */
public class QueryResult implements Serializable {
  public long queryId;
  public boolean result;
  //moze sie przydac, do np. zwracania kodu bledu, tudziesz informacji
  public String stringResult;
  public Table output;


  public QueryResult(long queryId) {
    this.queryId = queryId;
    this.result = false;
    this.output = null;
  }

  public QueryResult() {
  }

  @Override
  public String toString() {
    return "QueryResult{" +
        "queryId=" + queryId +
        ", result=" + result +
        ", stringResult='" + stringResult + '\'' +
        //", output=" + output +
        '}';
  }
}
