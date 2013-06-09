package pw.edu.elka.rso.logic.beans;

import pw.edu.elka.rso.storage.QueryResult;
import pw.edu.elka.rso.storage.QueryResultReceiver;

import java.util.AbstractMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 02.06.13
 */
public class QueryResultReceiverImpl implements QueryResultReceiver {


//  private LinkedBlockingQueue<QueryResult> results = new LinkedBlockingQueue<>();
  private LinkedBlockingQueue<AbstractMap.SimpleEntry<QueryResult, Object>> results = new LinkedBlockingQueue<>();

  @Override
  public void complete(QueryResult qr, Object queryContext) {

    if (qr.result) {
      AbstractMap.SimpleEntry<QueryResult, Object> result = new AbstractMap.SimpleEntry<>(qr,queryContext);
      results.add(result);
    } else {
      //TODO: obsluzyc bledne wywolanie procedury
    }
  }

  public LinkedBlockingQueue<AbstractMap.SimpleEntry<QueryResult,Object>> getTestResult() {
    return results;
  }

}
