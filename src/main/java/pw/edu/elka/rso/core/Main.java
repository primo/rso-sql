package pw.edu.elka.rso.core;

import org.apache.log4j.Logger;
import pw.edu.elka.rso.logic.beans.InputManager;
import pw.edu.elka.rso.logic.beans.QueryExecutorImpl;
import pw.edu.elka.rso.logic.procedures.ProceduresManager;
import pw.edu.elka.rso.storage.DataShard;
import pw.edu.elka.rso.storage.QueryResultReceiver;

import java.io.*;

/**
 * Main class, bootstrapping the database.
 */
public class Main {

  static Logger logger = Logger.getLogger(Main.class);

  public static void main(String[] args) throws IOException {
    logger.debug("Start inputManager");

    InputManager inputManager = new InputManager();

    DataShard dataShard = new DataShard();
    QueryExecutorImpl queryExecutor = new QueryExecutorImpl(inputManager);
    queryExecutor.setDataShard(dataShard);

    QueryResultReceiver queryResultReceiver;

    Thread queryExecutorThread = new Thread(queryExecutor);



    dataShard.start();
    queryExecutorThread.start();


//    ProceduresManager proceduresManager = new ProceduresManager();
    //proceduresManager.prepareProcedures();

//    while (true) {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

      System.out.println("Type sql command | DUPA for exit");
//      String command = br.readLine();
      String command = "CREATE TABLE table\n" +
          "(\n" +
          "zupa INTEGER,\n" +
          ");";
      if("dupa".equals(command)) {
//        break;
      }

      Reader reader = new StringReader(command);
      logger.debug("Wczytuje zapytanie:" + reader);
      inputManager.readQuery(reader);



//    }

    /**
     *  WATEK KTORY ODBIERA BEDZIE CHODZIL CALY CZAS W TLE
     *  ZABIJAMY GO CZERWONYM KRZYZYKIEM PO TY JAK SIE PROGRAM WYWALI
     *  KKK TXZ
     */
  }

}
