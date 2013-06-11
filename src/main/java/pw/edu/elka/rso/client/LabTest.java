package pw.edu.elka.rso.client;

import pw.edu.elka.rso.logic.procedures.SerializedProcedureCall;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: primo
 * Date: 6/11/13
 * Time: 9:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class LabTest {

    public static final String clusterGateId = "localhost";

    public static void main(String[] args) throws Exception {
        String modifiedSentence;
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
        Socket clientSocket = new Socket(clusterGateId, 2222);
        ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // Test table
        outToServer.writeObject("CreateTest");
        modifiedSentence = inFromServer.readLine();
        System.out.println("FROM SERVER: " + modifiedSentence);

        // Test table
        outToServer.writeObject("CreateTest2");
        modifiedSentence = inFromServer.readLine();
        System.out.println("FROM SERVER: " + modifiedSentence);

        for(int i = 0; i < 1000; ++i) {
            List<String> params = new ArrayList<>();
            params.add(Integer.valueOf(i).toString());
            params.add(Integer.valueOf(i + 1).toString());
            params.add("TEsT"+i);
            SerializedProcedureCall procedure = new SerializedProcedureCall("InsertIntoTest",params);
            outToServer.writeObject(procedure);
            modifiedSentence = inFromServer.readLine();
            System.out.println("FROM SERVER: " + modifiedSentence);
            procedure = new SerializedProcedureCall("InsertIntoTest2",params);
            outToServer.writeObject(procedure);
            modifiedSentence = inFromServer.readLine();
            System.out.println("FROM SERVER: " + modifiedSentence);
        }

        for (int i = 0 ; i < 50 ; ++i) {
            List<String> params = new ArrayList<>();
            params.add(Integer.valueOf(1000 - i).toString());
            SerializedProcedureCall procedure = new SerializedProcedureCall("SelectFromTestWhere2",params);
            outToServer.writeObject(procedure);
            modifiedSentence = inFromServer.readLine();
            System.out.println("FROM SERVER: " + modifiedSentence);
        }

        for (int i = 0 ; i < 50 ; ++i) {
            List<String> params = new ArrayList<>();
            params.add(Integer.valueOf(500 - i).toString());
            params.add(Integer.valueOf(510 + i).toString());
            SerializedProcedureCall procedure = new SerializedProcedureCall("JoinExample",params);
            outToServer.writeObject(procedure);
            modifiedSentence = inFromServer.readLine();
            System.out.println("FROM SERVER: " + modifiedSentence);
        }

        clientSocket.close();
    }
}
