package pw.edu.elka.rso.server;

import pw.edu.elka.rso.logic.beans.QueryInfo;
import pw.edu.elka.rso.storage.SqlDescription;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 */
public class QueryTask extends Task<SqlDescription> implements Serializable {

  private QueryType queryType;
  private ShardDetails queryRoot;
  private QueryInfo queryInfo;

  public ShardDetails getQueryRoot() {
    return queryRoot;
  }

  public QueryInfo getQueryInfo() {
    return queryInfo;
  }

  public void setQueryInfo(QueryInfo queryInfo) {
    this.queryInfo = queryInfo;
  }

  private transient LinkedList<ShardDetails> whereToExecuteQuery = new LinkedList<>();

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

  public QueryType getQueryType() {
    return queryType;
  }

  public void setQueryType(QueryType queryType) {
    this.queryType = queryType;
  }

  public void setQueryRoot(ShardDetails queryRoot) {
    this.queryRoot = queryRoot;
  }

  public QueryTask(SqlDescription input) {
    this.input = input;
  }
}
