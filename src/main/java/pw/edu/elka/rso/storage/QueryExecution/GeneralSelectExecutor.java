package pw.edu.elka.rso.storage.QueryExecution;

import net.sf.jsqlparser.statement.select.*;
import pw.edu.elka.rso.storage.DataRepresentation.Table;
import pw.edu.elka.rso.storage.QueryResult;

import java.util.List;

/**
 */
public class GeneralSelectExecutor implements SelectVisitor {

    QueryEngine queryEngine;
    protected QueryResult queryResult;

    public GeneralSelectExecutor(QueryEngine queryEngine) {
        this.queryEngine = queryEngine;
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        PlainSelectExecutor select = new PlainSelectExecutor(queryEngine);
        Table output = select.execute(plainSelect);
    }

    @Override
    public void visit(Union union) {
        throw new UnsupportedOperationException("UNIONs are not yet supported.");
    }
}


