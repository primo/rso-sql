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
     * @param tableName Table name.
     * @return an ArrayList of partitions.
     */
    public ArrayList<PartitionMetadata> getPartitionsContaining(Object element,String tableName) {
        ArrayList<PartitionMetadata> result = new ArrayList<PartitionMetadata>();
        int hash = Math.abs(element.hashCode()) % hashMaxIndex;
        for (PartitionMetadata p: partitionMetadata) {
            if (p.getRangeStart() <= hash && p.getRangeEnd() >= hash && tableName == p.tableName) {
                result.add(p);
            }
        }
        return result;
    }
    public ArrayList<PartitionMetadata> getPartitionsContaining(String tableName) {
        ArrayList<PartitionMetadata> result = new ArrayList<PartitionMetadata>();
        for (PartitionMetadata p: partitionMetadata) {
            if (tableName.toLowerCase().equals(p.tableName.toLowerCase())) {
                result.add(p);
            }
        }
        return result;
    }

    private static TreeSet<PartitionMetadata> partitionMetadata = new TreeSet<PartitionMetadata>();
}