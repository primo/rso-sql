package pw.edu.elka.rso.server.tasks;

import pw.edu.elka.rso.server.ShardDetails;
import pw.edu.elka.rso.server.Task;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 06.06.13
 */
public class SetConnectionTask extends Task<ShardDetails> {

  public SetConnectionTask(ShardDetails input) {
    this.input = input;
  }

  @Override
  public String toString() {
    return input.toString();
  }
}
