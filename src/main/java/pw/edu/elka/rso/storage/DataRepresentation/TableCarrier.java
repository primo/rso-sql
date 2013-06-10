package pw.edu.elka.rso.storage.DataRepresentation;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides a serializable counterpart of a table, it does not provide
 * Table interface, it is only a mean of transport of Table's content.
 *
 * @see Table
 */
public class TableCarrier implements Serializable {

    final TableSchema tableSchema;
    final List<byte[]> mainList;
    final Map<String, Index> indexes;

    public TableCarrier(Table source) {
        tableSchema = source.tableSchema;
        indexes = source.indexes;
        mainList = new ArrayList<byte[]>();
        for (ByteBuffer b : source.mainList) {
            mainList.add(b.array());
        }
    }

    /** Converts a TableCarrier object into a Table.
     *
     * @param carrier
     * @return
     */
    public static Table convertToTable(TableCarrier carrier) {

        List<ByteBuffer> contentList = new ArrayList<ByteBuffer>();
        for (byte[] b : carrier.mainList) {
            contentList.add(ByteBuffer.wrap(b));
        }
        return new Table(carrier.tableSchema, contentList, carrier.indexes);
    }

    /** Converts a Table object into a TableCarrier representation.
     *
     * @param table
     * @return
     */
    public static TableCarrier convertToCarrier(Table table) {
        return new TableCarrier(table);
    }
}
