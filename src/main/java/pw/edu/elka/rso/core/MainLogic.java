package pw.edu.elka.rso.core;

import com.sun.corba.se.impl.activation.ServerMain;
import pw.edu.elka.rso.logic.beans.InputManager;
import pw.edu.elka.rso.logic.beans.QueryExecutorImpl;
import pw.edu.elka.rso.logic.beans.QueryResultReceiverImpl;
import pw.edu.elka.rso.server.Server;
import pw.edu.elka.rso.server.SetConnectionTask;
import pw.edu.elka.rso.server.ShardDetails;
import pw.edu.elka.rso.server.Task;
import pw.edu.elka.rso.storage.DataShard;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 02.06.13
 * Time: 09:36
 * To change this template use File | Settings | File Templates.
 */
public class MainLogic {

  public static void main(String args[]) throws UnknownHostException {


//    inputManager.readInput("SelectFromClients");
    Server server1 = new Server(2222, 1);
    Server server2 = new Server(2223, 2);


    Thread thread1 = new Thread(server1);
    Thread thread2 = new Thread(server2);
    thread1.start();
    thread2.start();


    QueryResultReceiverImpl queryResultReceiver = new QueryResultReceiverImpl();

    InputManager inputManager = new InputManager();

    DataShard dataShard = new DataShard();
    dataShard.registerQueryResultReceiver(queryResultReceiver);

    QueryExecutorImpl queryExecutor = new QueryExecutorImpl(inputManager, dataShard, server1);
    queryExecutor.setQueryResultReceiver(queryResultReceiver);
    queryExecutor.DoTegoRootuj = server2;

    QueryExecutorImpl queryExecutor2 = new QueryExecutorImpl(inputManager, dataShard, server2);
    queryExecutor.setQueryResultReceiver(queryResultReceiver);
    queryExecutor.DoTegoRootuj = server1;
//
    server1.setQueryExecutor(queryExecutor);
    server2.setQueryExecutor(queryExecutor2);

    Thread queryExecutorThread = new Thread(queryExecutor);
    queryExecutorThread.start();
    dataShard.start();

    Thread queryExecutorThread2 = new Thread(queryExecutor2);
    queryExecutorThread2.start();
    dataShard.start();


    inputManager.readInput("SelectFromClients");


  }
}
