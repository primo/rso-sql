package pw.edu.elka.rso.procedures;

import net.sf.jsqlparser.JSQLParserException;
import pw.edu.elka.rso.logic.procedures.Procedure;

/**
 */
public class CreateTest extends Procedure {
    public final String procedureName = "CreateTest";

    public final String sql = "CREATE TABLE test (id INTEGER, test INTEGER, test2 Char(20));";

    public Procedure prepareProcedure() throws JSQLParserException {
        return super.prepareProcedure(procedureName, sql);
    }
}
