package pw.edu.elka.rso.logic.interfaces;

import net.sf.jsqlparser.statement.Statement;

import java.io.Reader;

public interface IQueryReader {
  public boolean validateQuery(Reader reader);
  public Statement getParsedQuery();
}
