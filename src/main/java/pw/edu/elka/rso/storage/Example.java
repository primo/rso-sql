package pw.edu.elka.rso.storage;

import java.util.Iterator;

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
        record.setValue("SURNAME", "Jachocinski");
        table.insert(record);

        record.setValue("ID", 2);
        record.setValue("SALARY", 0.918731);
        record.setValue("SURNAME", "Piechocinski");
        table.insert(record);

        Iterator<Record> iterator = table.tableIterator();

        while(iterator.hasNext()){
            record = iterator.next();
            System.out.println(record.getValue("ID"));
            System.out.println(record.getValue("SALARY"));
            System.out.println(record.getValue("SURNAME"));
            System.out.println();
        }
    }
}
