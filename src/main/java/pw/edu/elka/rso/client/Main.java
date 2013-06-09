package pw.edu.elka.rso.client;

import pw.edu.elka.rso.logic.procedures.SerializedProcedureCall;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        String modifiedSentence;
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
        Socket clientSocket = new Socket("localhost", 2222);
        ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        List<String> params = new ArrayList<>();
        params.add("Pierwszy");
        params.add("Drugi");
        params.add("Trzeci");
        SerializedProcedureCall procedure = new SerializedProcedureCall("InsertIntoWorkers",params);

        outToServer.writeObject(procedure);
        modifiedSentence = inFromServer.readLine();
        System.out.println("FROM SERVER: " + modifiedSentence);
        clientSocket.close();
    }
}
