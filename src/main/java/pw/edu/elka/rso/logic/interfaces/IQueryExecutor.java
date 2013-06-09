package pw.edu.elka.rso.logic.interfaces;

import pw.edu.elka.rso.logic.beans.QueryInfo;
import pw.edu.elka.rso.logic.beans.QueryType;
import pw.edu.elka.rso.server.tasks.QueryTask;

public interface IQueryExecutor {
  public void executeProcedure(QueryInfo queryInfo, QueryType queryType, QueryTask queryTaskReceived) throws ClassNotFoundException;
}

