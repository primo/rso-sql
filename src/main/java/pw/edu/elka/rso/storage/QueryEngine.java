package pw.edu.elka.rso.storage;

/** Interface of shard's data layer
 */
public interface QueryEngine {
    QueryResult query( SqlDescription query);
}