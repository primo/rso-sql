package pw.edu.elka.rso.logic.QueryExecution;
import java.util.*;

/*
Klasa reprezentująca Metadane.
Ponieważ metadane dot. partycjonowania i replikacji są wolnozmienne to powinien być wyróżniony węzeł który zajmuje się
DMLami.
Dostęp z dowolnego miejsca przez Metadata.metadata
 */
public class Metadata{
    //One instance
    public static Metadata metadata = new Metadata();
    private Metadata(){}

    private Map<String, Set<Integer>> table2partitions = new HashMap<>();
    private Map<Integer, Set<Integer>> partition2nodes = new HashMap<>();
    private Map<Integer, Float> currentLoad = new HashMap<>();
    PriorityQueue<RepCount> replicasCount = new PriorityQueue<>();

    private static Integer curr_ID = Integer.MIN_VALUE;

    /*
    Przydziela unikalne IDki dla partycji, nodów etc.
    Unikalność w skali węzła, nie klastra.

    @see pw.edu.elka.rso.logic.QueryExecution.Metadata
     */
    public static Integer assignUID(){
        return curr_ID++;
    }

    /*
    Zwraca IDki partycji, które składają się na daną tabelę.

    @param table_name Nazwa tabeli.
     */
    Set<Integer> getTablePartitions(String table_name){
        return table2partitions.get(table_name);
    }

    /*
    Aktualizacja metadanych, automatyczne przesyłanie do innych węzłów. Asynchroniczne.

    @param table_name Nazwa nowej tabeli.
    @param partition_IDs IDki partycji, które składają sie na tabelę table_name.
    @param replication_mapping Mapowanie ID_Partycji -> ID_Nodów. Na każdym nodzie jest utrzymywana kopia danej partycji.
     */
    public void updateMetadata(String table_name, Set<Integer> partition_IDs, Map<Integer, Set<Integer>> replication_mapping){
        MetadataUpdatePack metadata_update_pack = new MetadataUpdatePack(table_name, partition_IDs, replication_mapping);
        updateMetadata(metadata_update_pack);
        //@TODO jak wywołać to na bieżącym serwerze ?
        //server.doTask(new MetadataUpdateTask(metadata_update_pack));
    }

    /*
    Alternatywna forma aktualizacji - bez dzielenia się nową wiedzą.
     */
    public void updateMetadata(MetadataUpdatePack metadata_update_pack){
        table2partitions.put(metadata_update_pack.tableName, metadata_update_pack.partitionIDs);
        partition2nodes.putAll(metadata_update_pack.replicationMapping);
    }

    /*
    Zwraca numery nodów, na których jest utrzymywana kopia danej partycji.
    @param partition_id ID partycji.
     */
    Set<Integer> getPartitionNodes(Integer partition_id){
        return partition2nodes.get(partition_id);
    }

    void updateLoad(int node_id, float new_load){
        currentLoad.put(node_id, new_load);
    }

    float getLoad(int node_id){
        return currentLoad.get(node_id);
    }
}
