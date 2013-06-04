package pw.edu.elka.rso.storage.QueryExecution;

import javafx.util.Pair;
import java.util.HashMap;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

import java.util.LinkedList;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: primo
* Date: 6/1/13
* Time: 6:37 PM
* To change this template use File | Settings | File Templates.
*/
class SelectItemsExtractor implements SelectItemVisitor {


    public String table;
    public String column;

    public SelectItemsExtractor() {}

    @Override
    public void visit(AllColumns allColumns) {
        table = "*";
        column = "*";
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
        table = allTableColumns.getTable().getName();
        column = "*";
    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        QueryItemsExtractor qie = new QueryItemsExtractor();
        selectExpressionItem.getExpression().accept(qie);
        assert qie.items.size() == 1;
        // FIXME {
        if (qie.items.size() != 1) throw new IllegalArgumentException("qie.items.size != 1");
        // } FIXME

        Pair<String,String> item = (Pair<String, String>) qie.items.get(0);
        table = item.getKey();
        column = item.getValue();
    }
}