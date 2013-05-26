package pw.edu.elka.rso.core;

import org.apache.log4j.Logger;
import pw.edu.elka.rso.logic.beans.InputManager;
import pw.edu.elka.rso.logic.beans.QueryExecutorImpl;

import java.io.*;

/**
 * Main class, bootstrapping the database.
 */
public class Main {

  static Logger logger = Logger.getLogger(Main.class);

  public static void main(String[] args) throws IOException {
    logger.debug("Start inputManager");

    InputManager inputManager = new InputManager();

    QueryExecutorImpl queryExecutor = new QueryExecutorImpl(inputManager);
    Thread queryExecutorThread = new Thread(queryExecutor);
    queryExecutorThread.start();



    while (true) {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

      System.out.println("Type sql command | DUPA for exit");
      String command = br.readLine();
      if("dupa".equals(command)) {
        break;
      }

      Reader reader = new StringReader(command);
      logger.debug("Wczytuje zapytanie:" + reader);
      inputManager.readQuery(reader);



    }

    /**
     *  WATEK KTORY ODBIERA BEDZIE CHODZIL CALY CZAS W TLE
     *  ZABIJAMY GO CZERWONYM KRZYZYKIEM PO TY JAK SIE PROGRAM WYWALI
     *  KKK TXZ
     */
  }

}
