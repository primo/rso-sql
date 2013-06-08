package pw.edu.elka.rso.server;

import pw.edu.elka.rso.storage.SqlDescription;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 */
public class QueryTask extends Task<SqlDescription> implements Serializable {



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
}
