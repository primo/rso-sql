package pw.edu.elka.rso.logic.QueryExecution;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class MetadataUpdatePack implements Serializable {
    public String tableName;
    public Set<PartitionMetadata> partitions;
    public Map<Integer, Set<Integer>> replicationMapping;

    public MetadataUpdatePack(String table_name, Set<PartitionMetadata> partitions, Map<Integer, Set<Integer>> replication_mapping){
        tableName = table_name;
        this.partitions = partitions;
        replicationMapping = replication_mapping;
    }
}