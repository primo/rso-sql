package pw.edu.elka.rso.storage;

import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import org.apache.log4j.Logger;
import pw.edu.elka.rso.storage.QueryExecution.QueryEngine;

import java.security.InvalidParameterException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Provides interface to data base content.
 */
public class DataShard implements IDataShard, Runnable {

    private LinkedBlockingDeque<Entry<SqlDescription,Object>> tasks;
    LinkedBlockingDeque<Entry<QueryResult,Object>> results;
    private QueryResultReceiver queryResultReceiver = null;
    private Thread qrDispatcherThread = null;
    private Thread shardExecutor = null;
    boolean stopped = false;
    private long nextQueryId = 0;
    private QueryEngine engine = null;

    static Logger LOG = Logger.getLogger(DataShard.class);

    public DataShard() {
    }

    @Override
    public void query(SqlDescription query, Object queryContext) {
        tasks.offer(new SimpleEntry<SqlDescription, Object>(query, queryContext));
    }

    @Override
    public ShardMetadata getMetadata() {
        return null;
    }

    @Override
    public ShardStatistics getStatistics() {
        return null;
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
            SqlDescription query = null;
            Entry<SqlDescription,Object> job = null;
            try {
                job = tasks.take();
                query = job.getKey();
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            }
            QueryResult qr = engine.query(query);
            qr.queryId = query.id;
            results.offer(new SimpleEntry<QueryResult, Object>(qr,job.getValue()));
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
        tasks = new LinkedBlockingDeque<Entry<SqlDescription, Object>>();
        results = new LinkedBlockingDeque<Entry<QueryResult, Object>>();
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
        while (!managedObj.stopped) {
            QueryResult qr = null;
            Entry<QueryResult,Object> result=null;
            try {
                result = managedObj.results.take();
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            }
            queryResultReceiver.complete(result.getKey(),result.getValue());
        }
    }
}
