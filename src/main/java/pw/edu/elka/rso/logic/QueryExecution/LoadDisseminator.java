package pw.edu.elka.rso.logic.QueryExecution;

import pw.edu.elka.rso.server.ShardDetails;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

public class LoadDisseminator implements Runnable {
    Metadata metadata;
    private MulticastSocket sendingSocket;
    private MulticastSocket receivingsocket;
    ByteBuffer sendingBuffer;
    InetAddress sendingInetAddress;

    public LoadDisseminator(Metadata a_metadata){
        metadata = a_metadata;
        byte[] sndBuffer = new byte[8];
        sendingBuffer = ByteBuffer.wrap(sndBuffer);

        try{
            receivingsocket = new MulticastSocket(ShardDetails.loadReportingPort);
            sendingInetAddress = InetAddress.getByName(ShardDetails.groupName);
            receivingsocket.joinGroup(sendingInetAddress);
            sendingSocket = new MulticastSocket();
            //stworz watek nasluchujacy
            (new Thread(this)).start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void update(int node_id, float load){
        sendingBuffer.rewind();
        sendingBuffer.putInt(node_id);
        sendingBuffer.putFloat(load);
        byte[] snd_buf = sendingBuffer.array();
        DatagramPacket pack = new DatagramPacket(snd_buf, snd_buf.length, sendingInetAddress, ShardDetails.loadReportingPort);
        try {
            sendingSocket.send(pack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buf = new byte[8];
        ByteBuffer byteBuffer = ByteBuffer.wrap(buf);
        DatagramPacket pack = new DatagramPacket(buf, buf.length);
        while(true){
            try {
                receivingsocket.receive(pack);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int node_id = byteBuffer.getInt();
            float load = byteBuffer.getFloat();
            byteBuffer.rewind();
            metadata.doUpdateLoad(node_id, load);
        }
    }
}
