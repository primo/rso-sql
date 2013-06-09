package pw.edu.elka.rso.logic.beans;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;

public class InputManager extends Observable {
  static Logger logger = Logger.getLogger(InputManager.class);
  private Queue<QueryInfo> input = new LinkedList<>();

  public void readInput(String procedureName, Long queryId, LinkedList<String> parameters) {
    QueryInfo queryInfo = new QueryInfo(procedureName, queryId, parameters);
    input.add(queryInfo);
    update();

  }

  public void update() {

    logger.debug("Notyfikuje obserwatorow.");

    setChanged();
    notifyObservers();

  }

  public Queue<QueryInfo> getQueryQueue() {
    return this.input;
  }
}
