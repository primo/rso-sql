package pw.edu.elka.rso.storage;

import net.sf.jsqlparser.statement.Statement;

import java.io.Serializable;

/**
 * This class will describe possible sql queries
 */
public class SqlDescription implements Serializable {
    public long id;

    public transient Statement statement;

    private String procedureName;

    public SqlDescription() {}

    public SqlDescription(Statement statement) {
        this.statement = statement;
    }

    public void toStringQuery(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getProcedureName() {
        return procedureName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SqlDescription that = (SqlDescription) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "SqlDescription{" +
                "statement=" + statement +
                ", id=" + id +
                '}';
    }
}

enum SqlAction {
    INSERT,
    SELECT
}
