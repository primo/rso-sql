package pw.edu.elka.rso.server;

import pw.edu.elka.rso.storage.QueryResult;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 08.06.13
 */
public class QueryResultTask extends Task<QueryResult>{


  public QueryResultTask(QueryResult input) {
    this.input = input;
  }
}
