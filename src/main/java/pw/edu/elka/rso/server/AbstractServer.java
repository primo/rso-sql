package pw.edu.elka.rso.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 09.06.13
 */
public abstract class AbstractServer {

  // The server socket.
  protected ServerSocket serverSocket = null;
  protected ShardDetails shardDetails;
  protected Map<ShardDetails, Queue<Object>> outcomingData = new HashMap<ShardDetails, Queue<Object>>();
  // The client socket.

  public AbstractServer(int portNumber, int id) throws UnknownHostException {
    shardDetails = new ShardDetails(portNumber, InetAddress.getLocalHost(), id);
  }

  private static Logger log = Logger.getLogger(AbstractServer.class);

  public ServerSocket initServerSocket(int portNumber) {
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(portNumber);
    } catch (IOException ex) {
      log.debug("serwer nie wstal, gie gie");
      ex.printStackTrace();
    }
    return serverSocket;
  }

  public static Socket initConnectionToOtherShard(ShardDetails shardDetails) {
    Socket clientSocket = null;
    try {
      clientSocket = new Socket(shardDetails.getHost(), shardDetails.getPortNumber());
    } catch (IOException e) {
      log.debug("shiet nie polaczylem sie z serwerem " + shardDetails.getHost().toString() + " na porcie " + shardDetails.getPortNumber());
      e.printStackTrace();
    }

    return clientSocket;
  }

  public abstract void initOutgoingConnections(ShardDetails shardDetail);
  public abstract void manageIncomingConnections(ServerSocket serverSocket);


}
