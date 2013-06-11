package pw.edu.elka.rso.procedures;

import net.sf.jsqlparser.JSQLParserException;
import pw.edu.elka.rso.logic.procedures.Procedure;

/**
 */
public class CreateTestx extends Procedure {
    public final String procedureName = "CreateTestx";

    public final String sql = "CREATE TABLE testx (id INTEGER, test INTEGER);";

    public Procedure prepareProcedure() throws JSQLParserException {
        return super.prepareProcedure(procedureName, sql);
    }
}
