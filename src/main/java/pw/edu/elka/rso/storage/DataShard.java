package pw.edu.elka.rso.storage;

import java.lang.Thread;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created with IntelliJ IDEA.
 * User: primo
 * Date: 5/25/13
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataShard implements IDataShard, Runnable {

    private ConcurrentLinkedQueue<SqlDescription> tasks;
    private ConcurrentLinkedQueue<QueryResult> results;

    public DataShard(){

    }

    @Override
    public long query(SqlDescription query) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
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
        return false;  //To change body of implemented methods use File | Settings | File Templates.
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
        //To change body of implemented methods use File | Settings | File Templates.
    }


}


class QueryResultDispatcher implements Runnable{
    private QueryResultReceiver queryResultReceiver;

    public QueryResultDispatcher(QueryResultReceiver queryResultReceiver) {
        this.queryResultReceiver = queryResultReceiver;
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
        //To change body of implemented methods use File | Settings | File Templates.
    }
}