package pw.edu.elka.rso.core.communication;

import pw.edu.elka.rso.server.AbstractServer;
import pw.edu.elka.rso.server.ShardDetails;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 09.06.13
 */
public class Client {

  public static void main(String[] args) throws IOException, ClassNotFoundException {

    ShardDetails clientServer = new ShardDetails(5000, 100, "192.168.47.235");

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    Socket clientSocket = AbstractServer.initConnectionToOtherShard(clientServer);

    ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
    System.out.println("Podłączony do " + clientServer.toString());
    while(true) {
        System.out.print("Enter procedureName [params,params]");
        String input = br.readLine();
        oos.writeObject(input);

        //czekamy na odpowiedz
        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
        Object data = ois.readObject();
        System.out.println(data.toString());
    }
  }
}
