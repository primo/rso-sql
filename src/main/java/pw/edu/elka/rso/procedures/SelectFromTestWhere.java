package pw.edu.elka.rso.procedures;

/**
 * Created with IntelliJ IDEA.
 * User: zewlak
 * Date: 28.05.13
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */

import net.sf.jsqlparser.JSQLParserException;
import pw.edu.elka.rso.logic.procedures.Procedure;

public class SelectFromTestWhere extends Procedure {

    public final String procedureName = "SelectFromTestWhere";

    public final String sql = "SELECT ID, TEST from test WHERE ID > 5;";

    public Procedure prepareProcedure() throws JSQLParserException {
        return super.prepareProcedure(procedureName, sql);
    }

}
