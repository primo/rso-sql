package pw.edu.elka.rso.shard;

/** Interface of shard's data layer
 */
public interface QueryEngine {
    void query( SqlDescription query);
    ShardStatistics getStatistics();
    ShardMetadata getMetadata();
}
