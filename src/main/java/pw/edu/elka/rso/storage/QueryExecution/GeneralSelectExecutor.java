package pw.edu.elka.rso.storage.QueryExecution;

import net.sf.jsqlparser.statement.select.*;
import pw.edu.elka.rso.storage.DataRepresentation.Record;
import pw.edu.elka.rso.storage.DataRepresentation.Table;
import pw.edu.elka.rso.storage.QueryResult;

import java.util.Iterator;
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
        try {
            PlainSelectExecutor select = new PlainSelectExecutor(queryEngine);
            Table output = select.execute(plainSelect);
            queryResult = new QueryResult();
            queryResult.result = true;
            Iterator<Record> it = output.iterator();
            for(;it.hasNext();) {
                queryResult.output.add(it.next().getContentDuplicate());
            }
        } catch (Exception e) {
            queryResult = new QueryResult();
            queryResult.result = false;
            queryResult.output = null;
        }
    }

    @Override
    public void visit(Union union) {
        throw new UnsupportedOperationException("UNIONs are not yet supported.");
    }
}


