package pw.edu.elka.rso.storage;

/**
 * Created with IntelliJ IDEA.
 * User: primo
 * Date: 5/24/13
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IDataShard {
    long query( SqlDescription query);
    ShardMetadata getMetadata();
    ShardStatistics getStatistics();
    boolean registerQueryResultReceiver( QueryResultReceiver queryResultReceiver);
}
