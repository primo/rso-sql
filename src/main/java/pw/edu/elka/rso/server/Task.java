package pw.edu.elka.rso.server;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 05.06.13
 */

public abstract class Task<I> implements Serializable {

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
