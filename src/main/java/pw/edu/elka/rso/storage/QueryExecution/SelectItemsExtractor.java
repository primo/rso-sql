package pw.edu.elka.rso.storage.QueryExecution;

import java.util.Map.Entry;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;


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
    public boolean allColumns;

    public SelectItemsExtractor() {}

    @Override
    public void visit(AllColumns allColumns) {
        table = "*";
        column = "*";
        this.allColumns = true;
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
        table = allTableColumns.getTable().getName();
        column = "*";
        this.allColumns = true;
    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        QueryItemsExtractor qie = new QueryItemsExtractor();
        selectExpressionItem.getExpression().accept(qie);
        assert qie.items.size() == 1;
        // FIXME {
        if (qie.items.size() != 1) throw new IllegalArgumentException("qie.items.size != 1");
        // } FIXME

        Entry<String,String> item = (Entry<String, String>) qie.items.get(0);
        table = item.getKey();
        column = item.getValue();
    }
}