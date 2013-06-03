package pw.edu.elka.rso.core;

import org.apache.log4j.Logger;
import pw.edu.elka.rso.logic.procedures.ProceduresManager;

import java.io.*;
import java.net.*;

/**
 * Main class, bootstrapping the database.
 */
public class Main {

    static Logger logger = Logger.getLogger(Main.class);

    // This DB server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 10;
    private static final ClientThread[] threads = new ClientThread[maxClientsCount];

    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;

    public static void main(String[] args) throws IOException {
        int portNumber = 2222;
        serverSocket = new ServerSocket(portNumber);
        logger.debug("Start listening server thread");

        ProceduresManager proceduresManager = new ProceduresManager();
        proceduresManager.prepareProcedures();

        while (true) {
            clientSocket = serverSocket.accept();

              /*
              * Create a client socket for each connection and pass it to a new client
              * thread.
              */
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] == null) {
                    logger.debug("New client! Creating new thread...");
                    (threads[i] = new ClientThread(clientSocket, threads, proceduresManager)).start();
                    break;
                }
            }
            //TODO handle clients over maxClientsCount
        }
    }
}
