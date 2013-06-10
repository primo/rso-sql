package pw.edu.elka.rso.logic.QueryExecution;

/**
 * Created with IntelliJ IDEA.
 * User: Tadeo
 * Date: 10.06.13
 * Time: 19:35
 * To change this template use File | Settings | File Templates.
 */
public class PartitionMetadata {
    private int id;
    private int[] hashRange = {0,0};
    public static final int hashMaxIndex = 1 << 16;

    public PartitionMetadata(int rangeStart, int rangeStop)
    {
        id = (int) (Math.random()*(1 << 60));
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
}
