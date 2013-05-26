package pw.edu.elka.rso.core;

import org.apache.log4j.Logger;
import pw.edu.elka.rso.logic.beans.Console;
import pw.edu.elka.rso.logic.beans.QueryExecutorImpl;

import java.io.Reader;
import java.io.StringReader;

/**
 * Main class, bootstrapping the database.
 */
public class Main {

  static Logger logger = Logger.getLogger(Main.class);

  public static void main(String[] args) {
    logger.debug("Start Console");

    Console console = new Console();

    QueryExecutorImpl queryExecutor = new QueryExecutorImpl(console);
    Thread queryExecutorThread = new Thread(queryExecutor);
    queryExecutorThread.start();

    Reader reader = new StringReader("update pl_pracownicy set dupa = 2, dupa2=adas where id=32");
    console.readQuery(reader);


    /**
     *  WATEK KTORY ODBIERA BEDZIE CHODZIL CALY CZAS W TLE
     *  ZABIJAMY GO CZERWONYM KRZYZYKIEM PO TY JAK SIE PROGRAM WYWALI
     *  KKK TXZ
     */
  }

}
