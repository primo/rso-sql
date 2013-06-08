package pw.edu.elka.rso.procedures;

import net.sf.jsqlparser.JSQLParserException;
import pw.edu.elka.rso.logic.procedures.Procedure;

/**
 * Created with IntelliJ IDEA.
 * User: primo
 * Date: 6/4/13
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateTestTable  extends Procedure
{
    public final String procedureName = "CreateTestTable";

    public final String sql = "CREATE TABLE test (id INTEGER, name INTEGER);";

    public Procedure prepareProcedure() throws JSQLParserException {
        return super.prepareProcedure(procedureName, sql);
    }
}
