
import org.junit.Before;
import org.junit.BeforeClass;
import pw.edu.elka.rso.core.communication.ClientServer;
import pw.edu.elka.rso.logic.QueryExecution.Metadata;
import pw.edu.elka.rso.logic.beans.InputManager;
import pw.edu.elka.rso.logic.beans.QueryExecutorImpl;
import pw.edu.elka.rso.logic.beans.QueryResultReceiverImpl;
import pw.edu.elka.rso.server.AbstractServer;
import pw.edu.elka.rso.server.Server;
import pw.edu.elka.rso.server.ShardDetails;
import pw.edu.elka.rso.storage.DataShard;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 11.06.13
 */
public class SetupClassTest {

  private static InputManager inputManager;
  private static Server server;
  private static ClientServer clientServer;

  private static Thread serverThread;
  private static Thread serverToClientThread;

  private static QueryResultReceiverImpl queryResultReceiver;
  private static DataShard dataShard;
  private static Metadata metadata;

  private static QueryExecutorImpl queryExecutor;
  private static Thread queryExecutorThread;

  protected static Socket clientSocket;
  protected static ShardDetails clientConnectHere;

  @BeforeClass
  public static void setupData() throws IOException {
    inputManager = new InputManager();
    server = new Server(2222, 1);
    clientServer = new ClientServer(5000,100);
    clientServer.setInputManager(inputManager);

    serverThread = new Thread(server);
    serverToClientThread = new Thread(clientServer);

    queryResultReceiver = new QueryResultReceiverImpl();
    dataShard = new DataShard();
    dataShard.registerQueryResultReceiver(queryResultReceiver);

    metadata = new Metadata(server);
    queryExecutor = new QueryExecutorImpl(inputManager, dataShard, server, metadata);
    queryExecutor.setQueryResultReceiver(queryResultReceiver);
    queryExecutor.setClientServer(clientServer);
    queryExecutorThread = new Thread(queryExecutor);


    serverThread.start();
    serverToClientThread.start();
    dataShard.start();
    queryExecutorThread.start();

    clientConnectHere = new ShardDetails(5000, InetAddress.getLocalHost(), 100);
    clientSocket = AbstractServer.initConnectionToOtherShard(clientConnectHere);
  }

}
