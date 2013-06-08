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

  public void toStringQuery(String procedureName) {
    this.procedureName = procedureName;
  }

  public String getProcedureName() {
    return procedureName;
  }
}

enum SqlAction {
  INSERT,
  SELECT
}