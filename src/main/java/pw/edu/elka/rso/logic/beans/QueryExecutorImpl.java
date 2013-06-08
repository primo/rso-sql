package pw.edu.elka.rso.logic.beans;

import org.apache.log4j.Logger;
import pw.edu.elka.rso.logic.interfaces.IQueryExecutor;
import pw.edu.elka.rso.logic.procedures.Procedure;
import pw.edu.elka.rso.logic.procedures.ProceduresManager;
import pw.edu.elka.rso.server.*;
import pw.edu.elka.rso.storage.IDataShard;
import pw.edu.elka.rso.storage.QueryResultReceiver;
import pw.edu.elka.rso.storage.SqlDescription;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueryExecutorImpl implements Observer, Runnable, IQueryExecutor, ITaskManager {
  static Logger logger = Logger.getLogger(QueryExecutorImpl.class);

  private IDataShard dataShard;
  private QueryResultReceiverImpl queryResultReceiver;
  private Server server;
  private Observable consoleObservable;
  private LinkedBlockingQueue<String> clientProcedureQueue = new LinkedBlockingQueue<>();
  private ProceduresManager proceduresManager;
  private Queue<Task> tasks = new LinkedBlockingQueue<>();

  private static int queryId = 0;

  public Server DoTegoRootuj;

  public QueryExecutorImpl(Observable consoleObservable, IDataShard dataShard, Server server) {
    this.proceduresManager = ProceduresManager.getInstance();
    proceduresManager.prepareProcedures();

    this.consoleObservable = consoleObservable;
    this.dataShard = dataShard;

    this.consoleObservable.addObserver(this);
    this.server = server;
  }

  public void executeProcedure(String procedureName, QueryType queryType) throws ClassNotFoundException {
    Procedure procedure = proceduresManager.getProcedure(procedureName);
    try {
      //logika powinna wiedziec gdzie jjuz jet polaczona


      SqlDescription sqlDescription = new SqlDescription();
      sqlDescription.statement = procedure.getParsedQuery();
      sqlDescription.toStringQuery(procedureName);
      sqlDescription.id = queryId++;

      QueryTask queryTask = new QueryTask(sqlDescription);

      if (queryType == QueryType.MANGED) {
        //TODO: ALL LOGIC GOES HERE
        //listy etcetc


        logger.debug("Doing some logic stuff.");
        LinkedList<ShardDetails> whereToConnect = new LinkedList<>();
        whereToConnect.add(DoTegoRootuj.getServerDetails());

        Task connectionTask = new SetConnectionTask(DoTegoRootuj.getServerDetails());
        server.doTask(connectionTask);
        logger.debug("Doing some execution stuff.");

        queryTask.getWhereToExecuteQuery().addAll(whereToConnect);
        queryTask.setQueryRoot(server.getServerDetails());
        server.doTask(queryTask);
      }


      /**
       * !!!!!!!
       * !!!!!!!
       * TUTAJ WYKONUJEMY ZAPYTANIE DO Sharda
       * !!!!!!!
       * !!!!!!!
       */
      dataShard.query(sqlDescription);

    } catch (Exception ex) {
      //TODO: ladne obluzycy wyjatek
      ex.printStackTrace();
    }
  }

  public void update(Observable o, Object arg) {
    if ((o instanceof InputManager)) {
      logger.debug("Zostalem powiadomiony");
      InputManager console = (InputManager) o;

      String query = console.getQueryQueue().poll();
      logger.debug("Dodaje do kolejki zapytanie \"" + query.toString() + "\"");

      clientProcedureQueue.add(query);

    }
  }

  public void run() {
    while (true) {

      if (clientProcedureQueue.size() > 0) {
        String query = clientProcedureQueue.poll();
        logger.debug("Wykonuje procedure\"" + query + "\"");
        try {
          executeProcedure(query, QueryType.MANGED);
        } catch (ClassNotFoundException e) {
          //TODO
          e.printStackTrace();
        }
      }

      if (tasks.size() > 0) {
        for (int i = 0; i < tasks.size(); i++) {
          Task task = tasks.poll();
          if (task instanceof QueryTask) {
            QueryTask queryTask = (QueryTask) task;
            logger.debug("Dostalem zapytanie: " + queryTask.getInput().getProcedureName());

            try {
              executeProcedure(queryTask.getInput().getProcedureName(), QueryType.RAW);
            } catch (ClassNotFoundException e) {
              logger.debug("Wywalilem sie przy probie wykonania zapytania" + queryTask.getInput().getProcedureName());
              e.printStackTrace();
            }
          }
        }
      }

//      if (!queryResultReceiver.getQueryResult().isEmpty()) {
//        logger.debug("COS JEST!1111");
//
//      }
    }

  }

  @Override
  public void doTask(Task task) {
    tasks.add(task);
  }

  public QueryResultReceiver getQueryResultReceiver() {
    return queryResultReceiver;
  }

  public void setQueryResultReceiver(QueryResultReceiverImpl queryResultReceiver) {
    this.queryResultReceiver = queryResultReceiver;
  }
}

