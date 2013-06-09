package pw.edu.elka.rso.logic.beans;

import org.apache.log4j.Logger;
import pw.edu.elka.rso.logic.interfaces.IQueryExecutor;
import pw.edu.elka.rso.logic.procedures.Procedure;
import pw.edu.elka.rso.logic.procedures.ProceduresManager;
import pw.edu.elka.rso.server.*;
import pw.edu.elka.rso.storage.IDataShard;
import pw.edu.elka.rso.storage.QueryResult;
import pw.edu.elka.rso.storage.QueryResultReceiver;
import pw.edu.elka.rso.storage.SqlDescription;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class QueryExecutorImpl implements Observer, Runnable, IQueryExecutor, ITaskManager {
  static Logger logger = Logger.getLogger(QueryExecutorImpl.class);

  private IDataShard dataShard;
  private QueryResultReceiverImpl queryResultReceiver;
  private Server server;
  //
  private Observable consoleObservable;
  // ta kolejka zawiera w sobie procedury ktory wysyla do nas zdalny klient
  // z niej sa zdemowane zadania ktore potem sa wykonywane wraz z logika
  private LinkedBlockingQueue<QueryInfo> clientProcedureQueue = new LinkedBlockingQueue<>();
  private ProceduresManager proceduresManager;
  private QueryResultManager queryResultManager;

  //do tej kolejki pushowane sa zadania ktore logika ma wykonac, pushuje je serwer.
  private Queue<Task> tasks = new LinkedBlockingQueue<>();

  private static long queryId = 0;

  //TODO: wywalic, testowe
  public Server DoTegoRootuj;

  public QueryExecutorImpl(Observable consoleObservable, IDataShard dataShard, Server server) {
    this.proceduresManager = ProceduresManager.getInstance();
    proceduresManager.prepareProcedures();

    this.consoleObservable = consoleObservable;
    this.dataShard = dataShard;

    this.consoleObservable.addObserver(this);
    this.server = server;

    this.queryResultManager = new QueryResultManager();
  }

  public void executeProcedure(QueryInfo queryInfo, QueryType queryType, QueryTask queryTaskReceived) throws ClassNotFoundException {
    Procedure procedure = proceduresManager.getProcedure(queryInfo.getProcedureName());
    try {
      //logika powinna wiedziec gdzie jjuz jet polaczona


      /**
       *
       */
      SqlDescription sqlDescription = new SqlDescription();
      sqlDescription.statement = procedure.getParsedQuery();
      sqlDescription.toStringQuery(queryInfo.getProcedureName());
      sqlDescription.id = queryType == QueryType.RAW ? queryTaskReceived.getInput().id : queryInfo.getQueryId();


      /**
       * Ta ladna liste wypelni nam Tadek swoja funkcja haszujaca
       */


      QueryTask queryTask = new QueryTask(sqlDescription);

      if (queryType == QueryType.MANGED) {

        LinkedList<ShardDetails> rootQueryHere = new LinkedList<>();
        rootQueryHere.add(DoTegoRootuj.getServerDetails());
        //TODO: ALL LOGIC GOES HERE

        logger.debug("Doing some logic stuff.");

        /**
         * Tworzymy nowego taska do polaczenia z innym shardem dla warsty komunikacyjnej
         * tylko i wylacznie wtedy jesli nie jestemy juz z nim polaczeni
         */
        for (ShardDetails snigleShard : rootQueryHere) {
          boolean isAlreadyConnected = server.getConnections().containsKey(snigleShard);
          if (!isAlreadyConnected) {
            Task connectionTask = new SetConnectionTask(snigleShard);
            server.doTask(connectionTask);
          }
        }
        //dodaje siebie


        /**
         * QueryTask uzupelniamy:
         * whereToExecuteQuery - obvious - gdzie mamy wykonac dane zapytanie
         * rootQuery - ustalamy kto jest "gospodarzem" tego zapytania, ten bedzie zbieral dane i odsylal
         */
        queryTask.getWhereToExecuteQuery().addAll(rootQueryHere);
        queryTask.setQueryRoot(server.getServerDetails());
        queryTask.setQueryInfo(queryInfo);
        server.doTask(queryTask);
        //mozliwe ze zla kolejnosc wykonania
        rootQueryHere.add(server.getServerDetails());
        queryResultManager.beginQuery(sqlDescription, rootQueryHere);
      }


      /**
       * !!!!!!!
       * !!!!!!!
       * TUTAJ WYKONUJEMY ZAPYTANIE DO Sharda
       * !!!!!!!
       * !!!!!!!
       */

      //dataShard.query(sqlDescription);

      QueryResult qr = new QueryResult();
      qr.queryId = sqlDescription.id;
      String result = "Wynik:" + server.getServerDetails() + "[id:" + sqlDescription.id+ "]";
      qr.stringResult = result;


      //TESTOWE!
      if (queryType == QueryType.MANGED) {
        queryResultReceiver.getTestResult().add(qr);
      } else if (queryType == QueryType.RAW) {
        //TO WPIAC W MIEJSCE W KTORY MA BYC TWRZONY TASK Z REZUTLATEM !111
        Task queryResultTask = new QueryResultTask(qr);
        ((QueryResultTask) queryResultTask).setReturnShard(queryTaskReceived.getQueryRoot());
        server.doTask(queryResultTask);
      }

    } catch (Exception ex) {
      //TODO: ladne obluzycy wyjatek
      ex.printStackTrace();
    }
  }

  public void update(Observable o, Object arg) {
    if ((o instanceof InputManager)) {
      logger.debug("Zostalem powiadomiony");
      InputManager console = (InputManager) o;

      QueryInfo query = console.getQueryQueue().poll();
      logger.debug("Dodaje do kolejki wykonanie procedury \"" + query.toString() + "\"");

      clientProcedureQueue.add(query);

    }
  }

  public void run() {
    while (true) {

      /**
       * OBSLUGA KOLEJKI ZAPYTAN OD KLIENTA
       * Wykonywanie procedury z uwzglednieniem logiki
       * param QueryType.MANGED
       */
      if (clientProcedureQueue.size() > 0) {
        QueryInfo query = clientProcedureQueue.poll();
        logger.debug("Wykonuje procedure\"" + query + "\"");
        try {
          executeProcedure(query, QueryType.MANGED, null);
        } catch (ClassNotFoundException e) {
          //TODO
          e.printStackTrace();
        }
      }
      /**
       *  Obsluga taskow
       *
       */
      if (tasks.size() > 0) {
        for (int i = 0; i < tasks.size(); i++) {
          Task task = tasks.poll();

          /**
           * Task: QueryType
           * To zadanie przyszlo od innego sharda, wykonujemy tylko i wylaczenie zapytanie
           * do lokalnego sharda bez obslugi logiki zapytania.
           * param QueryType.RAW
           */
          if (task instanceof QueryTask) {
            QueryTask queryTask = (QueryTask) task;
            logger.debug("Dostalem(@" + server.getServerDetails() + ") zapytanie: " + queryTask.getInput().getProcedureName());

            try {
              executeProcedure(queryTask.getQueryInfo(), QueryType.RAW, queryTask);
            } catch (ClassNotFoundException e) {
              logger.debug("Wywalilem sie przy probie wykonania zapytania" + queryTask.getInput().getProcedureName());
              e.printStackTrace();
            }
          }
          if (task instanceof QueryResultTask) {
            QueryResultTask queryResultTask = (QueryResultTask) task;
            logger.debug("Dostalem(@" + server.getServerDetails() + ") odpowiedz  " + queryResultTask.getInput());
            QueryResult queryResult = queryResultTask.getInput();
            queryResultManager.insertResult(queryResult.queryId, queryResult.stringResult);
          }
        }
      }


      /**
       * Obsluga REZULTATOW(@TEST) z roznych nodow
       * Analogicznie bedzie dla rzeczywistych rezultatow tylko zmieni sie typ
       */
      if (!queryResultReceiver.getTestResult().isEmpty()) {
        logger.debug("Dostalem rezultat");
        Queue<QueryResult> queryResult = queryResultReceiver.getTestResult();
        for (int i = 0; i < queryResult.size(); i++) {
          QueryResult qr = queryResult.poll();
          //if(qr.) stworzyc taska

          logger.debug("Dodaje rezultat zapytania " + qr.toString());
          queryResultManager.insertResult(qr.queryId, qr.stringResult);
        }
      }

      /**
       * Obsluga koncowa rezultatu, rezultat gotowy do wyslania dalej
       *
       */
      if (queryResultManager.checkResult() != null) {
        //PUSH DALEJ
        //wyswietlam wynik calego zapytania dla jaj
        for (Map.Entry<Long, LinkedList<String>> entry : queryResultManager.returnResult().entrySet()) {
          Long queryId = entry.getKey();
          LinkedList<String> value = entry.getValue();
          for (String val : value) {
            logger.debug("Rezultat:" + val);
          }
        }

      }
    }

  }

  @Override
  public void doTask(Task task) {
    tasks.add(task);
  }

  public QueryResultReceiver getQueryResultReceiver() {
    return queryResultReceiver;
  }

  public void setQueryResultReceiver(QueryResultReceiverImpl queryResultReceiver) {
    this.queryResultReceiver = queryResultReceiver;
  }

  public static long returnNewQueryId(){
    return queryId++;
  }
}

