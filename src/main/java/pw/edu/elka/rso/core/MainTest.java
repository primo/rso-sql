package pw.edu.elka.rso.core;

import pw.edu.elka.rso.logic.QueryExecution.Metadata;
import pw.edu.elka.rso.logic.QueryExecution.PartitionMetadata;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: oem
 * Date: 10.06.13
 * Time: 19:35
 * To change this template use File | Settings | File Templates.
 */
public class MainTest {
    public static void main(String[] args){
        Metadata metadata = new Metadata(null);

        //Nowa tabela
        String name = "tabela";
        Set<PartitionMetadata> parts = new TreeSet<>();
        parts.add(new PartitionMetadata(0, 30000,"tabela",1));
        parts.add(new PartitionMetadata(30001, 70000,"tabela",1));
        Set<Integer> replicas = new TreeSet<>();
        replicas.add(3);
        replicas.add(4);
        Map<Integer, Set<Integer>> repmap = new TreeMap<>();
        repmap.put(1, replicas);
        repmap.put(2, replicas);

        metadata.updateMetadata(name, parts, repmap);
        int i = 0;
        i++;
    }
}
