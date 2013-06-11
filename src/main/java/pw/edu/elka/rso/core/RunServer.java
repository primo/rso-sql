package pw.edu.elka.rso.core;

import pw.edu.elka.rso.core.communication.ClientServer;
import pw.edu.elka.rso.logic.QueryExecution.Metadata;
import pw.edu.elka.rso.logic.beans.InputManager;
import pw.edu.elka.rso.logic.beans.QueryExecutorImpl;
import pw.edu.elka.rso.logic.beans.QueryResultReceiverImpl;
import pw.edu.elka.rso.server.Server;
import pw.edu.elka.rso.server.ShardDetails;
import pw.edu.elka.rso.storage.DataShard;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 11.06.13
 */
public class RunServer {

  public static void main(String[] args) throws UnknownHostException {
    InputManager inputManager;
    Server server;
    ClientServer clientServer;

    server = new Server(2222, 1);
    clientServer = new ClientServer(5000, 100);

    //InetAddress address = new Inet4Address();
    ShardDetails thisServer = server.getServerDetails();
    //UZUPELNIC InetAddress.getLocalHost() WARTOSCIAMI KOLEGOW
    ShardDetails server1 = new ShardDetails(2222, InetAddress.getLocalHost(), 3);
    ShardDetails server2 = new ShardDetails(2222, InetAddress.getLocalHost(), 4);
    ShardDetails server3 = new ShardDetails(2222, InetAddress.getLocalHost(), 5);
    ShardDetails server4 = new ShardDetails(2222, InetAddress.getLocalHost(), 6);


    Thread serverThread;
    Thread serverToClientThread;

    QueryResultReceiverImpl queryResultReceiver;
    DataShard dataShard;
    Metadata metadata;

    QueryExecutorImpl queryExecutor;
    Thread queryExecutorThread;

    inputManager = new InputManager();
    clientServer.setInputManager(inputManager);

    serverThread = new Thread(server);
    serverToClientThread = new Thread(clientServer);

    queryResultReceiver = new QueryResultReceiverImpl();
    dataShard = new DataShard();
    dataShard.registerQueryResultReceiver(queryResultReceiver);

    metadata = new Metadata(server);
    queryExecutor = new QueryExecutorImpl(inputManager, dataShard, server, metadata);
    queryExecutor.setQueryResultReceiver(queryResultReceiver);
    queryExecutor.setClientServer(clientServer);
    queryExecutorThread = new Thread(queryExecutor);

    serverThread.start();
    serverToClientThread.start();
    dataShard.start();
    queryExecutorThread.start();


  }
}


