package pw.edu.elka.rso.logic.QueryExecution;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class MetadataUpdatePack implements Serializable {
    public String tableName;
    public Set<Integer> partitionIDs;
    public Map<Integer, Set<Integer>> replicationMapping;

    public MetadataUpdatePack(String table_name, Set<Integer> partition_IDs, Map<Integer, Set<Integer>> replication_mapping){
        tableName = table_name;
        partitionIDs = partition_IDs;
        replicationMapping = replication_mapping;
    }
}