package pw.edu.elka.rso.storage.QueryExecution;

import java.util.*;

import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.log4j.Logger;
import pw.edu.elka.rso.storage.DataRepresentation.ColumnType;
import pw.edu.elka.rso.storage.DataRepresentation.Record;
import pw.edu.elka.rso.storage.DataRepresentation.Table;
import pw.edu.elka.rso.storage.DataRepresentation.TableSchema;

/**
 */
public class PlainSelectExecutor {

    public static final Logger LOG = Logger.getLogger(PlainSelectExecutor.class);

    private QueryEngine queryEngine;
    private Expression whereExpressions;
    HashMap<String, Table> tables = new HashMap<String, Table>();
    HashMap<String, String> aliases = new HashMap<String, String>();
    HashMap<String, String> columnMapping = new HashMap<String, String>();
    HashMap<String, Record> currentLines = new HashMap<String, Record>();
    private ExpressionInterpreter ei = new ExpressionInterpreter(currentLines, tables, aliases, columnMapping);

    public PlainSelectExecutor(QueryEngine queryEngine) {
        this.queryEngine = queryEngine;
    }

    Table execute(PlainSelect select) throws CloneNotSupportedException {
        Table targetTable = null;
        final String lTable;
        boolean noJoin = false;
        HashMap<String, Table> tablesList = new HashMap<String, Table>();
        FromExtractor fe = new FromExtractor();
        List<JoinDescription> joinDescriptions = null;

        // Fetch parsed objects
        FromItem table1 = select.getFromItem();
        List<Join> joins = select.getJoins();
        Expression joinCond = null;

        // Get the main table name - JSQL puts it in from item
        table1.accept(fe);
        lTable = fe.tableName;
        targetTable = queryEngine.getTable(lTable);
        tables.put(fe.tableName, targetTable);
        if (null != fe.tableAlias) {
            aliases.put(fe.tableAlias, fe.tableName);
        }
        tablesList.put(fe.tableName, targetTable);

        // Manage joins

        if (null != joins && !joins.isEmpty()) {
            if (1 != joins.size()) {
                throw new IllegalArgumentException("Only simple joins allowed.");
            }
            Join join = joins.get(0);
            join.getRightItem().accept(fe);
            String rName = fe.tableName;
            String rAlias = fe.tableAlias;
            Table rTable = queryEngine.getTable(rName);
            tables.put(rName, rTable);
            tablesList.put(fe.tableName, targetTable);
            if (null != rAlias) {
                aliases.put(rAlias, rName);
            }
            if (null == join.getOnExpression()) {
                throw new IllegalArgumentException("Join condition must be in FROM clause.");
            }
            joinCond = join.getOnExpression();
            JoinDescription jdesc = new JoinDescription(lTable, rName, "v1", joinCond, false);
            joinDescriptions = new ArrayList<JoinDescription>();
            joinDescriptions.add(jdesc);
        } else {
            noJoin = true;
        }

        // Fetch WHERE clause
        Map<String, List<SelectRange>> selectRanges = null;
        whereExpressions = select.getWhere();
        if (null != whereExpressions) {
            ColumnRangeExtractor cre = new ColumnRangeExtractor();
            whereExpressions.accept(cre);
            if (!cre.isFullScan()) {
                selectRanges = cre.getRanges();
            }
        }
        // Fetch all tables used in the query, all columns existing in FROM and WHERE clauses
        Entry<List<SimpleEntry<String, String>>, List<SimpleEntry<String, String>>> tabColInfo
                = fetchTabColNames(targetTable, fe.tableName, joins, whereExpressions);
        final List<SimpleEntry<String, String>> allTables = tabColInfo.getKey();
        final List<SimpleEntry<String, String>> allCols = tabColInfo.getValue();
        for (Entry<String, String> e : allTables) {
            aliases.put(e.getValue(), e.getKey());
            targetTable = queryEngine.getTable(fe.tableName);
            tables.put(fe.tableName, targetTable);
        }

        // Select required columns
        SimpleEntry<List<SimpleEntry<String, String>>, Boolean> selectItemsInfo = extractSelectItems(select.getSelectItems());
        List<SimpleEntry<String, String>> selectItems = selectItemsInfo.getKey();
        final boolean allColumnsTarget = selectItemsInfo.getValue();
        selectItems = solveConflicts(selectItems, allColumnsTarget, tablesList);

        Table output = null;
        if (noJoin) {
            // No join
            Entry<String, Table> e = tables.entrySet().iterator().next();
            String tableName = e.getKey();
            Table table = e.getValue();
            // Create output schema
            TableSchema schema = new TableSchema();
            // Redundant now - solveConflicts aids in this task
            if (allColumnsTarget) {
                Iterator<SimpleEntry<String, String>> it = selectItems.iterator();
                while (it.hasNext()) {
                    final SimpleEntry<String, String> next = it.next();
                    if (next.getValue().equals("*")) {
                        // Add all columns to the set
                        // FIXME what if the single table is a result of join
                        if (!next.getKey().equals("*") && !next.getKey().equals(tableName)) {
                            // The table name must be equal to the current
                            throw new IllegalArgumentException("Table " + next.getKey() + " given in SELECT " +
                                    "is different that one specified in FROM clause: " + tableName);
                        }
                        it.remove();
                        final Set<String> columnNames = table.getTableSchema().getColumnNames();
                        for (String columnName : columnNames) {
                            selectItems.add(new SimpleEntry<String, String>(null, columnName));
                        }
                        // We are assuming that no one will ever give two * in a select to one table
                        // FIXME, making a collection of iterators to remove and columns to add is a bit unnecessary work
                        break;
                    }
                }
            }
            for (Entry<String, String> p : selectItems) {
                final String sourceTable = p.getKey();
                if (sourceTable != null && !sourceTable.equals(tableName)) {
                    throw new IllegalArgumentException("Table prefix: " + sourceTable + " is different from" +
                            " table given in FROM clause.");

                }
                // Assuming that if we join tables then we prefix columns with its names
                final String columnName = p.getValue();
                ColumnType type = table.getTableSchema().getColumnType(columnName);
                int length = 0;
                if (type == ColumnType.CHAR) {
                    length = (table.getTableSchema().getTableColumn(columnName).getLength() - 1) / 2;
                }
                schema.addColumn(p.getValue(), type, length);
            }
            output = new Table(schema);
            // Manage index
            SelectRange sr = null;
            if (null != selectRanges && null != selectRanges.get(tableName)) {
                if (0 != selectRanges.get(tableName).size()) {
                    if (table.hasIndex(selectRanges.get(tableName).get(0).column)) {
                        sr = selectRanges.get(tableName).get(0);
                    }
                }
            }
            Iterator<Record> it1 = getRecordIterator(table, sr);
            for (; it1.hasNext(); ) {
                Record r = it1.next();
                // TODO setup ei
                currentLines.put(ExpressionInterpreter.DEFAULT_TABLE, r);
                currentLines.put(tableName, r);
                // Check WHERE condition if given
                if (null != whereExpressions) {
                    whereExpressions.accept(ei);
                    if (!ei.isTrue())
                        continue;
                }

                // Select items
                Record out = output.newRecord();
                for (Entry<String, String> p : selectItems) {
                    Object o = r.getValue(p.getValue());
                    out.setValue(p.getValue(), o);
                }
                output.insert(out);
            }
        } else {
            // Assuming only single join
            if (joinDescriptions.size() > 1) {
                throw new IllegalArgumentException("Multiple tables not yet supported.");
            }
            TableSchema schema = new TableSchema();
            for (Entry<String, String> p : selectItems) {
                final String sourceTable = p.getKey();
                // Assuming that if we join tables then we prefix columns with its names
                final String columnName = p.getValue();
                ColumnType type = tables.get(sourceTable).getTableSchema().getColumnType(columnName);
                int length = 0;
                if (type == ColumnType.CHAR) {
                    length = (tables.get(sourceTable).getTableSchema().getTableColumn(columnName).getLength() - 1) / 2;
                }
                schema.addColumn(p.getKey() + "." + p.getValue(), type, length);
            }
            output = new Table(schema);
            JoinDescription jd = joinDescriptions.get(0);
            Table temp = doInnerJoin(tables.get(jd.table1), jd.table1, tables.get(jd.table2), jd.table2, jd.joinCondition, whereExpressions, selectRanges);
            Iterator<Record> it = temp.iterator();
            Record out = output.newRecord();
            while (it.hasNext()) {
                Record in = it.next();
                for (Entry<String, String> p : selectItems) {
                    final String name = p.getKey() + "." + p.getValue();
                    Object o = in.getValue(name);
                    out.setValue(name, o);
                }
                output.insert(out);
            }
        }
        return output;
    }

