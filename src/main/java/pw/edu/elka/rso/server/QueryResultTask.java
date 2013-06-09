package pw.edu.elka.rso.server;

import pw.edu.elka.rso.storage.QueryResult;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 08.06.13
 */
public class QueryResultTask extends Task<QueryResult> implements Serializable {

  private ShardDetails returnShard;

  public QueryResultTask(QueryResult input) {
    this.input = input;
  }

  public ShardDetails getReturnShard() {
    return returnShard;
  }

  public void setReturnShard(ShardDetails returnShard) {
    this.returnShard = returnShard;
  }
}
