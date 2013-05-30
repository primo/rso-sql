package pw.edu.elka.rso.storage;

import java.util.LinkedList;

public class Example {

    public static void main(final String[] args){
        TableSchema ts = new TableSchema();
        ts.addColumn("ID", ColumnType.INT, 0);
        ts.addColumn("SALARY", ColumnType.DOUBLE, 0);
        ts.addColumn("SURNAME", ColumnType.CHAR, 22);

        Table table = new Table(ts);
        Record record = table.newRecord();
        record.setValue("ID", 1);
        record.setValue("SALARY", 1230.45);
        record.setValue("SURNAME", "Abacki");
        table.insert(record);

        record.setValue("ID", 2);
        record.setValue("SALARY", 0.918731);
        record.setValue("SURNAME", "Babacki");
        table.insert(record);

        for (Record record2 : table) {
            System.out.println(record2.getValue("ID"));
            System.out.println(record2.getValue("SALARY"));
            System.out.println(record2.getValue("SURNAME"));
            System.out.println();
        }

        table.createIndex("SALARY");

        //TwofoldIterator TwofoldIteratora
        LinkedList<LinkedList<Integer>> hi = new LinkedList<LinkedList<Integer>>();
        LinkedList<Integer> lo;
        for(int i=0; i<30; i+=10){
            lo = new LinkedList<Integer>();
            for(int j=0; j<2; j++)
                lo.add(i+j);
            hi.add(lo);
        }

        TwofoldIterator<Integer> test = new TwofoldIterator<Integer>(hi);
        while(test.hasNext()){
            System.out.println(test.next());
            test.remove();
        }

        test = new TwofoldIterator<Integer>(hi);
        while(test.hasNext()){
            System.out.println(test.next());
            test.remove();
        }
    }
}