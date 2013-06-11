package pw.edu.elka.rso.core;

import pw.edu.elka.rso.core.communication.ClientServer;
import pw.edu.elka.rso.logic.QueryExecution.Metadata;
import pw.edu.elka.rso.logic.beans.InputManager;
import pw.edu.elka.rso.logic.beans.QueryExecutorImpl;
import pw.edu.elka.rso.logic.beans.QueryResultReceiverImpl;
import pw.edu.elka.rso.server.Server;
import pw.edu.elka.rso.server.ShardDetails;
import pw.edu.elka.rso.storage.DataShard;

import java.net.UnknownHostException;
import java.util.LinkedList;

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

    server = new Server(2222, 10, "192.168.1.104");
    clientServer = new ClientServer(5000, 100, "192.168.1.104");

    //InetAddress address = new Inet4Address();
    ShardDetails thisServer = server.getServerDetails();
    //UZUPELNIC InetAddress.getLocalHost() WARTOSCIAMI KOLEGOW
    ShardDetails server1 = new ShardDetails(2222, 1, "192.168.1.101");
    ShardDetails server2 = new ShardDetails(2222, 4, "adresIP");
    ShardDetails server3 = new ShardDetails(2222, 5, "adresIP");
    ShardDetails server4 = new ShardDetails(2222, 6, "adresIP");

    LinkedList<ShardDetails> lolCodeCat = new LinkedList<>();

    lolCodeCat.add(server1);

    QueryExecutorImpl.lolCode = true;
    QueryExecutorImpl.lolCodeList = lolCodeCat;

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
    metadata = new Metadata(server);
    queryResultReceiver = new QueryResultReceiverImpl();
    dataShard = new DataShard(metadata);
    dataShard.registerQueryResultReceiver(queryResultReceiver);


    //metadata.addNode(server1);
    //metadata.addNode(server.getServerDetails());
    queryExecutor = new QueryExecutorImpl(inputManager, dataShard, server, metadata);
    queryExecutor.setQueryResultReceiver(queryResultReceiver);
    queryExecutor.setClientServer(clientServer);
    queryExecutorThread = new Thread(queryExecutor);
    server.setQueryExecutor(queryExecutor);
    serverThread.start();
    serverToClientThread.start();
    dataShard.start();
    queryExecutorThread.start();

    LinkedList<String> params = new LinkedList<>();
    params.add("999");
    params.add("999");

    //inputManager.readInput("CreateTestx", QueryExecutorImpl.returnNewQueryId(), null);
    //inputManager.readInput("InsertIntoTestx", QueryExecutorImpl.returnNewQueryId(), params);
    //inputManager.readInput("SelectAllFromTestx", QueryExecutorImpl.returnNewQueryId(), null);

  }
}


