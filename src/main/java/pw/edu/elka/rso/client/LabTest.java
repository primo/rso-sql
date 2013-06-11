package pw.edu.elka.rso.client;

import pw.edu.elka.rso.logic.procedures.SerializedProcedureCall;
import pw.edu.elka.rso.server.AbstractServer;
import pw.edu.elka.rso.server.ShardDetails;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
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
        ShardDetails clientServer = new ShardDetails(5000, 100, clusterGateId);
        Socket clientSocket = AbstractServer.initConnectionToOtherShard(clientServer);
        ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());

        // Test table
        outToServer.writeObject("CreateTest");
        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
        Object data = ois.readObject();
        System.out.println(data.toString());

        // Test table
        outToServer.writeObject("CreateTest2");
        ois = new ObjectInputStream(clientSocket.getInputStream());
        data = ois.readObject();
        System.out.println(data.toString());

        for(int i = 0; i < 1000; ++i) {
            List<String> params = new ArrayList<>();
            String req = "InsertIntoTest " + Integer.valueOf(i).toString() + " " +
                    Integer.valueOf(i + 1).toString() + " TEsT"+Integer.valueOf(i).toString();
            outToServer.writeObject(req);
            ois = new ObjectInputStream(clientSocket.getInputStream());
            data = ois.readObject();
            System.out.println(data.toString());

            req = "InsertIntoTest2 " + Integer.valueOf(i).toString() + " " +
                    Integer.valueOf(i + 1).toString() + " TEsT"+Integer.valueOf(i).toString();
            outToServer.writeObject(req);
            ois = new ObjectInputStream(clientSocket.getInputStream());
            data = ois.readObject();
            System.out.println(data.toString());
        }

        for (int i = 0 ; i < 50 ; ++i) {
            outToServer.writeObject("SelectFromTestWhere2 " + Integer.valueOf(1000 - i).toString());
            ois = new ObjectInputStream(clientSocket.getInputStream());
            data = ois.readObject();
            System.out.println(data.toString());
        }

        for (int i = 0 ; i < 50 ; ++i) {
            outToServer.writeObject("JoinExample "+Integer.valueOf(500 - i).toString()+" "+Integer.valueOf(500 + i).toString());
            ois = new ObjectInputStream(clientSocket.getInputStream());
            data = ois.readObject();
            System.out.println(data.toString());
        }

        clientSocket.close();
    }
}
