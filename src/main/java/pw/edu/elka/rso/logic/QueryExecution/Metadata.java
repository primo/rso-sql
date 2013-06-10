package pw.edu.elka.rso.logic.QueryExecution;
import java.util.*;

public class Metadata{
    //One instance
    public static Metadata metadata = new Metadata();
    private Metadata(){}

    private Map<String, Set<Integer>> table2partitions = new HashMap<>();
    private Map<Integer, Set<Integer>> partition2nodes = new HashMap<>();
    private Map<Integer, Float> currentLoad = new HashMap<>();
    PriorityQueue<RepCount> replicasCount = new PriorityQueue<>();

    //ID management
    private static Integer curr_ID = Integer.MIN_VALUE;
    public static Integer assignUID(){
        return curr_ID++;
    }

    Set<Integer> getTablePartitions(String str){
        return table2partitions.get(str);
    }

    public void updateMetadata(String table_name, Set<Integer> partition_IDs, Map<Integer, Set<Integer>> replication_mapping){
        MetadataUpdatePack metadata_update_pack = new MetadataUpdatePack(table_name, partition_IDs, replication_mapping);
        updateMetadata(metadata_update_pack);
        //@TODO jak wywołać to na bieżącym serwerze ?
        //server.doTask(new MetadataUpdateTask(metadata_update_pack));
    }

    public void updateMetadata(MetadataUpdatePack metadata_update_pack){
        table2partitions.put(metadata_update_pack.tableName, metadata_update_pack.partitionIDs);
        partition2nodes.putAll(metadata_update_pack.replicationMapping);
    }

    Set<Integer> getPartitionNodes(Integer partition_id){
        return partition2nodes.get(partition_id);
    }

    //Load Functionalities
    void updateLoad(int node_id, float new_load){
        currentLoad.put(node_id, new_load);
    }

    float getLoad(int node_id){
        return currentLoad.get(node_id);
    }
}
