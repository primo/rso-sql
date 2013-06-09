package pw.edu.elka.rso.logic.QueryExecution;

import java.util.*;

class ReplicaAddress{
    public ReplicaAddress(int node_id, int partition_ID){
        nodeID = node_id;
        partitionID = partition_ID;
    }
    public int nodeID;
    public int partitionID;
}

public class Metadata{
    //One instance
    public static Metadata metadata = new Metadata();
    private Metadata(){}


    private Map<String, Set<Integer>> table2partitions = new HashMap<String, Set<Integer>>();
    private Map<Integer, Set<Integer>> partition2nodes = new HashMap<Integer, Set<Integer>>();
    private Map<Integer, Float> currentLoad = new HashMap<Integer, Float>();
    PriorityQueue<RepCount> replicasCount = new PriorityQueue<RepCount>();

    //ID management
    private static Integer curr_ID = Integer.MIN_VALUE;
    public static Integer assignUID(){
        return curr_ID++;
    }

    Set<Integer> getTablePartitions(String str){
        return table2partitions.get(str);
    }

    Collection<ReplicaAddress> addTable(){
        //@TODO Wywolaj partycjonowanie, powinno zwrocic IDki nowych partycji.
        //@TODO Dla kazdej nowej partycji wyznaczenie miejsc replikacji.

        return null;
    }

    //Przeslanie informacji o zmianach do wszystkich
    void commit(){

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
