package pw.edu.elka.rso.logic.QueryExecution;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import pw.edu.elka.rso.server.Server;
import pw.edu.elka.rso.server.ShardDetails;
import pw.edu.elka.rso.storage.SqlDescription;

import java.util.*;

/*
Klasa reprezentująca Metadane.
Ponieważ metadane dot. partycjonowania i replikacji są wolnozmienne to powinien być wyróżniony węzeł który zajmuje się
DMLami.
Dostęp z dowolnego miejsca przez Metadata.metadata
 */
public class Metadata{
    Server server;
    //One instance
    public Metadata(Server a_server){
        server = a_server;
    }

    private Map<String, Set<Integer>> table2partitions = new HashMap<>();
    private Map<Integer, Set<Integer>> partition2nodes = new HashMap<>();
    private Map<Integer, PartitionMetadata> partitions = new HashMap<>();
    private Map<Integer, ShardDetails> shards = new HashMap<>();
    private Map<Integer, Float> currentLoad = new HashMap<>();
    PriorityQueue<RepCount> replicasCount = new PriorityQueue<>();

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
    public void updateMetadata(String table_name, Set<PartitionMetadata> partitions, Map<Integer, Set<Integer>> replication_mapping){

        MetadataUpdatePack metadata_update_pack = new MetadataUpdatePack(table_name, partitions, replication_mapping);
        updateMetadata(metadata_update_pack);

        //server.doTask(new MetadataUpdateTask(metadata_update_pack));
    }

    /*
    Alternatywna forma aktualizacji - bez dzielenia się nową wiedzą z innymi nodami.
     */
    public void updateMetadata(MetadataUpdatePack metadata_update_pack){
        TreeSet<Integer> ids = new TreeSet<Integer>();

        for(PartitionMetadata p: metadata_update_pack.partitions)
        {
            ids.add(p.getId());
            if(!partition2nodes.containsKey(p.getId()))
            {
                partition2nodes.put(p.getId(),new TreeSet<Integer>());
            }
            partition2nodes.get(p.getId()).add(p.getNodeId());
        }
        table2partitions.put(metadata_update_pack.tableName, ids);
        partition2nodes.putAll(metadata_update_pack.replicationMapping);

        //Zliczanie inkrementacji
        class MutableInt {
            int value = 1; // note that we start at 1 since we're counting
            public void increment () { ++value;      }
            public int  get ()       { return value; }
        }
        Map<Integer, MutableInt> freq = new HashMap<>();

        //dla kazdej partycji
        for (Set<Integer> rep_count : metadata_update_pack.replicationMapping.values()) {
            //dla kazdej kopii
            for (Integer node_id : rep_count){
                MutableInt count = freq.get(node_id);
                if (count == null)
                    freq.put(node_id, new MutableInt());
                else
                    count.increment();
            }
        }

        //Inkrementacja
        List<RepCount> updated_rep_counts = new ArrayList<>(metadata_update_pack.replicationMapping.size() * ReplicationManager.replicationFactor);
        for (Iterator<RepCount> iterator = replicasCount.iterator(); iterator.hasNext(); ) {
            RepCount rep_count = iterator.next();
            if (freq.containsKey(rep_count.nodeId)){
                iterator.remove();
                rep_count.repCount += freq.get(rep_count.nodeId).get();
            }
        }

        //Dodanie nowych wartości
        for(RepCount rep_count : updated_rep_counts)
            replicasCount.add(rep_count);
    }

    /*
    Zwraca numery nodów, na których jest utrzymywana kopia danej partycji.
    @param partition_id ID partycji.
     */
    Set<Integer> getPartitionNodes(Integer partition_id){
        return partition2nodes.get(partition_id);
    }

    public void addNode(ShardDetails shard_details){
        Integer id = shard_details.getId();
        shards.put(id, shard_details);
        replicasCount.add(new RepCount(id));
    }

    void updateLoad(int node_id, float new_load){
        currentLoad.put(node_id, new_load);
    }

    float getLoad(int node_id){
        return currentLoad.get(node_id);
    }

    public ArrayList<ShardDetails> getNodesContaining(SqlDescription sql)
    {
        return null;
        /*Statement statement = sql.statement;
        Set<Integer> prts = null;
        Set<Integer> result = new TreeSet<Integer>();
        if (statement instanceof Select)
        {
            SelectBody body =((Select) statement).getSelectBody();
            if (body instanceof PlainSelect)
            {
                prts = this.getTablePartitions( ((PlainSelect)body).getFromItem().toString() );
            }

            if (prts == null) return result;

            for (Integer partId : prts){
                result.addAll(partition2nodes.get(partId));
            }
            return result;
        }

        if (statement instanceof Insert)
        {
            prts = this.getTablePartitions( ((Insert) statement).getTable().getName() );
            ExpressionList list = (ExpressionList)((Insert) statement).getItemsList();
            PartitionMetadata partition;
            for(Integer id: prts)
            {
                partition = partitions.get(id);
                for(Expression e: ((ArrayList < Expression >)list.getExpressions()))
                {
                    if (partition.contains(e))
                    {
                        result.add(id);
                    }
                }
            }
            return result;
        }

        return result;*/
    }
}
