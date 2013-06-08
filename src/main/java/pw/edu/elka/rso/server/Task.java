package pw.edu.elka.rso.server;

import pw.edu.elka.rso.logic.beans.QueryExecutorImpl;
import pw.edu.elka.rso.storage.SqlDescription;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 05.06.13
 * Time: 01:21
 * To change this template use File | Settings | File Templates.
 */
public abstract class Task<I> {

  private int taskId = 0;

  protected I input;

  public I getInput() {
    return input;
  }

  @Override
  public String toString() {
    return "Task{" +
        "taskId=" + taskId +
        ", input=" + input +
        '}';
  }
}
