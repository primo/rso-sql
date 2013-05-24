package pw.edu.elka.rso.logic;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;

import java.io.Reader;
import java.io.StringReader;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 19.05.13
 */
public class jsqlParserExample {

    public static void main(final String[] args) throws JSQLParserException {
        CCJSqlParserManager parser = new CCJSqlParserManager();
        Reader reader = new StringReader("select tt.nazwisko, tz.nazwy from pracownicy tt join zupy tz on tz.id = tt.id where tt.id > 10;");
        Statement statement = parser.parse(reader);
        if("asdasd".equals("asd")) return;
    }


}
