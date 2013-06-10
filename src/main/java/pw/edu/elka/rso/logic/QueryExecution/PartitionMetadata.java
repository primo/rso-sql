package pw.edu.elka.rso.logic.QueryExecution;

/**
 * Created with IntelliJ IDEA.
 * User: Tadeo
 * Date: 10.06.13
 * Time: 19:35
 * To change this template use File | Settings | File Templates.
 */
public class PartitionMetadata implements Comparable<PartitionMetadata> {
    private int id;
    private int[] hashRange = {0,0};
    public static final int hashMaxIndex = 1 << 16;
    private String tableName;
    private int nodeId;

    public PartitionMetadata(int rangeStart, int rangeStop,String tableName, int nodeId)
    {
        id = (int) (Math.random()*(1 << 30));
        this.tableName = tableName;
        this.nodeId = nodeId;
        if (rangeStop > rangeStart &&
                rangeStop >= 0 && rangeStart >= 0)
        {
            hashRange[0] = rangeStart;
            hashRange[1] = rangeStop;
        }
    }

    public int getRangeStart() {
        return hashRange[0];
    }
    public int getRangeEnd() {
        return hashRange[1];
    }
    public void setHashRange(int start,int end)
    {
        hashRange[0] = Math.max(0,Math.min(hashMaxIndex,start));
        hashRange[1] = Math.max(0,Math.min(hashRange[0],end));
    }
    public boolean contains(Object o) {
        int hash = o.hashCode();
        if (hashRange[1] >= hash && hashRange[0] <= hash) return true;
        return false;
    }
    public int getId() {
        return id;
    }
    public int getNodeId() {
        return nodeId;
    }
    public int compareTo(PartitionMetadata p)
    {
        if (id < p.id) return -1;
        if (id > p.id) return 1;
        return 0;
    }
}
