package pw.edu.elka.rso.logic.beans;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;

public class InputManager extends Observable {
  static Logger logger = Logger.getLogger(InputManager.class);
  private Queue<String> input = new LinkedList<String>();

  public void readInput(String procedureName) {

    input.add(procedureName);
    update();

  }

  public void update() {

    logger.debug("Notyfikuje obserwatorow.");

    setChanged();
    notifyObservers();

  }

  public Queue<String> getQueryQueue() {
    return this.input;
  }

}
