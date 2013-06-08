package pw.edu.elka.rso.server;

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
    return input.toString();    //To change body of overridden methods use File | Settings | File Templates.
  }
}
