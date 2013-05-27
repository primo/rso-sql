package pw.edu.elka.rso.storage.QueryExecution;

import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import pw.edu.elka.rso.storage.QueryResult;

/** Vists a JSQL Statement and describes its content in QueryEngine understandable
 * form.
 */
class RootQueryVisitor implements StatementVisitor {

    private QueryResult queryResult;
    private QueryEngine queryEngine;

    /** C-tor.
     *
     * A reference to QueryEngine is passed so the Visitor can
     * execute requests directly on data.
     *
     * @param queryEngine object representing data shard
     */
    public RootQueryVisitor(QueryEngine queryEngine) {
        this.queryEngine = queryEngine;
    }

    @Override
    public void visit(Select select) {
        // TODO
    }

    @Override
    public void visit(Delete delete) {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }

    @Override
    public void visit(Update update) {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }

    @Override
    public void visit(Insert insert) {
        // TODO
    }

    @Override
    public void visit(Replace replace) {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }

    @Override
    public void visit(Drop drop) {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }

    @Override
    public void visit(Truncate truncate) {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }


    /** Executes SQL create query
     *
     * @param createTable SQL Create query in JSQLParser structure
     */
    @Override
    public void visit(CreateTable createTable) {
        //TODO

    }

    /* Accessors */

    public QueryResult getQueryResult() {
        return queryResult;
    }
}
