package pw.edu.elka.rso.core;

import pw.edu.elka.rso.core.communication.ClientServer;
import pw.edu.elka.rso.logic.QueryExecution.Metadata;
import pw.edu.elka.rso.logic.beans.InputManager;
import pw.edu.elka.rso.logic.beans.QueryExecutorImpl;
import pw.edu.elka.rso.logic.beans.QueryResultReceiverImpl;
import pw.edu.elka.rso.server.Server;
import pw.edu.elka.rso.storage.DataShard;

import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 09.06.13
 */
public class ExampleServer {
  public static void main(String[] args) throws UnknownHostException {

    InputManager inputManager = new InputManager();


    Server shardServer = new Server(2222, 1);

    ClientServer clientServer = new ClientServer(5000,100);
    clientServer.setInputManager(inputManager);

    Thread thread1 = new Thread(shardServer);
    Thread thread2 = new Thread(clientServer);
    thread1.start();
    thread2.start();


    QueryResultReceiverImpl queryResultReceiver = new QueryResultReceiverImpl();
    //QueryResultReceiverImpl queryResultReceiver2 = new QueryResultReceiverImpl();


    //InputManager inputManager2 = new InputManager();

    DataShard dataShard = new DataShard();
    dataShard.registerQueryResultReceiver(queryResultReceiver);

      Metadata metadata = new Metadata(shardServer);
    QueryExecutorImpl queryExecutor = new QueryExecutorImpl(inputManager, dataShard, shardServer, metadata);
    queryExecutor.setQueryResultReceiver(queryResultReceiver);
    //queryExecutor.DoTegoRootuj = clientServer;

//    QueryExecutorImpl queryExecutor2 = new QueryExecutorImpl(inputManager2, dataShard, clientServer);
//    queryExecutor2.setQueryResultReceiver(queryResultReceiver2);
//    queryExecutor2.DoTegoRootuj = shardServer;
//
    shardServer.setQueryExecutor(queryExecutor);
//    clientServer.setQueryExecutor(queryExecutor2);

    Thread queryExecutorThread = new Thread(queryExecutor);
    queryExecutorThread.start();
    //dataShard.start();

//    Thread queryExecutorThread2 = new Thread(queryExecutor2);
//    queryExecutorThread2.start();
    //dataShard.start();


//    inputManager.readInput("SelectFromClients", QueryExecutorImpl.returnNewQueryId(), null);
  }
}
