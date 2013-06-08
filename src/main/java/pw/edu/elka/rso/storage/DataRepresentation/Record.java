package pw.edu.elka.rso.storage.DataRepresentation;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Record {
    TableSchema tableSchema;
    ByteBuffer byteBuffer;

    public Record(TableSchema table_schema){
        tableSchema = table_schema;
        byteBuffer = ByteBuffer.allocate(tableSchema.getLength());
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
    }

    public void anew(){
        byteBuffer = ByteBuffer.allocate(tableSchema.getLength());
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
    }

    void setByteBuffer(ByteBuffer byte_buffer){
        byteBuffer = byte_buffer;
    }

    public void setValue(String column_name, Object value){
        tableSchema.getTableColumn(column_name).setValue(byteBuffer, value);
    }

    public Object getValue(String column_name){
        return tableSchema.getTableColumn(column_name).getValue(byteBuffer);
    }

    public ByteBuffer getContentDuplicate() {
        return byteBuffer.duplicate();
    }
}
