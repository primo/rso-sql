package pw.edu.elka.rso.server;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 04.06.13
 * Time: 02:25
 * To change this template use File | Settings | File Templates.
 */
public class ShardDetails implements Serializable, Comparable<ShardDetails> {
  private int portNumber;
  public final static int loadReportingPort = 3333;
  public final static String group_name = "load_exchange";
  private InetAddress host;
  private int id;

  public ShardDetails(int portNumber, InetAddress host, int id) {
      this.portNumber = portNumber;
      this.host = host;
      this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ShardDetails that = (ShardDetails) o;

    if (id != that.id) return false;

    return true;
  }


  @Override
  public int hashCode() {
    return id;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public InetAddress getHost() {
    return host;
  }

  public void setHost(InetAddress host) {
    this.host = host;
  }

  public int getPortNumber() {
    return portNumber;
  }

  public void setPortNumber(int portNumber) {
    this.portNumber = portNumber;
  }

  @Override
  public String toString() {
    return "ShardDetails{" +
        "portNumber=" + portNumber +
        ", host=" + host +
        ", id=" + id +
        '}';
  }

  public int compareTo(ShardDetails sd) {
      if (id < sd.id) return -1;
      if (id > sd.id) return 1;
      return 0;
  }
}
