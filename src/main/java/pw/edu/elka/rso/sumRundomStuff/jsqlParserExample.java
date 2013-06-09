package pw.edu.elka.rso.sumRundomStuff;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import pw.edu.elka.rso.logic.beans.FileReader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Vector;

/**
* Created with IntelliJ IDEA.
* User: michal
* Date: 19.05.13
*/
public class jsqlParserExample {

    final static String INPUT_FILE = "C:/Documents and Settings/Administrator/My Documents/GitHub/rso-sql/file.txt";

    public static void main(final String[] args) throws JSQLParserException {

        CCJSqlParserManager parser = new CCJSqlParserManager();
        Reader reader = new StringReader("select tt.nazwisko, tz.nazwy, tz.kk from pracownicy as tt, zupy as tz join tg on tg.p = zupy.q;");
        Statement statement = parser.parse(reader);
        Vector<Statement> statements = new Vector<Statement>();

        if("asdasd".equals("asd")) {
            return;
        }
//       }
    }


}
