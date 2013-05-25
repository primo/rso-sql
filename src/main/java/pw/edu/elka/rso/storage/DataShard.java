package pw.edu.elka.rso.storage;

import java.lang.Thread;

/**
 * Created with IntelliJ IDEA.
 * User: primo
 * Date: 5/25/13
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataShard extends Thread implements IDataShard {

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
}
