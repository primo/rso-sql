package pw.edu.elka.rso.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import pw.edu.elka.rso.server.ShardDetails;

/** This class will hold shard's metadata - information on partitionMetadata, replication etc...
 */
public class ShardMetadata implements Serializable {
    public static final int hashMaxIndex = 1 << 16;

    public boolean addPartition(PartitionMetadata p) {
        return partitionMetadata.add(p);
    }
    //Just in case
    public boolean removePartition(PartitionMetadata p) {
        return partitionMetadata.remove(p);
    }
    public SortedSet<PartitionMetadata> getAllPartitionMetadata() {

        return partitionMetadata.tailSet(null);
    }
    /**
     * Retrieves a list of partitions which may contain the specified element.
     *
     * @param element The specified element.
     * @return an ArrayList of partitions.
     */
    public ArrayList<PartitionMetadata> getPartitionsContaining(Object element) {
        ArrayList<PartitionMetadata> result = new ArrayList<PartitionMetadata>();
        int hash = element.hashCode() % hashMaxIndex;
        for (PartitionMetadata p: partitionMetadata) {
            if (p.getRangeStart() <= hash && p.getRangeEnd() >= hash) {
                result.add(p);
            }
        }
        return result;
    }

    private TreeSet<PartitionMetadata> partitionMetadata = new TreeSet<PartitionMetadata>();
}

class PartitionMetadata implements Serializable, Comparable<PartitionMetadata> {
    public final long id;
    //Range is inclusive
    public PartitionMetadata(int rangeStart, int rangeStop)
    {
        id = (long) (Math.random()*(1 << 60));
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
    private TreeSet<ShardDetails> replicaShards;
}