    // Choose the correct iterator
    private Iterator<Record> getRecordIterator(Table table, SelectRange sr) {
        Iterator<Record> it1;
        if (sr == null) {
            return table.iterator();
        }
        // Cast to integer if necessary
        if (table.getTableSchema().getColumnType(sr.column)==ColumnType.INT) {
            if (sr.from != null && sr.from instanceof Long) {
                long temp = (long)sr.from;
                sr.from = Integer.valueOf((int)temp);
            } else if (sr.to != null && sr.to instanceof Long) {
                long temp = (long)sr.to;
                sr.to = Integer.valueOf((int)temp);
            }
        }
        if (sr.from == null) {
            it1 = table.indexedIteratorTo(sr.column, sr.to, sr.toInclusive);
        } else if (sr.to == null) {
            it1 = table.indexedIteratorFrom(sr.column, sr.from, sr.fromInclusive);
        } else {
            if (sr.to == null && sr.from == null) {
                LOG.error("Invalid index.");
            }
            it1 = table.indexedIterator(sr.column, sr.from, sr.fromInclusive, sr.to, sr.toInclusive);
        }
        return it1;
    }

    /**
     * @param firstTable
     * @param firstTableName
     * @param joins
     * @return
     */
    private Entry<List<SimpleEntry<String, String>>, List<SimpleEntry<String, String>>> fetchTabColNames(
            Table firstTable,
            String firstTableName,
            List<Join> joins,
            Expression whereExpressions
    ) {
        List<SimpleEntry<String, String>> retTables = new ArrayList<SimpleEntry<String, String>>();
        List<SimpleEntry<String, String>> retColumns = new ArrayList<SimpleEntry<String, String>>();
        ColumnExtractor ce = new ColumnExtractor();
        if (null != joins) {
            FromExtractor fe = new FromExtractor();
            Table lTab = firstTable;
            String lTabN = firstTableName;
            for (Join j : joins) {
                j.getRightItem().accept(fe);
                retTables.add(new SimpleEntry<String, String>(fe.tableName, fe.tableName));
                Expression joinCondition = j.getOnExpression();
                joinCondition.accept(ce);
                retColumns.addAll(ce.columns);
            }
        }
        if (null != whereExpressions) {
            whereExpressions.accept(ce);
            retColumns.addAll(ce.columns);
        }
        return new SimpleEntry<List<SimpleEntry<String, String>>, List<SimpleEntry<String, String>>>(retTables, retColumns);
    }

