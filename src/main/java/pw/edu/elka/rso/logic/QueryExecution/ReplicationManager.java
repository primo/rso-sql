package pw.edu.elka.rso.logic.QueryExecution;

import java.util.*;

class RepCount implements Comparable<RepCount> {
    int nodeId;
    int repCount = 0;
    public RepCount(int node_id){
        nodeId = node_id;
    }

    public void increment(){
        repCount++;
    }

    @Override
    public int compareTo(RepCount o) {
        Integer me = repCount;
        return me.compareTo(o.repCount);
    }
}

public class ReplicationManager {
    static final int replicationFactor = 3;
    static Set<Integer> getPartitionReplicasNodes(Integer partition_id){
        return Metadata.metadata.getPartitionNodes(partition_id);
    }

    static Set<Integer> reserveNodesForPartitionReplicas(Integer partition_id){
        //moze byc malo wydajne, nie przeszkadza
        List<RepCount> temp = new ArrayList<RepCount>(replicationFactor);
        Set<Integer> retset = new HashSet<Integer>();
        for(int i=0; i<replicationFactor; i++){
            RepCount rep_count = Metadata.metadata.replicasCount.poll();
            if (rep_count == null)
                break;
            rep_count.increment();
            retset.add(rep_count.nodeId);
        }

        Metadata.metadata.replicasCount.addAll(temp);
        return retset;
    }
}
