package pw.edu.elka.rso.logic;

import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;

import net.sf.jsqlparser.statement.Statement;

/**
 * Created with IntelliJ IDEA.
 * User: Maciek
 * Date: 25.05.13
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 */


public class Queue {

    private class Node{

        private Statement statement;
        private long hash;

        Node(){

        }

        private Statement getStatement(){
            return statement;
        }

        private void setStatement(Statement s){
            statement = s;
        }

        private long getHash(){
            return hash;
        }

        private void setHash(){                         // TU TRZEBA ZAIMPLEMENTOWAC POPRAWNA METODE GENERUJACA ID'ki ... MD5?
            Random rand = new Random();
            hash = (System.currentTimeMillis() - rand.nextLong())% 819 ;
        }
    }

    LinkedList<Node> queue=  new LinkedList<Node>();

    Queue(){

    }

    /**
     *
     * @param statement
     */
    public void add(Statement statement){
        Node node = new Node();
        node.setStatement(statement);
        node.setHash();
        queue.add(node);
    }

    /**
     *
     * @param i
     */
    public void remove(int i){
        queue.remove(i);
    }

    /**
     *
     */
    public void getFirst(){
        queue.getFirst();
    }

    /**
     *
     */
    public void getLast(){
        queue.getLast();
    }

    /**
     *
     * @param i
     */
    public Node get(int i){
       return queue.get(i);
    }

    public Statement getStatement(int i){
        Node node = queue.get(i);
        return node.getStatement();
    }

    public long getHash(int i){
       Node node = queue.get(i);
       return node.getHash();
    }

    public LinkedList<Node> load(Vector<Statement> vector){
        for(int i = 0; i < vector.size(); i++){
            Node node = new Node();
            node.setStatement(vector.get(i));
            node.setHash();
            queue.add(node);
        }
        return queue;
    }

    /**
     *
     * @return
     */
    public int size(){
        return queue.size();
    }
}
