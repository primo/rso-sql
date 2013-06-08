package pw.edu.elka.rso.logic.QueryExecution;

import java.util.Map;
import java.util.Set;

public class Metadata{
    //One instance
    public static Metadata metadata = new Metadata();
    private Metadata(){}

    public Map<String, Set<Integer>> table2partitions;
    public Map<Integer, Set<Integer>> partition2nodes;
    public Map<Integer, Float> currentLoad;

    //ID management
    private static Integer curr_ID = Integer.MIN_VALUE;
    public static Integer assignID(){
        return curr_ID++;
    }

    Set<Integer> getTablePartitions(String str){
        return table2partitions.get(str);
    }

    Set<Integer> getPartitionNodes(Integer partition_id){
        return partition2nodes.get(partition_id);
    }

//    static Set<Integer> stripID(Set<MetadataObject> collection){
//        Set<Integer> return_set = new HashSet<Integer>();
//        for (MetadataObject mo : collection)
//            return_set.add(mo.ID);
//        return return_set;
//    }
}
