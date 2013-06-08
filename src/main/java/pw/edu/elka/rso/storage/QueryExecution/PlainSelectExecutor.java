package pw.edu.elka.rso.storage.QueryExecution;

import java.util.*;

import javafx.util.Pair;
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
    private List<ExpressionWrapper> whereExpressions;

    public PlainSelectExecutor( QueryEngine queryEngine) {
        this.queryEngine = queryEngine;
        whereExpressions = new LinkedList<ExpressionWrapper>();
    }

    Table execute(PlainSelect select) {
        FromItem table1 = select.getFromItem();
        List<Join> joins = select.getJoins();
        Table targetTable = null;
        Map<String,Table> tables = new HashMap<String, Table>();
        List<Pair<String,Table>> tablesList = new LinkedList<Pair<String,Table>>();
        FromExtractor fe = new FromExtractor();
        table1.accept(fe);
        targetTable = queryEngine.getTable(fe.tableName);
        tables.put(fe.tableName, targetTable);
        tables.put(fe.tableAlias, targetTable);
        tablesList.add(new Pair(fe.tableName, targetTable));

        if (!joins.isEmpty()) {
            // table1 is also joined, it's a strange jsqlparser custom to split join clause in this way
            // so perform a join
            String leftTable = fe.tableName;
            for (Iterator it = joins.iterator();it.hasNext();) {
                Join j = (Join) it.next();
                FromItem temp = j.getRightItem();
                temp.accept(fe);
                Table table = queryEngine.getTable(fe.tableName);
                tables.put(fe.tableAlias, table);
                tables.put(fe.tableName, table);
                tablesList.add(new Pair(fe.tableName, table));
                Expression onExpr = j.getOnExpression();
                whereExpressions.add(wrap(leftTable, fe.tableName, onExpr));
                leftTable = fe.tableName;
            }
        }
        // now evaluate where clause and prepare a filter
        Expression whereExpr =  select.getWhere();
        whereExpressions.addAll(split(whereExpr));

        // select required columns
        List<Pair<String,String>> selectItems = extractSelectItems(select.getSelectItems());
        List<SelectItemDescription> outputItems = new LinkedList<SelectItemDescription>();

        // Create output schema
        TableSchema schema = new TableSchema();
        for (Pair<String,String> p : selectItems) {
            String sourceTable = p.getKey();
            if (null == sourceTable) {
                //must check all tables
                //TODO
                // sourceTable = ...
            }
            Table temp = queryEngine.getTable(sourceTable);
            ColumnType type = temp.getTableSchema().getColumnType(p.getValue());
            int length = 0;
            if (type == ColumnType.CHAR) {
                length = (temp.getTableSchema().getTableColumn(p.getValue()).getLength() - 1) / 2;
            }

            SelectItemDescription desc = new SelectItemDescription(sourceTable, p.getValue(), type);
            outputItems.add(desc);
            schema.addColumn(p.getValue(), type ,length);
        }
        Table output = new Table(schema);
        if (tablesList.size() == 1) {
            String n1 = tablesList.get(0).getKey();
            Iterator<Record> it1 = tablesList.get(0).getValue().iterator();
            for (;it1.hasNext();) {
                // Validate where
                //for( ExpressionWrapper e : whereExpressions) {
//                    e.e
                // continue ; // ...
                //}

                // Select items
                Record r = it1.next();
                Record out = output.newRecord();
                for (Pair<String,String> p : selectItems) {
                    Object o = r.getValue(p.getValue());
                    out.setValue(p.getValue(), o);
                }
                output.insert(out);
            }
        } else {
            HashMap<String, Iterator<Record>> records = new HashMap<String, Iterator<Record>>();
            for (Pair<String,Table> p : tablesList) {
                Iterator<Record> it = p.getValue().iterator();
                records.put(p.getKey(), it);
            }
            String n1 = tablesList.get(0).getKey();
            String n2 = tablesList.get(1).getKey();
            Iterator<Record> it1 = tablesList.get(0).getValue().iterator();
            Iterator<Record> it2 = tablesList.get(0).getValue().iterator();
        }
        return output;
    }

    List<ExpressionWrapper> split(Expression expr) {
        ExpressionInterpreter ei = new ExpressionInterpreter();
        expr.accept(ei);
        return ei.expressions;
    }

    ExpressionWrapper wrap(String t1, String t2, Expression expr) {
        return null;
    }

    List<Pair<String,String>> extractSelectItems( List<SelectItem> items ) {
        List<Pair<String,String>> ret = new LinkedList<Pair<String, String>>();
        SelectItemsExtractor extractor = new SelectItemsExtractor();
        for (SelectItem si : items) {
            si.accept(extractor);
            ret.add(new Pair<String, String>(extractor.table, extractor.column));
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