    /**
     * Solve possible conflicts between column names in queries.
     *
     * @param columns
     * @param allColumnsTarget
     * @param tablesList
     * @return
     */
    private List<SimpleEntry<String, String>> solveConflicts(
            List<SimpleEntry<String, String>> columns,
            boolean allColumnsTarget,
            HashMap<String, Table> tablesList
    ) {
        List<SimpleEntry<String, String>> result = new ArrayList<SimpleEntry<String, String>>();
        HashMap<String, Set<String>> allnames = new HashMap<String, Set<String>>();
        // Extract all column names from every table
        for (Entry<String, Table> e : tablesList.entrySet()) {
            allnames.put(e.getKey(), e.getValue().getTableSchema().getColumnNames());
        }
        for (Entry<String, String> e : columns) {
            final String tableName = e.getKey();
            final String colName = e.getValue();
            // Resolve * or table.*
            if (allColumnsTarget && colName.equals("*")) {
                if (null == tableName || tableName.equals("*")) {
                    // Selecting all tables
                    for (Entry<String, Set<String>> e2 : allnames.entrySet()) {
                        for (String e3 : e2.getValue()) {
                            result.add(new SimpleEntry<String, String>(e2.getKey(), e3));
                        }
                    }
                } else {
                    // Select only all columns from 'tableName'
                    Set<String> cols = allnames.get(tableName);
                    for (String e3 : cols) {
                        result.add(new SimpleEntry<String, String>(tableName, e3));
                    }
                }
            }
            if (null == tableName) {
                String temp = null;
                for (Entry<String, Set<String>> e2 : allnames.entrySet()) {
                    if (e2.getValue().contains(colName)) {
                        if (temp != null) {
                            throw new IllegalArgumentException("Specified a column that exists in two tables: " + colName);
                        }
                        temp = e2.getKey();
                    }
                }
                result.add(new SimpleEntry<String, String>(temp, colName));
            }
            // FIXME Assuming that no one will mix alias and real table name when specifying a column
        }
        return result;
    }

