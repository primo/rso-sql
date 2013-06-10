package pw.edu.elka.rso.server;

import org.apache.log4j.Logger;
import pw.edu.elka.rso.logic.beans.QueryExecutorImpl;
import pw.edu.elka.rso.server.tasks.*;
import pw.edu.elka.rso.server.utils.IncomingConnectionsThread;
import pw.edu.elka.rso.server.utils.IncomingDataThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 04.06.13
 * Time: 01:14
 */
public class Server extends AbstractServer implements Runnable, ITaskManager {


  static Logger log = Logger.getLogger(Server.class);

  private Map<ShardDetails, Socket> connections = new HashMap<>();


  private Queue<Task> tasks = new LinkedList<>();

  private QueryExecutorImpl queryExecutor;

  public Server(int portNumber, int id) throws UnknownHostException {
    super(portNumber, id);
  }

  public void initOutgoingConnections(ShardDetails shardDetail) {


    boolean connectionSucessfull = false;
    if (!connections.containsKey(shardDetail)) {
      Socket clientSocket = initConnectionToOtherShard(shardDetail);
      IncomingDataThread incData = new IncomingDataThread(this, clientSocket);

      ObjectOutputStream oos;
      ObjectInputStream ois;

      try {
        oos = new ObjectOutputStream(clientSocket.getOutputStream());
        oos.writeObject(this.getServerDetails());
        log.debug("Lacze sie z serwerem: " + clientSocket.toString());

        ois = new ObjectInputStream(clientSocket.getInputStream());

        String message = (String) ois.readObject();

        if ("ok".equals(message)) {
          connectionSucessfull = true;
        }

        Thread incomingDataThread = new Thread(incData);
        incomingDataThread.start();


      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }

      if (connectionSucessfull) {
        connections.put(shardDetail, clientSocket);
        outcomingData.put(shardDetail, new LinkedBlockingQueue<>());
        log.debug("Success! Polaczono do " + shardDetail.toString());
      }
    } else {
      log.debug("Polaczenie do " + shardDetail.toString() + "exist.");
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
      outcomingData.put(shard, new LinkedBlockingQueue<>());

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
            log.debug("Wykonuje zadanie " + task.input.toString());
            /**
             * DODAC OBSLUGE WSZYSTKICH TYPOW TASKOW
             */
            if (task instanceof SetConnectionTask) {
              SetConnectionTask connectionTask = (SetConnectionTask) task;
              initOutgoingConnections(connectionTask.input);
            }

            else if (task instanceof QueryTask) {

              QueryTask queryTask = (QueryTask) task;

              for (ShardDetails shardDetail : queryTask.getWhereToExecuteQuery()) {
                Queue<Object> queue = outcomingData.get(shardDetail);
                queue.add(task);
                outcomingData.put(shardDetail, queue);
              }
            }

            else if (task instanceof QueryResultTask) {

              QueryResultTask queryTask = (QueryResultTask) task;
              // Force the QueryResult to convert its content to a serializable form
              queryTask.getInput().prepareForTransport();
              Queue<Object> queue = outcomingData.get(queryTask.getReturnShard());
              queue.add(queryTask);

            }
            //Metadata Update
            // @author: PZ
            else if (task instanceof MetadataUpdateTask) {
                for (ShardDetails shardDetail : connections.keySet())
                    outcomingData.get(shardDetail).add(task);
            }
          }
        }

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

  public QueryExecutorImpl getQueryExecutor() {
    return queryExecutor;
  }

  public Map<ShardDetails, Socket> getConnections() {
    return connections;
  }

  public void setQueryExecutor(QueryExecutorImpl queryExecutor) {
    this.queryExecutor = queryExecutor;
  }

  public ServerSocket getServerSocket() {
    return serverSocket;
  }

  public void setServerSocket(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
  }
};



