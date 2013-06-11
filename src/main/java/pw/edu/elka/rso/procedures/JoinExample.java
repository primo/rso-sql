package pw.edu.elka.rso.procedures;

import net.sf.jsqlparser.JSQLParserException;
import pw.edu.elka.rso.logic.procedures.Procedure;

/**
 */
public class JoinExample extends Procedure{
    public final String procedureName = "JoinExample";

    public final String sql = "select * from Test join Test2 on Test.id = Test2.id WHERE Test2.id > ? AND Test2.id < ?;";

    public Procedure prepareProcedure() throws JSQLParserException {
        return super.prepareProcedure(procedureName, sql);
    }
}
