package pw.edu.elka.rso.logic.beans;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 09.06.13
 */
public class QueryInfo implements Serializable {
  private String procedureName;
  private Long queryId;
  private LinkedList<String> parameters = new LinkedList<>();

  QueryInfo(String procedureName, Long queryId, LinkedList<String> parameters) {
    this.procedureName = procedureName;
    this.queryId = queryId;
    this.parameters = parameters;
  }

  QueryInfo(String procedureName, Long queryId) {
    this.procedureName = procedureName;
    this.queryId = queryId;
  }

  public String getProcedureName() {
    return procedureName;
  }

  public Long getQueryId() {
    return queryId;
  }

  public LinkedList<String> getParameters() {
    return parameters;
  }

  public void setParameters(LinkedList<String> parameters) {
    this.parameters = parameters;
  }

}
