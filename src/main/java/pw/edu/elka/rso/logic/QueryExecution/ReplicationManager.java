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
        Integer my_rep_count = repCount;
        return my_rep_count.compareTo(o.repCount);
    }
}

public class ReplicationManager {
    static final int replicationFactor = 3;
    Metadata metadata;

    public ReplicationManager(Metadata new_metadata){
        metadata = new_metadata;
    }
    public Set<Integer> getPartitionReplicasNodes(Integer partition_id){
        return metadata.getPartitionNodes(partition_id);
    }

    public Set<Integer> reserveNodesForPartitionReplicas(Integer partition_id){
        //moze byc malo wydajne, nie przeszkadza
        List<RepCount> temp = new ArrayList<RepCount>(replicationFactor);
        Set<Integer> retset = new HashSet<Integer>();
        for(int i=0; i<replicationFactor; i++){
            RepCount rep_count = metadata.replicasCount.poll();
            if (rep_count == null)
                break;
            retset.add(rep_count.nodeId);
        }

        metadata.replicasCount.addAll(temp);
        return retset;
    }
}
