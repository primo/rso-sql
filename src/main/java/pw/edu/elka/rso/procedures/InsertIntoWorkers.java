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

public class InsertIntoWorkers extends Procedure {

    public final String procedureName = "InsertIntoTestWithParams";

    public final String sql = "INSERT INTO TEST VALUES (?,?);"; //firstname, lastname, salary

    public Procedure prepareProcedure() throws JSQLParserException {
        return super.prepareProcedure(procedureName, sql);
    }

}
