package pw.edu.elka.rso.logic.interfaces;

import net.sf.jsqlparser.statement.Statement;

public interface IQueryExecutor {
  public abstract void execute(Statement statement);
}

