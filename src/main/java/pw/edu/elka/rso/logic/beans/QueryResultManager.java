package pw.edu.elka.rso.logic.beans;

import org.apache.log4j.Logger;
import pw.edu.elka.rso.server.ShardDetails;
import pw.edu.elka.rso.storage.QueryResult;
import pw.edu.elka.rso.storage.SqlDescription;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 08.06.13
 */
public class QueryResultManager {

  static Logger logger = Logger.getLogger(QueryResultManager.class);

  private LinkedList<Long> queryIManage = new LinkedList<>();
  //  public Queue<Map<Long, LinkedList<Table>>> readyToPullResults = new LinkedBlockingQueue<>();
  private Queue<Map<Long, LinkedList<String>>> readyToPullResults = new LinkedBlockingQueue<>();

  private Map<Long, LinkedList<String>> queryResults = new HashMap<>();
//  public Map<Long, LinkedList<Table>> queryResults = new HashMap<>();

  /**
   * Mapa ktora mowi ile shardow bierze udzial w aktualnie
   * Akutalizowana jesli pojawi sie rezultat z ktoregos
   */
  private Map<Long, Long> shardCountInQuery = new HashMap<>();


  public void beginQuery(SqlDescription queryDescription, LinkedList<ShardDetails> whichShardExecuteQuery) {
    //dodac czasomierz
    shardCountInQuery.put(queryDescription.id, (long) whichShardExecuteQuery.size());
    queryResults.put(queryDescription.id, new LinkedList<String>());
    logger.debug("Zaczynam wykonywac zadanie" + queryDescription.toString());
    queryIManage.add(queryDescription.id);
  }

  public void insertResult(Long queryId, String queryResult) {

    logger.debug("Dostalem wynik zapytania id[" + queryId + "] : " + queryResult);
    LinkedList<String> queryResultList = queryResults.containsKey(queryId) ? queryResults.get(queryId) : new LinkedList<String>();
//    List<Table> currentWorkingQuery = queryResults.containsKey(sqlDescription.id) ? queryResults.get(sqlDescription.id) : new LinkedList<Table>();

    queryResultList.add(queryResult);
    //queryResultList.add(queryResult.output);

    //dodjamy do mapy wynikow
    Long currentCount = shardCountInQuery.get(queryId);
    queryResults.put(queryId, queryResultList);

    /**
     * Jelsi dostalismy ostania odpowiedz ktora oczekiwalismy
     * Wyrzucamy z mapy querResults wyniki i przezucamy je do kolejki readyToPullResults.
     * Przed musimy polaczyc wyniki i cos z nimi zrobic, narazie to sobie darujemy
     */
    if (currentCount == 1) {
//      queryResultList.add(queryResult);
//        queryResultList.add(queryResult.output);

      //tworzymy pare id zapytania - wynik
      Map<Long, LinkedList<String>> result = new HashMap<>();
      result.put(queryId, queryResultList);
      readyToPullResults.add(result);

      //usuwamy z mapy akutalnych rezultatow to co wysylamy dalej
      queryResults.remove(queryId);

    } else {
      currentCount--;
      shardCountInQuery.put(queryId, currentCount);
    }


  }

  public Map<Long, LinkedList<String>> returnResult() {
    if (readyToPullResults.size() > 0) {
//      logger.debug("Wynik ostateczny" + readyToPullResults.peek().get(0).get(0));
      return readyToPullResults.poll();
    } else {
      return null;
    }
  }

  public Map<Long, LinkedList<String>> checkResult() {
    if (readyToPullResults.size() > 0) {
//      logger.debug("Wynik ostateczny" + readyToPullResults.peek().get(0).get(0));
      return readyToPullResults.peek();
    } else {
      return null;
    }

  }
}
