import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        //Stworz specyfikacje
        Map<String, ColumnType> table_spec = new HashMap<String, ColumnType>();
        table_spec.put("ID", new IntColumnType());
        table_spec.put("NAME", new CharColumnType(32));
        table_spec.put("SALARY", new DoubleColumnType());

        //Stworz tabele
        Table table = new Table(table_spec);

        Record record = table.newRecord();
        record.setValue("ID", 1);
        record.setValue("SALARY", 1234.56);
        record.setValue("NAME", "Stefanski");
        table.insert(record);
    }
}