    private Table doInnerJoin(
            Table base,
            String baseName,
            Table subject,
            String subjectName,
            Expression onExpr,
            Expression whereExpressions,
            Map<String, List<SelectRange>> ranges
    ) throws CloneNotSupportedException {
        Set<String> baseColumns = base.getTableSchema().getColumnNames();
        Set<String> subjectColumns = subject.getTableSchema().getColumnNames();
        TableSchema schema = null;
        if (baseName.contains("#")) {
            schema = base.getTableSchema().copy();
        } else {
            schema = base.getTableSchema().copy(baseName);
        }
        schema.appendSchema(subject.getTableSchema(), subjectName);
        Table output = new Table(schema);
        Record out = output.newRecord();

        // Resolve iterators
        SelectRange sr = null;
        if (null != ranges && null != ranges.get(baseName)) {
            if (0 != ranges.get(baseName).size()) {
                if (base.hasIndex(ranges.get(baseName).get(0).column)) {
                    sr = ranges.get(baseName).get(0);
                }
            }
        }
        Iterator<Record> baseIt = getRecordIterator(base, sr);
        // For the subject table
        sr = null;
        if (null != ranges && null != ranges.get(subjectName)) {
            if (0 != ranges.get(subjectName).size()) {
                if (subject.hasIndex(ranges.get(subjectName).get(0).column)) {
                    sr = ranges.get(subjectName).get(0);
                }
            }
        }

        while (baseIt.hasNext()) {
            Record rb = baseIt.next();
            currentLines.put(baseName, rb);
            Iterator<Record> subjectIt = getRecordIterator(subject, sr);
            while (subjectIt.hasNext()) {
                Record rs = subjectIt.next();
                currentLines.put(subjectName, rs);
                onExpr.accept(ei);
                boolean result = ei.isTrue();
                if (null != whereExpressions) {
                    whereExpressions.accept(ei);
                    result = result && ei.isTrue();
                }
                if (result) {
                    // Join the rows
                    for (String s : baseColumns) {
                        out.setValue(baseName + "." + s, rb.getValue(s));
                    }
                    for (String s : subjectColumns) {
                        out.setValue(subjectName + "." + s, rs.getValue(s));
                    }
                    output.insert(out);
                }
            }
        }
        return output;
    }

    /**
     * Extracts target columns from a Select statement
     *
     * @param items
     * @return
     */
    SimpleEntry<List<SimpleEntry<String, String>>, Boolean>
    extractSelectItems(List<SelectItem> items) {
        List<SimpleEntry<String, String>> ret = new LinkedList<SimpleEntry<String, String>>();
        SelectItemsExtractor extractor = new SelectItemsExtractor();
        boolean allColumns = false;
        for (SelectItem si : items) {
            si.accept(extractor);
            if (null != extractor.table) {
                extractor.table = extractor.table.toLowerCase();
            }
            allColumns |= extractor.allColumns;
            SimpleEntry temp = new SimpleEntry<String, String>(extractor.table, extractor.column.toLowerCase());
            ret.add(temp);
        }
        return new SimpleEntry<List<SimpleEntry<String, String>>, Boolean>(ret, allColumns);
    }
}

class JoinDescription {
    public final String table1;
    public final String table2;
    public final String result;
    public final Expression joinCondition;
    public final boolean indexed;
    public final List<SelectRange> selectRanges;

    JoinDescription(String table1, String table2, String result, Expression joinCondition, boolean indexed) {
        this.table1 = table1;
        this.table2 = table2;
        this.result = result;
        this.joinCondition = joinCondition;
        this.indexed = indexed;
        if (indexed) {
            selectRanges = new ArrayList<SelectRange>();
        } else {
            selectRanges = null;
        }
    }
}

class SelectRange {
    public String table;
    public String column;
    public Object from;
    public boolean fromInclusive;
    public Object to;
    public boolean toInclusive;
    public Operation op;
}