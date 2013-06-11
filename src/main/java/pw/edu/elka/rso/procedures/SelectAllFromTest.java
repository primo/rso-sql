package pw.edu.elka.rso.procedures;

import net.sf.jsqlparser.JSQLParserException;
import pw.edu.elka.rso.logic.procedures.Procedure;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 11.06.13
 * Time: 01:59
 * To change this template use File | Settings | File Templates.
 */
public class SelectAllFromTest extends  Procedure{
  public final String procedureName = "SelectAllFromTest";

  public final String sql = "SELECT ID, TEST from test;";

  public Procedure prepareProcedure() throws JSQLParserException {
    return super.prepareProcedure(procedureName, sql);
  }
}
