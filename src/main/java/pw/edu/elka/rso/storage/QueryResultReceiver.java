package pw.edu.elka.rso.storage;

/** Interface required to receive QueryResults from IDataShard implementations.
 */
public interface QueryResultReceiver {
    public void complete( QueryResult qr);
}
