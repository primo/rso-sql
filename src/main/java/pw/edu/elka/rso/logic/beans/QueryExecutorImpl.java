package pw.edu.elka.rso.logic.beans;

import net.sf.jsqlparser.statement.Statement;
import org.apache.log4j.Logger;
import pw.edu.elka.rso.logic.interfaces.IQueryExecutor;
import pw.edu.elka.rso.logic.procedures.Procedure;
import pw.edu.elka.rso.logic.procedures.ProceduresManager;
import pw.edu.elka.rso.storage.IDataShard;
import pw.edu.elka.rso.storage.QueryExecution.QueryEngine;
import pw.edu.elka.rso.storage.QueryResultReceiver;
import pw.edu.elka.rso.storage.SqlDescription;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.LinkedBlockingQueue;

public class QueryExecutorImpl implements Observer, Runnable, IQueryExecutor {
  static Logger logger = Logger.getLogger(QueryExecutorImpl.class);

  private IDataShard dataShard;
  private QueryResultReceiverImpl queryResultReceiver;

  private Observable consoleObservable;
  private LinkedBlockingQueue<String> queryQueue = new LinkedBlockingQueue<>();
  private ProceduresManager proceduresManager;


  public QueryExecutorImpl(Observable consoleObservable, IDataShard dataShard) {
    this.proceduresManager = ProceduresManager.getInstance();
    proceduresManager.prepareProcedures();

    this.consoleObservable = consoleObservable;
    this.dataShard = dataShard;

    this.consoleObservable.addObserver(this);
  }

  public void executeQuery(String procedureName) throws ClassNotFoundException {
    Procedure procedure = proceduresManager.getProcedure(procedureName);
    try {
      //TODO: do logic here

      SqlDescription sqlDescription = new SqlDescription();
      sqlDescription.statement = procedure.getParsedQuery();
      //TODO: get properID
      sqlDescription.id = 0;
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

      queryQueue.add(query);

    }
  }

  public void run() {
    while (true) {
      if (queryQueue.size() > 0) {

        String query = queryQueue.poll();
        logger.debug("Wykonuje procedure\"" + query + "\"");

        try {
          executeQuery(query);
        } catch (ClassNotFoundException e) {
          //TODO
          e.printStackTrace();
        }

      }
      if(!queryResultReceiver.getQueryResult().isEmpty()){
        logger.debug("COS JEST!1111");

      }
    }

  }

  public QueryResultReceiver getQueryResultReceiver() {
    return queryResultReceiver;
  }

  public void setQueryResultReceiver(QueryResultReceiverImpl queryResultReceiver) {
    this.queryResultReceiver = queryResultReceiver;
  }
}

