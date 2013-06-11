package pw.edu.elka.rso.procedures;

import net.sf.jsqlparser.JSQLParserException;
import pw.edu.elka.rso.logic.procedures.Procedure;

/**
 * Created with IntelliJ IDEA.
 * User: zewlak
 * Date: 10.06.13
 * Time: 19:20
 * To change this template use File | Settings | File Templates.
 */
public class InsertIntoTestx extends Procedure {

    public final String procedureName = "InsertIntoTestx";

    public final String sql = "INSERT INTO TEST VALUES (?,?);";

    public Procedure prepareProcedure() throws JSQLParserException {
        return super.prepareProcedure(procedureName, sql);
    }
}
