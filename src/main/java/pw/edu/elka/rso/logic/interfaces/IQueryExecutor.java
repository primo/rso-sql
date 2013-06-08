package pw.edu.elka.rso.logic.interfaces;

import pw.edu.elka.rso.server.QueryType;

public interface IQueryExecutor {
  public void executeProcedure(String procedureName, QueryType queryType) throws ClassNotFoundException;
}

