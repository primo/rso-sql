package pw.edu.elka.rso.storage.QueryExecution;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import pw.edu.elka.rso.storage.DataRepresentation.Table;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: primo
 * Date: 6/1/13
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlainSelectExecutor {

    private QueryEngine queryEngine = null;

    public PlainSelectExecutor( QueryEngine queryEngine) {
        this.queryEngine = queryEngine;
    }

    Table execute(PlainSelect select) {
        FromItem table1 = select.getFromItem();
        List<Join> joins = select.getJoins();
        Table targetTable = null;
        Map<String,Table> tables = new HashMap<String, Table>();
        FromExtractor fe = new FromExtractor();

        if (!joins.isEmpty()) {
            // table1 is also joined, it's a strange jsqlparser custom to split join clause in this way
            // so perform a join
            table1.accept(fe);
            targetTable = queryEngine.getTable(fe.tableName);
            for (Iterator it = joins.iterator();it.hasNext();) {
                Join j = (Join) it.next();
                FromItem temp = j.getRightItem();
                temp.accept(fe);
                Table table = queryEngine.getTable(fe.tableName);
                tables.put(fe.tableAlias, table);
                tables.put(fe.tableName, table);
                Expression onExpr = j.getOnExpression();

            }
            // TODO
        } else {
            table1.accept(fe);
            targetTable = queryEngine.getTable(fe.tableName);
            tables.put(fe.tableName, targetTable);
            tables.put(fe.tableAlias, targetTable);
        }
        // now evaluate where clause and prepare a filter
        // select required columns
        return null;
    }

    ExpressionWrapper wrap(Expression expr) {
        return null;
    }

}

