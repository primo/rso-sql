package pw.edu.elka.rso.core.communication;

import org.apache.log4j.Logger;
import pw.edu.elka.rso.logic.beans.InputManager;
import pw.edu.elka.rso.logic.beans.QueryExecutorImpl;
import pw.edu.elka.rso.logic.beans.QueryInfo;
import pw.edu.elka.rso.server.AbstractServer;
import pw.edu.elka.rso.server.ShardDetails;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Time: 12:58
 */
public class ClientServer extends AbstractServer implements Runnable, IPushToClientServer {

  static Logger log = Logger.getLogger(ClientRequestThread.class);
  //niedkonca shard, ale wykorzystam to co mamy w Server.java
  private Queue<QueryInfo> queries = new LinkedBlockingQueue<>();
  private InputManager inputManager;
  private Socket clientSocket;

  public ClientServer(int portNumber, int id, String ipAddress) throws UnknownHostException {
    super(portNumber, id, ipAddress);
  }

  public InputManager getInputManager() {

    return inputManager;
  }

  public void setInputManager(InputManager inputManager) {
    this.inputManager = inputManager;
  }

  public Queue<QueryInfo> getQueries() {
    return queries;
  }

  @Override
  public void initOutgoingConnections(ShardDetails shardDetail) {
    //blank
  }

  @Override
  public void manageIncomingConnections(ServerSocket serverSocket) {
    ClientRequestThread clientRequestThread;


    try {

      Socket newSocket = serverSocket.accept();
      this.clientSocket = newSocket;

      clientRequestThread = new ClientRequestThread(this, newSocket);
      Thread thread = new Thread(clientRequestThread);
      thread.start();

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void run() {

    serverSocket = initServerSocket(shardDetails.getPortNumber());
    manageIncomingConnections(serverSocket);

    //narazie tylko jedno polaczenie moze w lepszych czasach wiecej.

    while (true) {

      if (queries.size() > 0) {

        QueryInfo queryInfo = queries.poll();
        String procedureName = queryInfo.getProcedureName();
        Long queryId = queryInfo.getQueryId();
        LinkedList<String> params = queryInfo.getParameters();

        log.debug("Wykonuje procedure: " + procedureName);

        inputManager.readInput(procedureName, queryId, params);
      }

      if(getDataQueue().size()>0){
        Object result = getDataQueue().poll();

        ObjectOutputStream oos;
        try {
          oos = new ObjectOutputStream(this.clientSocket.getOutputStream());
//          log.debug("Wysylam do klienta "+result);
          oos.writeObject(result);
        } catch (IOException e) {
          e.printStackTrace();
        }

      }

    }


  }

  public Queue<Object> getDataQueue(){
    if(!outcomingData.containsKey(this.shardDetails)){
      outcomingData.put(this.shardDetails ,new LinkedBlockingQueue<>());
    }
    Queue<Object> queue = outcomingData.get(this.shardDetails);
    return queue;
  }

  @Override
  public void pushToClientServer(Object result) {
    Queue<Object> queue = getDataQueue();
    queue.add(result);
  }
}

class ClientRequestThread implements Runnable {

  static Logger log = Logger.getLogger(ClientRequestThread.class);
  private ClientServer server;
  private Socket clientSocket;

  public ClientRequestThread(ClientServer server, Socket clientSocket) {
    this.server = server;
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {

    log.debug("Startuje thread nasluchujaccy dane przychodzace od klienta!");
    ObjectInputStream ois = null;

    try {
      ois = new ObjectInputStream(clientSocket.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }

    while (true) {
      try {
        Object data = ois.readObject();

        if (data instanceof String) {
          String dataString = (String) data;
          String[] clientArguments = dataString.split(" ");
          String procedureName = clientArguments[0];
          String[] params = clientArguments.length <= 1 ? null : clientArguments[1].split(",");
          LinkedList<String> clientParams = null;
          if (params != null) {
            clientParams = new LinkedList<>();
            for (int i = 0; i < params.length; i++) {
              clientParams.add(params[i]);
            }
          }

          QueryInfo queryInfo = new QueryInfo(procedureName, QueryExecutorImpl.returnNewQueryId(), clientParams);
          server.getQueries().add(queryInfo);

        }
      } catch (ClassNotFoundException | IOException e) {
        log.debug("WTH tutaj?");
        e.printStackTrace();
      }

    }
  }
}