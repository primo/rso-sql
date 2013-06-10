package pw.edu.elka.rso.logic.QueryExecution;

import java.util.*;

public class PlanningLoadBalancer {
    Metadata metadata;

    PlanningLoadBalancer(Metadata new_metadata){
        metadata = new_metadata;
    }

    int chooseNode(int[] partitions_ID){
        //uwzglednia gdzie dane leza i ich obciazenie dla nodow
        //nie uwzglednia jeszcze przesyłu danych
        int part1 = partitions_ID[0];
        int part2 = partitions_ID[1];
        Set<Integer> nodes_with_1 = metadata.getPartitionNodes(part1);
        Set<Integer> nodes_with_2 = metadata.getPartitionNodes(part2);

        Set<Integer> candidates = new HashSet<>(nodes_with_1);
        candidates.retainAll(nodes_with_2);

        //Jesli nie istnieje wezel z oboma - wez dowolny
        if(candidates.isEmpty()){
            candidates.addAll(nodes_with_1);
            candidates.addAll(nodes_with_2);
        }

        //wez najmniej obciazony sposrod kandydatow
        int best_node = -1;
        float best_val = Float.MAX_VALUE;
        for(Integer node : candidates){
            float curr_val = metadata.getLoad(node);
            if (curr_val < best_val)
                best_node = node;
        }
        return best_node;
    }
}
