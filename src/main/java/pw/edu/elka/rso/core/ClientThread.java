package pw.edu.elka.rso.core;

import pw.edu.elka.rso.logic.procedures.ProceduresManager;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: zewlak
 * Date: 31.05.13
 * Time: 16:50
 * The DB server client thread
 */
class ClientThread extends Thread {

    private final ClientThread[] threads;
    private ProceduresManager proceduresManager;
    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private int maxClientsCount;

    public ClientThread(Socket clientSocket, ClientThread[] threads, ProceduresManager proceduresManager) {
        this.proceduresManager = proceduresManager;
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        ClientThread[] threads = this.threads;

        //TODO change to some DataTable Structure
        String responseData;

        try {
          /*
           * Create input and output streams for this client.
           */
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            String procedureName = is.readLine().trim();
            System.out.println("Client wants to execute procedure: "+procedureName);
            try {
                responseData = proceduresManager.executeProcedure(procedureName);
            } catch (ClassNotFoundException e) {
                // TODO send ClassNotFoundException to client somehow
                responseData = "Nie znaleziono takiej procedury!";
            }
            System.out.println("Sending to client: "+responseData);
            os.println(responseData + '\n');

          /*
           * Clean up. Set the current thread variable to null so that a new client
           * could be accepted by the server.
           */
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] == this) {
                    threads[i] = null;
                }
            }

          /*
           * Close the output stream, close the input stream, close the socket.
           */
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}
