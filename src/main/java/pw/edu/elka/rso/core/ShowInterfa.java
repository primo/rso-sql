package pw.edu.elka.rso.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

import static java.lang.System.out;


/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 11.06.13
 * Time: 08:58
 * To change this template use File | Settings | File Templates.
 */
public class ShowInterfa {
//  public static void main(String[] args) throws Exception {
//    System.out.println("Host addr: " + InetAddress.getLocalHost().getHostAddress());  // often returns "127.0.0.1"
//    Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
//    for (; n.hasMoreElements(); ) {
//      NetworkInterface e = n.nextElement();
//      System.out.println("Interface: " + e.getName());
//      Enumeration<InetAddress> a = e.getInetAddresses();
//      for (; a.hasMoreElements(); ) {
//        InetAddress addr = a.nextElement();
//        System.out.println("  " + addr.getHostAddress());
//      }
//    }
//  }

  public static String getIpAddress() throws IOException {
    String ip = null;
    try {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface iface = interfaces.nextElement();
        // filters out 127.0.0.1 and inactive interfaces
        if (iface.isLoopback() || !iface.isUp())
          continue;

        Enumeration<InetAddress> addresses = iface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          InetAddress addr = addresses.nextElement();
          ip = addr.getHostAddress();
          System.out.println(iface.getDisplayName() + " " + ip);
        }
      }
    } catch (SocketException e) {
      throw new RuntimeException(e);
    }
    return ip;

  }

  public static void main(String args[]) throws SocketException {
    Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
    for (NetworkInterface netint : Collections.list(nets))
      displayInterfaceInformation(netint);
  }

  static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
    out.printf("Display name: %s\n", netint.getDisplayName());
    out.printf("Name: %s\n", netint.getName());
    Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
    for (InetAddress inetAddress : Collections.list(inetAddresses)) {
      out.printf("InetAddress: %s\n", inetAddress);
    }
    out.printf("\n");
  }
}
