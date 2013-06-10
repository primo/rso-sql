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
 * Date: 02.06.13
 * Time: 09:36
 * To change this template use File | Settings | File Templates.
 */
public class MainLogic {

  public static void main(String args[]) throws UnknownHostException {

    InputManager inputManager = new InputManager();
//    InputManager inputManager2 = new InputManager();

    Server server1 = new Server(2222, 1);
//    Server server2 = new Server(2223, 2);

//    ClientServer clientServer = new ClientServer(5000,100);
//    clientServer.setInputManager(inputManager);


    Thread thread1 = new Thread(server1);
//    Thread thread2 = new Thread(server2);
    thread1.start();
//    thread2.start();
//    Thread thread3 = new Thread(clientServer);
//    thread3.start();


    QueryResultReceiverImpl queryResultReceiver = new QueryResultReceiverImpl();
    QueryResultReceiverImpl queryResultReceiver2 = new QueryResultReceiverImpl();

    DataShard dataShard = new DataShard();
    dataShard.registerQueryResultReceiver(queryResultReceiver);

    Metadata metadata = new Metadata(server1);
    QueryExecutorImpl queryExecutor = new QueryExecutorImpl(inputManager, dataShard, server1, metadata);
    queryExecutor.setQueryResultReceiver(queryResultReceiver);
//    queryExecutor.DoTegoRootuj = server2;
//    queryExecutor.setClientServer(clientServer);

//    QueryExecutorImpl queryExecutor2 = new QueryExecutorImpl(inputManager2, dataShard, server2);
//    queryExecutor2.setQueryResultReceiver(queryResultReceiver2);
//    queryExecutor2.DoTegoRootuj = server1;
//
    server1.setQueryExecutor(queryExecutor);
//    server2.setQueryExecutor(queryExecutor2);

    Thread queryExecutorThread = new Thread(queryExecutor);
    queryExecutorThread.start();
    //dataShard.start();

//    Thread queryExecutorThread2 = new Thread(queryExecutor2);
//    queryExecutorThread2.start();

    dataShard.start();


    inputManager.readInput("SelectFromClients", QueryExecutorImpl.returnNewQueryId(), null);


  }
}
