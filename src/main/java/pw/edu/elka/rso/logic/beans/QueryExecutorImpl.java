package pw.edu.elka.rso.logic.beans;

import org.apache.log4j.Logger;
import pw.edu.elka.rso.core.communication.ClientServer;
import pw.edu.elka.rso.logic.QueryExecution.Metadata;
import pw.edu.elka.rso.logic.interfaces.IQueryExecutor;
import pw.edu.elka.rso.logic.procedures.Procedure;
import pw.edu.elka.rso.logic.procedures.ProceduresManager;
import pw.edu.elka.rso.server.Server;
import pw.edu.elka.rso.server.ShardDetails;
import pw.edu.elka.rso.server.Task;
import pw.edu.elka.rso.server.tasks.*;
import pw.edu.elka.rso.storage.DataRepresentation.Table;
import pw.edu.elka.rso.storage.IDataShard;
import pw.edu.elka.rso.storage.QueryResult;
import pw.edu.elka.rso.storage.QueryResultReceiver;
import pw.edu.elka.rso.storage.SqlDescription;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class QueryExecutorImpl implements Observer, Runnable, IQueryExecutor, ITaskManager {
  static Logger logger = Logger.getLogger(QueryExecutorImpl.class);
  private static long queryId = 0;
  //TODO: wywalic, testowe
  public Server DoTegoRootuj;

  //TODO; Przyjmie obiekt QueryInfo i z niego zwroci np. stringa ktory ma uzupelnione wartosci  ? ? ?
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
  //glupia nazwa dla serwera ktory jest odpowiedzilny za komunikacje z clientem
  //T_T
  private ClientServer clientServer;
  private Metadata metadata;

  public QueryExecutorImpl(Observable consoleObservable, IDataShard dataShard, Server server, Metadata new_metadata) {
    metadata = new_metadata;
    this.proceduresManager = ProceduresManager.getInstance();
    proceduresManager.prepareProcedures();

    this.consoleObservable = consoleObservable;
    this.dataShard = dataShard;

    this.consoleObservable.addObserver(this);
    this.server = server;

    this.queryResultManager = new QueryResultManager();
  }

  public static long returnNewQueryId() {
    return queryId++;
  }

  public ClientServer getClientServer() {
    return clientServer;
  }

  public void setClientServer(ClientServer clientServer) {
    this.clientServer = clientServer;
  }

  public void executeProcedure(QueryInfo queryInfo, QueryType queryType, QueryTask queryTaskReceived) throws ClassNotFoundException {
    Procedure procedure = proceduresManager.getProcedure(queryInfo.getProcedureName(), queryInfo.getParameters());
    try {





      /**
       *
       * TUTAJ DODAJEMY LOGIKE PARTYCJONOWANIA
       * ROOTOWANIA ZAPYTAN DO ODPOWIEDNICH SHARDOW
       * ETCETC...
       * SPROWADZA SIE TO DO UZUPELNIENIA LISTY PONIZJE
       * rootQueryHere() WARTOSCIAMI ShardDetails mowiacymi GDZIE DANA PROCEDURA MAMY WYKONAC
       * USTAWIAMY WARTOSC DLA executeQuereOnThisShard W ZALEZNOSCI CZY TA PROCEDURA SIE WYKONA TUTAJ
       *
       *
       *
       */
      LinkedList<ShardDetails> rootQueryHere = new LinkedList<>();
      SqlDescription sqlDescription = new SqlDescription();
      sqlDescription.statement = procedure.getParsedQuery();

      sqlDescription.toStringQuery(queryInfo.getProcedureName());
      sqlDescription.id = queryType == QueryType.RAW ? queryTaskReceived.getInput().id : queryInfo.getQueryId();


      /**
       * Ta ladna liste wypelni nam
       */


      QueryTask queryTask = new QueryTask(sqlDescription);
      boolean executeQuereOnThisShard = true;

      if (queryType == QueryType.MANGED) {


        //rootQueryHere.add(DoTegoRootuj.getServerDetails());
        //TODO: ALL LOGIC GOES HERE


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
       * TUTAJ WYKONUJEMY ZAPYTANIE DO Sharda
       * !!!!!!!
       */
      if (executeQuereOnThisShard)
        dataShard.query(sqlDescription, queryTaskReceived);


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
          } else if (task instanceof QueryResultTask) {
            QueryResultTask queryResultTask = (QueryResultTask) task;
            logger.debug("Dostalem(@" + server.getServerDetails() + ") odpowiedz  " + queryResultTask.getInput());
            QueryResult queryResult = queryResultTask.getInput();
            queryResult.prepareForReading();
            queryResultManager.insertResult(queryResult.queryId, queryResult.output);
          } else if (task instanceof MetadataUpdateTask) {
            logger.debug("Dostalem(@" + server.getServerDetails() + ") odpowiedz  " + task.getInput());
            metadata.updateMetadata(((MetadataUpdateTask) task).getInput());
          }
        }
      }


      /**
       * Obsluga REZULTATOW(@TEST) z roznych nodow
       * Analogicznie bedzie dla rzeczywistych rezultatow tylko zmieni sie typ
       */
      if (!queryResultReceiver.getTestResult().isEmpty()) {
        logger.debug("Dostalem rezultat");
        Queue<AbstractMap.SimpleEntry<QueryResult, Object>> queryResult = queryResultReceiver.getTestResult();
        for (int i = 0; i < queryResult.size(); i++) {
          AbstractMap.SimpleEntry<QueryResult, Object> qr = queryResult.poll();
          QueryResult queryResultValue = qr.getKey();
          Object queryContext = qr.getValue();
          // jesli kontekt rozny od null, znaczy sie ze to zadanie przyszlo z zewnetrz nalezy wynik odeslac wlascicielowi
          if (queryContext != null) {
            QueryTask queryTask = (QueryTask) queryContext;
            QueryResultTask queryResultTask = new QueryResultTask(queryResultValue);
            queryResultTask.setReturnShard(queryTask.getQueryRoot());
            server.doTask(queryResultTask);
          } else {
            // w przeciwym wypadku wrzucamy rezultat
            logger.debug("Dodaje rezultat zapytania " + qr.toString());
            queryResultManager.insertResult(queryResultValue.queryId, queryResultValue.output);
          }
        }
      }

      /**
       * Obsluga koncowa rezultatu, rezultat gotowy do wyslania dalej
       *
       */
      if (queryResultManager.checkResult() != null) {
        //PUSH DALEJ
        //wyswietlam wynik calego zapytania dla jaj
        for (Map.Entry<Long, LinkedList<Table>> entry : queryResultManager.returnResult().entrySet()) {
          Long queryId = entry.getKey();
          LinkedList<Table> value = entry.getValue();

          Table resultTable = null;

          if(value.size() > 1){
            resultTable = value.get(0);
          }

          for (int i = 1; i < value.size(); i++) {
            resultTable.merge(value.get(i));
          }

//          System.out.println(resultTable.toString());
          /**
           * ODKOMENTOWAC ZEBY WYSLAC WYNIK DO server'a ktory komunikuje sie z klientem
           * NARAZIE POMIJANY
           *
           */
          resultTable = resultTable == null ? value.get(0) : resultTable;
          clientServer.pushToClientServer(resultTable.toString());
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
}

