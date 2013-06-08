package pw.edu.elka.rso.server;

import pw.edu.elka.rso.storage.SqlDescription;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 */
public class QueryTask extends Task<SqlDescription> implements Serializable {

  ShardDetails queryRoot;

  public ShardDetails getQueryRoot() {
    return queryRoot;
  }

  public void setQueryRoot(ShardDetails queryRoot) {
    this.queryRoot = queryRoot;
  }

  public QueryTask(SqlDescription input) {
    this.input = input;
  }

  private LinkedList<ShardDetails> whereToExecuteQuery = new LinkedList<>();

  public LinkedList<ShardDetails> getWhereToExecuteQuery() {
    return whereToExecuteQuery;
  }

  public void setWhereToExecuteQuery(LinkedList<ShardDetails> whereToExecuteQuery) {
    this.whereToExecuteQuery = whereToExecuteQuery;
  }

  @Override
  public String toString() {
    return "QueryTask{" +
        "queryRoot=" + queryRoot +
        '}';
  }
}
