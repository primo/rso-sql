package pw.edu.elka.rso.logic.QueryExecution;

import pw.edu.elka.rso.server.ShardDetails;
import pw.edu.elka.rso.storage.ShardStatistics;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Tadeo
 * Date: 09.06.13
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionLoadBalancer {
    public ShardDetails selectLeastLoadedNode(ArrayList<ShardDetails> shards) {
        return statistics.getTheLeastLoaded(shards);
    }
    public void incOperationCount(ShardDetails shard,int by) {
        statistics.incShardOpcountBy(shard,by);
    }
    public void incShardOpcountBy(int shardId,int by) {
        statistics.incShardOpcount(shardId,by);
    }
    public void resetOperationCount(ShardDetails shard) {
        statistics.resetShardOpcount(shard);
    }
    public void resetOperationCount(int shardId) {
        statistics.resetShardOpcount(shardId);
    }
    private ShardStatistics statistics = new ShardStatistics();
}
