package pw.edu.elka.rso.storage.QueryExecution;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import pw.edu.elka.rso.storage.QueryResult;

import java.util.List;

/**
 */
public class SelectExecutor implements SelectVisitor {
    String fromTableName;
    List<String> columns;

    public SelectExecutor() {}

    @Override
    public void visit(PlainSelect plainSelect) {
        // TODO fullblown select parsing
        // Simple one table for now: one from item, no where clauses etc
        FromItem from = plainSelect.getFromItem();
        FromExtractor fromEx = new FromExtractor();
        from.accept(fromEx);
        fromTableName = fromEx.fromTableName;
        List<SelectItem> siList = plainSelect.getSelectItems();
        for (SelectItem si: siList) {
            SelectColumnsExtractor sce = new SelectColumnsExtractor(columns);
            si.accept(sce);
        }
    }

    @Override
    public void visit(Union union) {
        throw new UnsupportedOperationException("UNIONs are not yet supported.");
    }
}


class FromExtractor implements FromItemVisitor {
    String fromTableName = null;

    @Override
    public void visit(Table table) {
        fromTableName = table.getName();
    }

    @Override
    public void visit(SubSelect subSelect) {
        throw new UnsupportedOperationException("SubSelects are not yet supported.");
    }

    @Override
    public void visit(SubJoin subJoin) {
        throw new UnsupportedOperationException("SubJoins are not yet supported.");
    }
}

class SelectColumnsExtractor implements SelectItemVisitor{

    List<String> columns ;

    public SelectColumnsExtractor(List<String> columns) {
        this.columns = columns;
    }

    @Override
    public void visit(AllColumns allColumns) {
        columns.add("*");
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        QueryItemsExtractor qie = new QueryItemsExtractor();
        selectExpressionItem.getExpression().accept(qie);
        // TODO add check if the columns name is really a string and not fe a number
        for (Object o : qie.items) {
            assert o instanceof String;
            columns.add((String)o);
        }
    }
}