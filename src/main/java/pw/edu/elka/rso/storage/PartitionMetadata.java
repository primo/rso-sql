package pw.edu.elka.rso.storage;

import pw.edu.elka.rso.server.ShardDetails;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: Tadeo
 * Date: 10.06.13
 * Time: 15:22
 * To change this template use File | Settings | File Templates.
 */
public class PartitionMetadata implements Serializable, Comparable<PartitionMetadata> {
    public final long id;
    public final String tableName;
    //Range is inclusive
    public PartitionMetadata(int rangeStart, int rangeStop, String tableName)
    {
        id = (long) (Math.random()*(1 << 60));
        this.tableName = tableName;
        if (rangeStop > rangeStart &&
                rangeStop >= 0 && rangeStart >= 0)
        {
            hashRange[0] = rangeStart;
            hashRange[1] = rangeStop;
        }
    }
    public int getRangeStart() {
        return hashRange[0];
    }
    public int getRangeEnd() {
        return hashRange[1];
    }
    public void setHashRange(int start,int end)
    {
        hashRange[0] = Math.max(0,Math.min(ShardMetadata.hashMaxIndex,start));
        hashRange[1] = Math.max(0,Math.min(hashRange[0],end));
    }
    public boolean addReplica(ShardDetails e) {
        return replicaShards.add(e);
    }
    public boolean removeReplica(ShardDetails e) {
        return replicaShards.remove(e);
    }
    public LinkedList<ShardDetails> getReplicas() {
        LinkedList<ShardDetails> list = new LinkedList<ShardDetails>();
        for (ShardDetails s: replicaShards)
        {
            list.add(s);
        }
        return list;
    }
    public boolean equals(PartitionMetadata p) {
        if (id == p.id)
            return true;
        return false;
    }
    public int compareTo(PartitionMetadata to) {
        if (to == null) return 1;
        if (id > to.id) return 1;
        if (id < to.id) return -1;
        return 0;
    }
    private int[] hashRange = {0,0};
    private TreeSet<ShardDetails> replicaShards = new TreeSet<ShardDetails>();
}