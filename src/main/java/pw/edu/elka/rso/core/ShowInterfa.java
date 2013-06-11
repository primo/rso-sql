package pw.edu.elka.rso.core;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 11.06.13
 * Time: 08:58
 * To change this template use File | Settings | File Templates.
 */
public class ShowInterfa {
  public static void main(String[] args) throws Exception
  {
    System.out.println("Host addr: " + InetAddress.getLocalHost().getHostAddress());  // often returns "127.0.0.1"
    Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
    for (; n.hasMoreElements();)
    {
      NetworkInterface e = n.nextElement();
      System.out.println("Interface: " + e.getName());
      Enumeration<InetAddress> a = e.getInetAddresses();
      for (; a.hasMoreElements();)
      {
        InetAddress addr = a.nextElement();
        System.out.println("  " + addr.getHostAddress());
      }
    }
  }
}
