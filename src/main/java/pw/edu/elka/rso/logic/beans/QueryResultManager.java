package pw.edu.elka.rso.logic.beans;

import org.apache.log4j.Logger;
import pw.edu.elka.rso.server.ShardDetails;
import pw.edu.elka.rso.storage.DataRepresentation.Table;
import pw.edu.elka.rso.storage.SqlDescription;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 08.06.13
 */
public class QueryResultManager {

  static Logger logger = Logger.getLogger(QueryResultManager.class);
  public Queue<Map<Long, LinkedList<Table>>> readyToPullResults = new LinkedBlockingQueue<>();
  public Map<Long, LinkedList<Table>> queryResults = new HashMap<>();
  private LinkedList<Long> queryIManage = new LinkedList<>();
  /**
   * Mapa ktora mowi ile shardow bierze udzial w aktualnie
   * Akutalizowana jesli pojawi sie rezultat z ktoregos
   */
  private Map<Long, Long> shardCountInQuery = new HashMap<>();

  public void beginQuery(SqlDescription queryDescription, LinkedList<ShardDetails> whichShardExecuteQuery) {
    //dodac czasomierz
    shardCountInQuery.put(queryDescription.id, (long) whichShardExecuteQuery.size());
    queryResults.put(queryDescription.id, new LinkedList<Table>());
    logger.debug("Zaczynam wykonywac zadanie" + queryDescription.toString());
    queryIManage.add(queryDescription.id);
  }

  public void insertResult(Long queryId, Table queryResult) {

//    logger.debug("Dostalem wynik zapytania id[" + queryId + "] : " + queryResult);
    LinkedList<Table> queryResultList = queryResults.containsKey(queryId) ? queryResults.get(queryId) : new LinkedList<Table>();

    queryResultList.add(queryResult);

    //dodjamy do mapy wynikow
    Long currentCount = shardCountInQuery.get(queryId);
    queryResults.put(queryId, queryResultList);

    /**
     * Jelsi dostalismy ostania odpowiedz ktora oczekiwalismy
     * Wyrzucamy z mapy querResults wyniki i przezucamy je do kolejki readyToPullResults.
     * Przed musimy polaczyc wyniki i cos z nimi zrobic, narazie to sobie darujemy
     */
    if (currentCount == 1) {

      //tworzymy pare id zapytania - wynik
      Map<Long, LinkedList<Table>> result = new HashMap<>();
      result.put(queryId, queryResultList);
      readyToPullResults.add(result);

      //usuwamy z mapy akutalnych rezultatow to co wysylamy dalej
      queryResults.remove(queryId);

    } else {
      currentCount--;
      shardCountInQuery.put(queryId, currentCount);
    }


  }

  public Map<Long, LinkedList<Table>> returnResult() {
    if (readyToPullResults.size() > 0) {
      return readyToPullResults.poll();
    } else {
      return null;
    }
  }

  public Map<Long, LinkedList<Table>> checkResult() {
    if (readyToPullResults.size() > 0) {
      return readyToPullResults.peek();
    } else {
      return null;
    }

  }
}
