package pw.edu.elka.rso.logic.beans;

import pw.edu.elka.rso.storage.QueryResult;
import pw.edu.elka.rso.storage.QueryResultReceiver;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 02.06.13
 */
public class QueryResultReceiverImpl implements QueryResultReceiver {

  private QueryResult qr;
  private Map<Long, LinkedList<ByteBuffer>> queryResult = new HashMap<Long, LinkedList<ByteBuffer>>();

  @Override
  public void complete(QueryResult qr) {

    if (qr.result) {
      Long queryId = qr.queryId;
      LinkedList<ByteBuffer> result = !(queryResult.containsKey(queryId)) ? new LinkedList<ByteBuffer>() : queryResult.get(queryId);
      result.addAll(qr.output);
    }else{
     //TODO: obsluzyc bledne wywolanie procedury
    }
  }

  public Map<Long, LinkedList<ByteBuffer>> getQueryResult() {
    return queryResult;
  }

  public void setQueryResult(Map<Long, LinkedList<ByteBuffer>> queryResult) {
    this.queryResult = queryResult;
  }
}
