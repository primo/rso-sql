package pw.edu.elka.rso.storage.DataRepresentation;

import java.util.Iterator;

public class Example {

    public static void main(final String[] args){
        TableSchema ts = new TableSchema();
        ts.addColumn("ID", ColumnType.INT, 0);
        ts.addColumn("SALARY", ColumnType.DOUBLE, 0);
        ts.addColumn("SURNAME", ColumnType.CHAR, 22);

        Table table = new Table(ts);
        table.createIndex("SALARY");

        Record record = table.newRecord();
        record.setValue("ID", 3);
        record.setValue("SALARY", 2230.45);
        record.setValue("SURNAME", "Cabacki");
        table.insert(record);

        record.setValue("ID", 1);
        record.setValue("SALARY", 718.731);
        record.setValue("SURNAME", "Babacki");
        table.insert(record);

        record.setValue("ID", 2);
        record.setValue("SALARY", 1000.0);
        record.setValue("SURNAME", "Abacki");
        table.insert(record);

        record.setValue("ID", 4);
        record.setValue("SALARY", 1110.0);
        record.setValue("SURNAME", "Dabacki");
        table.insert(record);

        record.setValue("ID", 5);
        record.setValue("SALARY", 1000.0);
        record.setValue("SURNAME", "Ebacki");
        table.insert(record);


        Iterator<Record> iterator = table.indexedIterator("SALARY", 1000.0, true, 2000.0, false);
        while (iterator.hasNext()) {
            Record record2 = iterator.next();
            System.out.println(record2.getValue("ID"));
            System.out.println(record2.getValue("SALARY"));
            System.out.println(record2.getValue("SURNAME"));
            System.out.println();

            //if(record2.getValue("ID").equals(5))
            iterator.remove();//Usuwanie jeszcze nie dziala =/
        }

        //Po zmianach
        System.out.println("--- PO ZMIANACH ---");
        for(Record record2 : table){
            System.out.println(record2.getValue("ID"));
            System.out.println(record2.getValue("SALARY"));
            System.out.println(record2.getValue("SURNAME"));
            System.out.println();
        }
    }
}
