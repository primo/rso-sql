package pw.edu.elka.rso.logic;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.*;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.select.SelectVisitor.*;
import net.sf.jsqlparser.schema.Table.*;

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
        Reader reader = new StringReader("select tt.nazwisko, tz.nazwy from pracownicy tt join zupy tz on tz.id = tt.id where tt.id > 10;");
        Statement statement = parser.parse(reader);
        Vector<Statement> statements = new Vector<Statement>();
        FileReader fileReader = new FileReader();
        Queue queue = new Queue();

        if("asdasd".equals("asd")) return;
 //       }

        try {
            fileReader.readFile(INPUT_FILE);
            for (int i = 0; i < fileReader.getSize(); i++){
                statement = parser.parse(new StringReader(fileReader.getElement(i)));
                statements.add(statement);
//                System.out.println(statements.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        queue.load(statements);
        for (int i = 0; i < queue.size(); i++){
            System.out.println("Statement: " + queue.getStatement(i));
            System.out.println("ID: " + queue.getHash(i));
            System.out.println("===");
        }

    }


}
