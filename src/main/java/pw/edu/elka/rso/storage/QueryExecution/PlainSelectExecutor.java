package pw.edu.elka.rso.storage.QueryExecution;

import java.util.*;

import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import pw.edu.elka.rso.storage.DataRepresentation.ColumnType;
import pw.edu.elka.rso.storage.DataRepresentation.Record;
import pw.edu.elka.rso.storage.DataRepresentation.Table;
import pw.edu.elka.rso.storage.DataRepresentation.TableSchema;

/**
 */
public class PlainSelectExecutor {

    private QueryEngine queryEngine;
    private Expression whereExpressions;
    HashMap<String,Table> tables = new HashMap<String, Table>();
    HashMap<String, String>  aliases = new HashMap<String, String>();
    HashMap<String, String> columnMapping  = new HashMap<String, String>();
    HashMap<String, Record> currentLines = new HashMap<String, Record>();
    private ExpressionInterpreter ei = new ExpressionInterpreter(currentLines, tables, aliases,  columnMapping);

    public PlainSelectExecutor( QueryEngine queryEngine) {
        this.queryEngine = queryEngine;
    }

    Table execute(PlainSelect select) {
        Table targetTable = null;

        // Parsed objects
        FromItem table1 = select.getFromItem();
        List<Join> joins = select.getJoins();

        List<SimpleEntry<String,Table>> tablesList = new LinkedList<SimpleEntry<String,Table>>();
        FromExtractor fe = new FromExtractor();
        table1.accept(fe);
        targetTable = queryEngine.getTable(fe.tableName);
        tables.put(fe.tableName, targetTable);
        aliases.put(fe.tableAlias, fe.tableName);
        tablesList.add(new SimpleEntry<String, Table>(fe.tableName, targetTable));

        if (null != joins && !joins.isEmpty()) {
            // table1 is also joined, it's a strange jsqlparser custom to split join clause in this way
            // so perform a join
            String leftTable = fe.tableName;
            String resultTable = null;
            for (Iterator it = joins.iterator();it.hasNext();) {
                // Extract the joined table object
                Join j = (Join) it.next();
                FromItem temp = j.getRightItem();
                temp.accept(fe);
                Table table = queryEngine.getTable(fe.tableName);
                // Get the join condition
                Expression onExpr = j.getOnExpression();
                if (null != onExpr) {
                    // Fetch the previous table
                    Table base = tables.get(leftTable);
                    Table result = doInnerJoin(base, leftTable, table, fe.tableName, onExpr);
                    resultTable = leftTable +"#"+fe.tableName;
                    tables.put(resultTable, table);
                    tables.remove(leftTable);
                } else {
                    aliases.put(fe.tableAlias, fe.tableName);
                    tables.put(fe.tableName, table);
                    resultTable = fe.tableName;
                }
                leftTable = resultTable;
            }
        }
        // now evaluate where clause and prepare a filter
        whereExpressions = select.getWhere();

        // select required columns
        List<SimpleEntry<String,String>> selectItems = extractSelectItems(select.getSelectItems());
        List<SelectItemDescription> outputItems = new LinkedList<SelectItemDescription>();

        Table output = null;
        if ( 1 == tables.size()) {
            // No join
            Entry<String, Table> e = tables.entrySet().iterator().next();
            String tableName = e.getKey();
            Table table = e.getValue();
            // Create output schema
            TableSchema schema = new TableSchema();
            for (Entry<String,String> p : selectItems) {
                String sourceTable = p.getKey();
                // Assuming that if we join tables then we prefix columns with its names
                String columnName = sourceTable!=null?sourceTable+"."+p.getValue():p.getValue();
                ColumnType type = table.getTableSchema().getColumnType(columnName);
                int length = 0;
                if (type == ColumnType.CHAR) {
                    length = (table.getTableSchema().getTableColumn(columnName).getLength() - 1) / 2;
                }

                SelectItemDescription desc = new SelectItemDescription(sourceTable, p.getValue(), type);
                outputItems.add(desc);
                schema.addColumn(p.getValue(), type ,length);
            }
            output = new Table(schema);
            Iterator<Record> it1 = table.iterator();
            for (;it1.hasNext();) {
                Record r = it1.next();
                // TODO setup ei
                currentLines.put(tableName, r);
                // Check WHERE condition if given
                if (null != whereExpressions ) {
                    whereExpressions.accept(ei);
                    if (!ei.isTrue())
                        continue;
                }

                // Select items
                Record out = output.newRecord();
                for (Entry<String,String> p : selectItems) {
                    Object o = r.getValue(p.getValue());
                    out.setValue(p.getValue(), o);
                }
                output.insert(out);
            }
        } else {
            throw new IllegalArgumentException("Multiple tables not yet supported.");
        }

        return output;
    }

    private Table doInnerJoin(Table base, String baseName, Table subject, String subjectName, Expression onExpr) {
        // FIXME do not create long names -- check it
        Set<String> baseColumns = base.getTableSchema().getColumnNames();
        Set<String> subjectColumns = subject.getTableSchema().getColumnNames();
        TableSchema schema = null;
        if (baseName.contains("#")) {
            schema = base.getTableSchema().copy();
        } else {
            schema = base.getTableSchema().copy(baseName);
        }
        schema.appendSchema(subject.getTableSchema(),subjectName);
        Table output = new Table(schema);
        Record out =  output.newRecord();

        Iterator<Record> baseIt = base.iterator();
        Iterator<Record> subjectIt = subject.iterator();
        while (baseIt.hasNext()) {
            Record rb = baseIt.next();
            currentLines.put(baseName, rb);
            while (subjectIt.hasNext()) {
                Record rs = subjectIt.next();
                currentLines.put(subjectName, rs);
                onExpr.accept(ei);
                if (ei.isTrue()) {
                    // Join the rows
                    for (String s : baseColumns) {
                        out.setValue(baseName+"."+s, rb.getValue(s));
                    }
                    for (String s : subjectColumns) {
                        out.setValue(subjectName+"."+s, rs.getValue(s));
                    }
                    output.insert(out);
                }
            }
        }
        return output;
    }

    List<SimpleEntry<String,String>> extractSelectItems( List<SelectItem> items ) {
        List<SimpleEntry<String,String>> ret = new LinkedList<SimpleEntry<String, String>>();
        SelectItemsExtractor extractor = new SelectItemsExtractor();
        for (SelectItem si : items) {
            si.accept(extractor);
            ret.add(new SimpleEntry<String, String>(extractor.table, extractor.column));
        }
        return ret;
    }
}


class SelectItemDescription {
    final public String table;
    final public String column;
    final public ColumnType type;

    SelectItemDescription(String table, String column, ColumnType type) {
        this.table = table;
        this.column = column;
        this.type = type;
    }
}