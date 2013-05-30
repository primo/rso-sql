package pw.edu.elka.rso.storage;

import java.util.Iterator;
import java.util.LinkedList;

public class Example {

    public static void main(final String[] args){
        TableSchema ts = new TableSchema();
        ts.addColumn("ID", ColumnType.INT, 0);
        ts.addColumn("SALARY", ColumnType.DOUBLE, 0);
        ts.addColumn("SURNAME", ColumnType.CHAR, 22);

        Table table = new Table(ts);
        Record record = table.newRecord();
        record.setValue("ID", 3);
        record.setValue("SALARY", 1230.45);
        record.setValue("SURNAME", "Cabacki");
        table.insert(record);

        record.setValue("ID", 1);
        record.setValue("SALARY", 0.918731);
        record.setValue("SURNAME", "Babacki");
        table.insert(record);

        record.setValue("ID", 2);
        record.setValue("SALARY", 100.0);
        record.setValue("SURNAME", "Abacki");
        table.insert(record);

        table.createIndex("SALARY");
//        Iterator<Record> iterator = table.iterator();
        Iterator<Record> iterator = table.indexedItearator("SALARY");
        while (iterator.hasNext()) {
            Record record2 = iterator.next();
            System.out.println(record2.getValue("ID"));
            System.out.println(record2.getValue("SALARY"));
            System.out.println(record2.getValue("SURNAME"));
            System.out.println();
        }
    }
}