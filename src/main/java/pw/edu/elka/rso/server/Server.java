package pw.edu.elka.rso.server;

import org.apache.log4j.Logger;
import pw.edu.elka.rso.logic.beans.QueryExecutorImpl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 04.06.13
 * Time: 01:14
 * To change this template use File | Settings | File Templates.
 */
public class Server implements Runnable, ITaskManager {


  static Logger log = Logger.getLogger(Server.class);

  // The server socket.
  private ServerSocket serverSocket = null;
  private ShardDetails shardDetails;
  // The client socket.

  private Map<ShardDetails, Socket> connections = new HashMap<>();
  private Map<ShardDetails, Queue<Object>> outcomingData = new HashMap<>();

  private Queue<Task> tasks = new LinkedList<>();

  private Queue<ShardDetails> newConnections = new LinkedList<>();

  private QueryExecutorImpl queryExecutor;

  public QueryExecutorImpl getQueryExecutor() {
    return queryExecutor;
  }

  public void setQueryExecutor(QueryExecutorImpl queryExecutor) {
    this.queryExecutor = queryExecutor;
  }

  public void setNewConnections(Queue<ShardDetails> newConnections) {
    this.newConnections = newConnections;
  }

  public Server(int portNumber, int id) throws UnknownHostException {
    shardDetails = new ShardDetails(portNumber, InetAddress.getLocalHost(), id);
  }

  public ServerSocket getServerSocket() {
    return serverSocket;
  }

  public void setServerSocket(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
  }

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

  public Socket initConnectionToOtherShard(ShardDetails shardDetails) {
    Socket clientSocket = null;
    try {
      clientSocket = new Socket(shardDetails.getHost(), shardDetails.getPortNumber());
    } catch (IOException e) {
      log.debug("shiet nie polaczylem sie z serwerem " + shardDetails.getHost().toString() + " na porcie " + shardDetails.getPortNumber());
      e.printStackTrace();
    }

    return clientSocket;
  }

  public void initOutgoingConnections(ShardDetails input) {


    boolean connectionSucessfull = false;

    if (!connections.containsKey(input)) {

      Socket clientSocket = initConnectionToOtherShard(input);

      ObjectOutputStream oos;
      ObjectInputStream ois;
      try {
        oos = new ObjectOutputStream(clientSocket.getOutputStream());
        oos.writeObject(input);
        log.debug("Lacze sie z serwerem: " + clientSocket.toString());

        ois = new ObjectInputStream(clientSocket.getInputStream());

        String message = (String) ois.readObject();

        if ("ok".equals(message)) {
          connectionSucessfull = true;
        }


      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }

      if (connectionSucessfull) {
        connections.put(input, clientSocket);
        outcomingData.put(input, new LinkedList<>());
        log.debug("Success! Polaczono do " + input.toString());
      }
    } else {
      log.debug("Polaczenie do " + input.toString() + "exist.");
    }

  }


  public void manageIncomingConnections(ServerSocket serverSocket) {

    ObjectOutputStream oos;
    ObjectInputStream ois;
    IncomingDataThread incData;


    try {

      Socket clientSocket = serverSocket.accept();
      ois = new ObjectInputStream(clientSocket.getInputStream());

      ShardDetails shard = (ShardDetails) ois.readObject();
      incData = new IncomingDataThread(this, clientSocket);

      connections.put(shard, clientSocket);
      outcomingData.put(shard, new LinkedList<>());

      oos = new ObjectOutputStream(clientSocket.getOutputStream());
      oos.writeObject("ok");

      Thread incomingDataThread = new Thread(incData);
      incomingDataThread.start();

    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }


  }


  @Override
  public void run() {
    this.serverSocket = initServerSocket(this.shardDetails.getPortNumber());
    log.debug("Startuje serwer na porcie " + this.shardDetails.getPortNumber());

    IncomingConnectionsThread incomingConnectionsThread = new IncomingConnectionsThread(this);
    Thread incomingConnections = new Thread(incomingConnectionsThread);
    incomingConnections.start();


    while (true) {
      try {
        //LOGIC HERE
        if (tasks.size() > 0) {

          for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.poll();
            log.debug("Wykonuje zadanie" + task.toString());
            /**
             * DODAC OBSLUGE WSZYSTKICH TYPOW TASKOW
             */
            if (task instanceof SetConnectionTask) {
              SetConnectionTask connectionTask = (SetConnectionTask) task;
              initOutgoingConnections(connectionTask.input);
            }


            if (task instanceof QueryTask) {

              QueryTask queryTask = (QueryTask) task;
              Queue<Object> queue = outcomingData.get(queryTask.getWhereToExecuteQuery().get(0));
              queue.add(task);

              for (ShardDetails shardDetail : queryTask.getWhereToExecuteQuery()) {
                outcomingData.put(shardDetail, queue);
              }
            }
          }
        }
        // DODAC OBSLUGE TASKOW


        // DODA


        for (Map.Entry<ShardDetails, Socket> entry : connections.entrySet()) {
          Socket socket = entry.getValue();
          ShardDetails key = entry.getKey();

          ObjectOutputStream oos;
          //wyslij to co masz wyslac!
          for (int i = 0; i < outcomingData.get(key).size(); i++) {

            Object data = outcomingData.get(key).poll();
            oos = new ObjectOutputStream(socket.getOutputStream());
            log.debug("Wysylam do " + key.toString() + "cos" + data.toString());
            oos.writeObject(data);

          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  public ShardDetails getServerDetails() {
    return this.shardDetails;
  }

  @Override
  public void doTask(Task task) {
    tasks.add(task);
  }
};

class IncomingConnectionsThread implements Runnable {

  static Logger log = Logger.getLogger(IncomingConnectionsThread.class);

  private Server server;

  IncomingConnectionsThread(Server server) {
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

class IncomingDataThread implements Runnable {

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
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }

    }
  }
}
