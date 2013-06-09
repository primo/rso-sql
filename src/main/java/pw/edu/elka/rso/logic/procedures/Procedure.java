package pw.edu.elka.rso.logic.procedures;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import org.apache.log4j.Logger;
import pw.edu.elka.rso.logic.interfaces.IQueryReader;

import java.io.Reader;
import java.io.StringReader;
import java.security.InvalidParameterException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zewlak
 * Date: 28.05.13
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */
public class Procedure implements IQueryReader {

  static Logger logger = Logger.getLogger(Procedure.class);
  public String name;
  private Statement statement;
  private CCJSqlParserManager parser = new CCJSqlParserManager();

  /*
  * method to execute statement
   * @param list of params to execute query
   * TODO return data type ???
   */
  public String run(List<String> params) {
    logger.debug(statement.toString());
    String tmpStatement = statement.toString();

    for (String s : params) {
      tmpStatement = tmpStatement.replaceFirst("\\?", s);
    }

    Reader reader = new StringReader(tmpStatement);
    try {
      this.statement = parser.parse(reader);
      logger.debug(tmpStatement);
    } catch (JSQLParserException e) {
      e.printStackTrace();
    }


    return "To sa dane z procedury: " + name + " z parametrami: " + params.toString();
  }

  public Procedure prepareProcedure(String name, String sql) throws JSQLParserException {
    Procedure procedure = new Procedure();

    procedure.name = name;
    Reader reader = new StringReader(sql);

        /*
         * @TODO check if all statement tables, fields etc. exist
         * maybe by executing it once?
         */
    if (!validateQuery(reader)) {
      throw new InvalidParameterException("Statement could not be parsed.");
    } else {
      procedure.statement = this.statement;
    }
    return procedure;
  }

  public boolean validateQuery(Reader query) {
    try {
      statement = parser.parse(query);

      return true;
    } catch (JSQLParserException e) {
      logger.debug("Blad podczas parsowania zapytania.");
      e.printStackTrace();
    }
    return false;
  }

  public Statement getParsedQuery() {
    return this.statement;
  }

  public void prepareParameters(List<String> params) {
    logger.debug(statement.toString());
    String tmpStatement = statement.toString();

    if (params != null) {
      for (String s : params) {
        tmpStatement = tmpStatement.replaceFirst("\\?", s);
      }

      Reader reader = new StringReader(tmpStatement);
      try {
        this.statement = parser.parse(reader);
        logger.debug(tmpStatement);
      } catch (JSQLParserException e) {
        e.printStackTrace();
      }
    }
  }
}
