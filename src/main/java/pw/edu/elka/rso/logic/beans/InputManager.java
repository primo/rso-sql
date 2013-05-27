package pw.edu.elka.rso.logic.beans;

import net.sf.jsqlparser.statement.Statement;
import org.apache.log4j.Logger;

import java.io.Reader;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;

public class InputManager extends Observable {
  static Logger logger = Logger.getLogger(InputManager.class);
  private Queue<Statement> queryQueue = new LinkedList<Statement>();
  private QueryReaderImpl queryReader;

  public InputManager() {
    this.queryReader = new QueryReaderImpl();
  }


  public void readQuery(Reader query) {
    if (queryReader.validateQuery(query)) {

      Statement statement = queryReader.getParsedQuery();

      if (statement != null) {
        queryQueue.add(statement);
        logger.debug("Doddaje zapytanie do kolejki");
      }else{
        logger.debug("Przeprsowane zapytanie puste");
        //TODO:rzucic ladnym wyjatkiem
      }

      update();
    }
  }


  public void update() {

    logger.debug("Notyfikuje obserwatorow.");

    setChanged();
    notifyObservers();

  }

  public Queue<Statement> getQueryQueue() {
    return this.queryQueue;
  }


  public void setQueryQueue(Queue<Statement> queryQueue) {
    this.queryQueue = queryQueue;
  }


  public QueryReaderImpl getQueryReader() {
    return this.queryReader;
  }


  public void setQueryReader(QueryReaderImpl queryReader) {
    this.queryReader = queryReader;
  }

}
