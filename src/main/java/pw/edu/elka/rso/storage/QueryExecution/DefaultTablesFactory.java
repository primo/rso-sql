package pw.edu.elka.rso.storage.QueryExecution;

import pw.edu.elka.rso.storage.DataRepresentation.ColumnType;
import pw.edu.elka.rso.storage.DataRepresentation.DefaultTables;
import pw.edu.elka.rso.storage.DataRepresentation.Table;
import pw.edu.elka.rso.storage.DataRepresentation.TableSchema;

import java.security.InvalidParameterException;

/** Creates default per-node tables: metadata and statistics.
 */
public class DefaultTablesFactory  {
    public static Table createDefaultTable(DefaultTables id) {
        switch (id) {
            case METADATA:
                return createMetadataTable();
            case STATISTICS:
                return createStatisticsTable();
            default:
                throw new InvalidParameterException("No internal table with id:" + id.toString());
        }
    }

    protected static Table createMetadataTable() {
        TableSchema schema = new TableSchema();
        //! This schema is just a placeholder
        schema.addColumn("TABLE_NAME", ColumnType.CHAR, 50);
        schema.addColumn("TABLE_ID", ColumnType.INT, 0);
        schema.addColumn("PARTITIONING", ColumnType.INT, 0);
        // TODO specify the schema
        Table meta = new Table(schema);
        return meta;
    }

    protected static Table createStatisticsTable() {
        // TODO specify the schema and implement the table creation
        throw new UnsupportedOperationException("Creation of statistics table is not implemented.");
    }
}
