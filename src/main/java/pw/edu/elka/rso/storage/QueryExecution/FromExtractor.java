package pw.edu.elka.rso.storage.QueryExecution;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * Created with IntelliJ IDEA.
 * User: primo
 * Date: 6/3/13
 * Time: 6:22 PM
 * To change this template use File | Settings | File Templates.
 */
class FromExtractor implements FromItemVisitor {
    String tableName;
    String tableAlias;

    public String getTableName() {
        return this.tableName;
    }

    @Override
    public void visit(Table table) {
        tableName = table.getName();
        tableAlias = table.getAlias();
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
