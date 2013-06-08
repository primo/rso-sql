package pw.edu.elka.rso.logic.QueryExecution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlanningLB {
 /*   Map<Integer, Float> currentLoad = new HashMap <Integer, Float>();

    void updateLoad(Integer node_id, float new_value){
        currentLoad.put(node_id, new_value);
    }*/

    int[] chooseNodes(int[] partitions_ID){
        int part1 = partitions_ID[0];
        int part2 = partitions_ID[1];
        Set<Integer> nodes_with_1 = Metadata.metadata.getPartitionNodes(part1);
        Set<Integer> nodes_with_2 = Metadata.metadata.getPartitionNodes(part2);

        Set<Integer> nodes_with_both = new HashSet<Integer>(nodes_with_1);
        nodes_with_both.retainAll(nodes_with_2);

        if(nodes_with_both.isEmpty()){
            //Nie istnieje wezel z oboma

        }
        return null;
    }
}
