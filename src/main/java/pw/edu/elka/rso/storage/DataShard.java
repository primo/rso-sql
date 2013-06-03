package pw.edu.elka.rso.storage;

import org.apache.log4j.Logger;
import pw.edu.elka.rso.storage.QueryExecution.QueryEngine;

import java.security.InvalidParameterException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Provides interface to data base content.
 */
public class DataShard implements IDataShard, Runnable {

  private ConcurrentLinkedQueue<SqlDescription> tasks;
  ConcurrentLinkedQueue<QueryResult> results;
  private QueryResultReceiver queryResultReceiver = null;
  private Thread qrDispatcherThread = null;
  private Thread shardExecutor = null;
  boolean stopped = false;
  private long nextQueryId = 0;
  private QueryEngine engine = null;

  static Logger LOG = Logger.getLogger(DataShard.class);

  public DataShard() {
    tasks = new ConcurrentLinkedQueue<>();
    /* TODO Should be removed ? */
  }

  @Override
  public long query(SqlDescription query) {
    tasks.offer(query);
    query.id = nextQueryId;
    return nextQueryId++;
  }

  @Override
  public ShardMetadata getMetadata() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public ShardStatistics getStatistics() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean registerQueryResultReceiver(QueryResultReceiver queryResultReceiver) {
    this.queryResultReceiver = queryResultReceiver;
    return true;
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used
   * to create a thread, starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may
   * take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    while (!stopped) {
      LOG.debug("inside");
      SqlDescription query = tasks.poll();
      QueryResult qr = engine.query(query);
      results.offer(qr);
    }
  }

  public void start() {
    if (null == queryResultReceiver) {
      throw new InvalidParameterException("QueryResultReceiver was not specified.");
    }
    QueryResultDispatcher dispatcher = new QueryResultDispatcher(queryResultReceiver, this);

    qrDispatcherThread = new Thread(dispatcher);
    shardExecutor = new Thread(this);

    engine = new QueryEngine();
    shardExecutor.start();
    qrDispatcherThread.start();
    LOG.trace("DataShard started successfully.");
  }

  public void stop() throws InterruptedException {
    stopped = true;
    qrDispatcherThread.join();
    shardExecutor.join();
    LOG.trace("DataShard stopped.");
  }

}


class QueryResultDispatcher implements Runnable {
  static Logger LOG = Logger.getLogger(QueryResultDispatcher.class);

  private QueryResultReceiver queryResultReceiver = null;
  private DataShard managedObj = null;

  public QueryResultDispatcher(QueryResultReceiver queryResultReceiver, DataShard managedObj) {
    this.queryResultReceiver = queryResultReceiver;
    this.managedObj = managedObj;
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used
   * to create a thread, starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may
   * take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    LOG.debug("WALALAL");
    while (!managedObj.stopped) {
      LOG.debug("inside2");
      QueryResult qr = managedObj.results.poll();
      queryResultReceiver.complete(qr);
    }
  }
}