package pw.edu.elka.rso.logic.beans;

import pw.edu.elka.rso.storage.DataRepresentation.Table;
import pw.edu.elka.rso.storage.QueryResult;
import pw.edu.elka.rso.storage.QueryResultReceiver;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 02.06.13
 */
public class QueryResultReceiverImpl implements QueryResultReceiver {

  private QueryResult qr;
  private LinkedBlockingQueue<QueryResult> results = new LinkedBlockingQueue<>();

  @Override
  public void complete(QueryResult qr, Object queryContext) {

//    if (qr.result) {
      results.add(qr);
//    } else {
      //TODO: obsluzyc bledne wywolanie procedury
//    }
  }

  public LinkedBlockingQueue<QueryResult> getTestResult() {
    return results;
  }

}
