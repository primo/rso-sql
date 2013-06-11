import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import pw.edu.elka.rso.server.AbstractServer;
import pw.edu.elka.rso.server.ShardDetails;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 11.06.13
 */
@RunWith(JUnit4.class)
public class ClientTest2 extends SetupClassTest {

  ObjectOutputStream oos;
  ObjectInputStream ois;


  @Test
  public void selectAllFromTest2() throws IOException, ClassNotFoundException {
    oos = new ObjectOutputStream(clientSocket.getOutputStream());

    String input = "SelectAllFromTest";
    oos.writeObject(input);

    //czekamy na odpowiedz
    ois = new ObjectInputStream(clientSocket.getInputStream());
    Object data = ois.readObject();
    System.out.println(data.toString());
  }
}
