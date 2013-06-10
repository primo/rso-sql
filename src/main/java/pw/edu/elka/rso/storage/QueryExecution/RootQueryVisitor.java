package pw.edu.elka.rso.storage.QueryExecution;

import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

import org.apache.log4j.Logger;

import pw.edu.elka.rso.storage.DataRepresentation.ColumnType;
import pw.edu.elka.rso.storage.DataRepresentation.Record;
import pw.edu.elka.rso.storage.DataRepresentation.Table;
import pw.edu.elka.rso.storage.DataRepresentation.TableSchema;
import pw.edu.elka.rso.storage.QueryResult;

import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** Vists a JSQL Statement and describes its content in QueryEngine understandable
 * form.
 */
class RootQueryVisitor implements StatementVisitor {

    private QueryResult queryResult;
    private QueryEngine queryEngine;

    static Logger LOG = Logger.getLogger(RootQueryVisitor.class);

    /** C-tor.
     *
     * A reference to QueryEngine is passed so the Visitor can
     * execute requests directly on data.
     *
     * @param queryEngine object representing data shard
     */
    public RootQueryVisitor(QueryEngine queryEngine) {
        this.queryEngine = queryEngine;
    }

    /** Executes a select sql statement.
     *
     * FIXME:
     * + parse UNIONS
     * + understand WITH statement
     * o for example create a stack of select queries to be done with their names
     *
     * @param select Select sql statement in JQLPARSER structure
     */
    @Override
    public void visit(Select select) {
        GeneralSelectExecutor executor =  new GeneralSelectExecutor(queryEngine);
        SelectBody selectBody = select.getSelectBody();
        selectBody.accept(executor);
        queryResult = executor.queryResult;
    }

    @Override
    public void visit(Delete delete) {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }

    @Override
    public void visit(Update update) {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }

    @Override
    public void visit(Insert insert) {
        // 1. Fetch the table object
        String tableName = insert.getTable().getName().toLowerCase();
        int tableId = queryEngine.name2TableId.get(tableName);
        Table table = queryEngine.tables.get(tableId);

        // 1.a Validate the schema
        // TODO

        // 2. Create new record
        Record record = table.newRecord();

        ItemsList items = insert.getItemsList();
        QueryItemsExtractor ie = new QueryItemsExtractor();
        items.accept(ie);

        Iterator iter = ie.items.iterator();
        List<Column> columns = insert.getColumns();
        if (null != columns) {
            assert ie.items.size() == columns.size();
            for( Column c: columns) {
                String colName = c.getColumnName().toLowerCase();
                record.setValue(colName, iter.next());
            }
        } else {
            // Get the columns from the table schema
            Set<String> cols = table.getTableSchema().getColumnNames();
            assert ie.items.size() == cols.size();
            for( String col: cols) {
                record.setValue(col, iter.next());
            }
        }
        // 3. Complete
        table.insert(record);
        queryResult = new QueryResult();
        queryResult.result = true;
        queryResult.output = null;
    }

    @Override
    public void visit(Replace replace) {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }

    @Override
    public void visit(Drop drop) {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }

    @Override
    public void visit(Truncate truncate) {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }


    /** Executes SQL create query
     *
     * @param createTable SQL Create query in JSQLParser structure
     */
    @Override
    public void visit(CreateTable createTable) {
        // 1. Check if table name is valid and available
        String tableName = createTable.getTable().getName().toLowerCase();
        assert  createTable.getTable().getSchemaName() == ""; //FIXME We do not provide schema support
        // TODO

        // 2. Modify Metadata global table - add new record
        // TODO

        // 3. Create table, its schema
        TableSchema schema = new TableSchema();
        for (ColumnDefinition def : (List<ColumnDefinition>)createTable.getColumnDefinitions()) {
            ColDataType dataType =  def.getColDataType();
            ColumnType internalDataType;
            if (dataType.getDataType().contentEquals("INTEGER")) {
                internalDataType = ColumnType.INT;
            } else if(dataType.getDataType().contentEquals("DOUBLE")) {
                internalDataType = ColumnType.DOUBLE;
            } else if (dataType.getDataType().contentEquals("CHAR")) {
                internalDataType = ColumnType.CHAR;
            } else {
                throw new InvalidParameterException("Unsupported data type");
            }
            schema.addColumn(def.getColumnName().toLowerCase(), internalDataType, 0);
        }
        Table table = new Table(schema);
        List<Index> indexes = createTable.getIndexes();
        if (null != indexes) {
            for (Index indexDef : indexes) {
                List<String> cols = indexDef.getColumnsNames();
                for (String s : cols) {
                    table.createIndex(s);
                }
            }
        }

        // 4. Complete creation by adding it to the QE collections
        int tableId = queryEngine.freeTableId++;
        queryEngine.name2TableId.put(tableName, tableId);
        queryEngine.tables.put(tableId, table);
        LOG.trace("Table " + tableName +" created with id:"+String.valueOf(tableId)+" using"
        + "SQL query: " + createTable.toString());
        queryResult = new QueryResult();
        queryResult.result = true;
    }

    /* Accessors */

    public QueryResult getQueryResult() {
        return queryResult;
    }
}
