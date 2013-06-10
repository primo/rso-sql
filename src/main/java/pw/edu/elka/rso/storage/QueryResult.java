package pw.edu.elka.rso.storage;

import pw.edu.elka.rso.storage.DataRepresentation.Table;
import pw.edu.elka.rso.storage.DataRepresentation.TableCarrier;

import java.io.Serializable;

/**
 */
public class QueryResult implements Serializable {
  public long queryId;
  public boolean result;
  //moze sie przydac, do np. zwracania kodu bledu, tudziesz informacji
  public String information;
  transient public Table output;
  private TableCarrier transportableOutput;


  public QueryResult(long queryId) {
    this.queryId = queryId;
    this.result = false;
    this.output = null;
  }

  public boolean prepareForTransport() {
      if (null == output)
          return false;
      transportableOutput = TableCarrier.convertToCarrier(output);
      return true;
  }

  public boolean prepareForReading() {
      if (null == transportableOutput)
          return false;
      output = TableCarrier.convertToTable(transportableOutput);
      return true;
  }

  public QueryResult() {
  }

  @Override
  public String toString() {
    return "QueryResult{" +
        "queryId=" + queryId +
        ", result=" + result +
        ", information='" + information + '\'' +
        //", output=" + output +
        '}';
  }
}
