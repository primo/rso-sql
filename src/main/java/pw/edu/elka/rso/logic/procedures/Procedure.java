package pw.edu.elka.rso.logic.procedures;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;

import java.io.Reader;
import java.io.StringReader;

/**
 * Created with IntelliJ IDEA.
 * User: zewlak
 * Date: 28.05.13
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */
public class Procedure {

    public String name;
    Statement statement;

    /*
    * method to execute statement
     * @param list of params to execute query
     * TODO return data type ???
     */
    public void run(){
        //execute statement
        System.out.println("Pobieram dane z bazy!");
    }

    public Procedure prepareProcedure(String name, String sql) throws JSQLParserException {
        Procedure procedure = new Procedure();

        procedure.name = name;

        CCJSqlParserManager parser = new CCJSqlParserManager();
        Reader reader = new StringReader(sql);
        procedure.statement = parser.parse(reader);

        /*
         * @TODO check if all statement tables, fields etc. exist
         * maybe by executing it once?
         */

        return procedure;
    }
}
