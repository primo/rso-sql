package pw.edu.elka.rso.logic.interfaces;

import pw.edu.elka.rso.logic.beans.QueryInfo;
import pw.edu.elka.rso.server.QueryTask;
import pw.edu.elka.rso.server.QueryType;

public interface IQueryExecutor {
  public void executeProcedure(QueryInfo queryInfo, QueryType queryType, QueryTask queryTaskReceived) throws ClassNotFoundException;
}

