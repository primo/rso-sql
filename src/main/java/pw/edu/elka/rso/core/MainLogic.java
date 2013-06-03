package pw.edu.elka.rso.core;

import pw.edu.elka.rso.logic.beans.InputManager;
import pw.edu.elka.rso.logic.beans.QueryExecutorImpl;
import pw.edu.elka.rso.logic.beans.QueryResultReceiverImpl;
import pw.edu.elka.rso.storage.DataShard;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 02.06.13
 * Time: 09:36
 * To change this template use File | Settings | File Templates.
 */
public class MainLogic {

  public static void main(String args[]) {

    QueryResultReceiverImpl queryResultReceiver = new QueryResultReceiverImpl();
    InputManager inputManager = new InputManager();
    DataShard dataShard = new DataShard();
    dataShard.registerQueryResultReceiver(queryResultReceiver);
    QueryExecutorImpl queryExecutor = new QueryExecutorImpl(inputManager, dataShard);
    queryExecutor.setQueryResultReceiver(queryResultReceiver);

    Thread queryExecutorThread = new Thread(queryExecutor);
    queryExecutorThread.start();
    dataShard.start();

    inputManager.readInput("SelectFromClients");


  }
}
