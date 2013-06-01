package pw.edu.elka.rso.logic.beans;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import org.apache.log4j.Logger;
import pw.edu.elka.rso.logic.interfaces.IQueryReader;

import java.io.Reader;

public class QueryReaderImpl implements IQueryReader {

  static Logger logger = Logger.getLogger(QueryReaderImpl.class);
  Statement parsedQuery;
  CCJSqlParserManager parser;

  public QueryReaderImpl() {
    this.parser = new CCJSqlParserManager();
  }

  public boolean validateQuery(Reader query) {
    try {
      parsedQuery = parser.parse(query);
      logger.debug("Zapytanie zostlao pomyslnie przeparsowane.");

      return true;
    } catch (JSQLParserException e) {
      logger.debug("Blad podczas parsowania zapytania.");
      e.printStackTrace();
    }
    return false;
  }

  public Statement getParsedQuery() {
    return this.parsedQuery;
  }

  public void setParsedQuery(Statement parsedQuery) {
    this.parsedQuery = parsedQuery;
  }
}

