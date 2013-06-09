package pw.edu.elka.rso.storage;

import net.sf.jsqlparser.statement.Statement;

/** This class will describe possible sql queries
 */
public class SqlDescription {
    public long id;
    public Statement statement;

    public SqlDescription() {}
    public SqlDescription(Statement statement) {
        this.statement = statement;
    }
}

enum SqlAction{
    INSERT,
    SELECT
}