package pw.edu.elka.rso.storage;

import pw.edu.elka.rso.server.ShardDetails;

import java.util.ArrayList;
import java.util.Hashtable;

/** This class will hold local shard statistics like number of processed queries.
 */
public class ShardStatistics {
    public void incShardOpcount(int shardId,int count) {
        if (!shardOpcount.contains(new Integer(shardId)))
        {
            shardOpcount.put(shardId,1);
        }
        else
        {
            shardOpcount.put(shardId,shardOpcount.get(shardId)+count);
        }
    }
    public void incShardOpcount(int shardId) {
        incShardOpcount(shardId,1);
    }
    public void incShardOpcount(ShardDetails shard) {
        incShardOpcount(shard.getId(),1);
    }
    public void incShardOpcountBy(ShardDetails shard,int count) {
        incShardOpcount(shard.getId(),count);
    }
    public void resetShardOpcount(int shardId) {
        shardOpcount.put(shardId,0);
    }
    public void resetShardOpcount(ShardDetails shard) {
        shardOpcount.put(shard.getId(),0);
    }
    public int getShardOpcount(int shardId) {
        return shardOpcount.get(new Integer(shardId));
    }
    public int getShardOpcount(ShardDetails shard) {
        return shardOpcount.get(new Integer(shard.getId()));
    }
    public ShardDetails getTheLeastLoaded(ArrayList<ShardDetails> shards) {
        Integer op = getShardOpcount(shards.get(0).getId());
        ShardDetails shard = shards.get(0);
        for(ShardDetails sh: shards) {
            if (op > getShardOpcount(sh))
            {
                op = getShardOpcount(sh);
                shard = sh;
            }
        }
        return shard;
    }
    private static Hashtable<Integer,Integer> shardOpcount = new Hashtable<Integer,Integer>();
}
