package pw.edu.elka.rso.server.utils;

import org.apache.log4j.Logger;
import pw.edu.elka.rso.server.Server;
import pw.edu.elka.rso.server.tasks.QueryResultTask;
import pw.edu.elka.rso.server.tasks.QueryTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 09.06.13
 */
public class IncomingDataThread implements Runnable {

  static Logger log = Logger.getLogger(IncomingDataThread.class);

  private Server server;
  private Socket clientSocket;

  public IncomingDataThread(Server server, Socket clientSocket) {
    this.server = server;
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {
    log.debug("Startuje thread nasluchujaccy dane przychodzace od klienta! " + server.getServerDetails());
    ObjectInputStream ois;

    while (true) {
      //odbierz to co masz odebrac dla socketa
      try {
        ois = new ObjectInputStream(clientSocket.getInputStream());
        Object data = ois.readObject();

        //DOPISAC OBSLUGE TASKOW
        if (data instanceof QueryTask) {
          log.debug("Odebralme(" + server.getServerDetails() + ") cos " + data.toString());
          server.getQueryExecutor().doTask((QueryTask) data);
        }
        //DOPISAC OBSLUGE TASKOW
        if (data instanceof QueryResultTask) {
          log.debug("Odebralme(" + server.getServerDetails() + ") cos " + data.toString());
          server.getQueryExecutor().doTask((QueryResultTask) data);
        }
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }

    }
  }
}