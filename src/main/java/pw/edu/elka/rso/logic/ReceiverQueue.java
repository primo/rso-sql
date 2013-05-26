package pw.edu.elka.rso.logic;

import java.nio.ByteBuffer;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Maciek
 * Date: 26.05.13
 * Time: 15:53
 * To change this template use File | Settings | File Templates.
 */
public class ReceiverQueue {

    Vector<ByteBuffer> buffer = new Vector<ByteBuffer>();

    public void add(ByteBuffer b){
        buffer.add(b);
    }

    public void remove(int i){
        buffer.remove(i);
    }

    public void displayElement(){

    }

    public void getElement(int i){
        buffer.get(i);
    }

    public int size(){
        return buffer.size();
    }


}
