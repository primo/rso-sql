package pw.edu.elka.rso.server.utils;

import org.apache.log4j.Logger;
import pw.edu.elka.rso.server.Server;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 09.06.13
 * Time: 13:20
 * To change this template use File | Settings | File Templates.
 */
public class IncomingConnectionsThread implements Runnable{
  static Logger log = Logger.getLogger(IncomingConnectionsThread.class);

  private Server server;

  public IncomingConnectionsThread(Server server) {
    this.server = server;
  }

  @Override
  public void run() {
    log.debug("Nasluchuje na przychodzace polacenia! " + server.getServerDetails());
    while (true) {
      server.manageIncomingConnections(server.getServerSocket());
    }
  }
}
