package pw.edu.elka.rso.storage.QueryExecution;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;

class FromExtractor implements FromItemVisitor {
    String tableName;
    String tableAlias;

    public String getTableName() {
        return this.tableName;
    }

    @Override
    public void visit(Table table) {
        tableName = table.getName().toLowerCase();
        tableAlias = table.getAlias();
        if (null != tableAlias) {
            tableAlias = tableAlias.toLowerCase();
        }
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
