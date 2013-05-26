package pw.edu.elka.rso.logic.beans;

import net.sf.jsqlparser.statement.Statement;
import org.apache.log4j.Logger;
import pw.edu.elka.rso.logic.interfaces.IQueryExecutor;
import pw.edu.elka.rso.storage.QueryEngine;
import pw.edu.elka.rso.storage.QueryResultReceiver;
import pw.edu.elka.rso.storage.SqlDescription;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.LinkedBlockingQueue;

public class QueryExecutorImpl implements Observer, Runnable, IQueryExecutor {
  static Logger logger = Logger.getLogger(QueryExecutorImpl.class);

  private QueryEngine queryEngine;
  private QueryResultReceiver queryResultReceiver;

  private Observable consoleObservable;
  private LinkedBlockingQueue<Statement> queryQueue = new LinkedBlockingQueue<Statement>();

  public QueryExecutorImpl(Observable observable) {
    this.consoleObservable = observable;
    this.consoleObservable.addObserver(this);
  }

  public QueryExecutorImpl(Observable consoleObservable, QueryEngine queryEngine) {
    this.consoleObservable = consoleObservable;
    this.queryEngine = queryEngine;
    this.consoleObservable.addObserver(this);
  }

  public void executeQuery(Statement statement) {
    SqlDescription query = new SqlDescription();
    //query.setStatement(statement);
    try {
      queryEngine.query(query);
    } catch (Exception ex) {
      //TODO: ladne obluzycy wyjatek
      ex.printStackTrace();
    }
  }

  public void update(Observable o, Object arg) {
    if ((o instanceof InputManager)) {
      logger.debug("Zostalem powiadomiony");
      InputManager console = (InputManager) o;

      Statement query = console.getQueryQueue().poll();
      logger.debug("Dodaje do kolejki zapytanie \"" + query.toString() + "\"");

      queryQueue.add(query);

    }
  }

  public void run() {
    while (true) {
      if (queryQueue.size() > 0) {

        Statement query = queryQueue.poll();
        logger.debug("Wykonuje zapytanie \"" + query.toString() + "\"");
        executeQuery(query);

      }

      //
      //???
//      QueryResult queryResult = queryResultReceiver.complete(new QueryResult(23));
    }

  }

  public QueryEngine getQueryEngine() {
    return this.queryEngine;
  }

  public void setQueryEngine(QueryEngine queryEngine) {
    this.queryEngine = queryEngine;
  }

  public Observable getConsoleObservable() {
    return consoleObservable;
  }

  public void setConsoleObservable(Observable consoleObservable) {
    this.consoleObservable = consoleObservable;
  }
}

