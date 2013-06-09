package pw.edu.elka.rso.storage.DataRepresentation;

import java.io.Serializable;
import java.nio.ByteBuffer;

public abstract class TableColumn implements Serializable {

    public int getLength() {
        return length;
    }

    final int length;
    final int position;
    final public ColumnType columnType;

    protected TableColumn(int length, int position, ColumnType columnType) {
        this.length = length;
        this.position = position;
        this.columnType = columnType;
    }

    abstract void setValue(ByteBuffer ba, Object value);
    abstract Object getValue(ByteBuffer ba);
}

class IntTableColumn extends TableColumn implements Serializable {
    IntTableColumn(int position) {
        super(5, position, ColumnType.INT);
    }

    void setValue(ByteBuffer buffer, Object value){
        if(value == null){
            buffer.put(position, (byte) 0);
            return;
        }

        final int new_value ;
        if (value instanceof Integer)
            new_value = (Integer) value;
        else {
            new_value = ((Long)value).intValue();
        }
        buffer.put(position, (byte) 1);
        buffer.putInt(position+1, new_value);
    }

    Object getValue(ByteBuffer buffer){
        if (buffer.get(position) == 0)
            return null;
        else
            return buffer.getInt(position + 1);
    }
}

class DoubleTableColumn extends TableColumn implements Serializable {
    DoubleTableColumn(int position) {
        super(9, position, ColumnType.DOUBLE);
    }

    void setValue(ByteBuffer buffer, Object value){
        if(value == null){
            buffer.put(position, (byte) 0);
            return;
        }
        double new_value = (Double) value;
        buffer.put(position, (byte) 1);
        buffer.putDouble(position + 1, new_value);
    }

    Object getValue(ByteBuffer buffer){
        if (buffer.get(position) == 0)
            return null;
        else
            return buffer.getDouble(position + 1);
    }
}

class CharTableColumn extends TableColumn implements Serializable {
    public CharTableColumn(int length, int position){
        super(2*(length)+1, position, ColumnType.CHAR);
    }

    void setValue(ByteBuffer buffer, Object object){
        buffer.position(position);
        if(object == null){
            buffer.put((byte) 0);
            return;
        }
        buffer.put((byte) 1);
        char[] new_value = ((String)object).toCharArray();
        int chars = Math.min(new_value.length, length/2);

        int i;
        for(i = 0; i < chars; i++)
            buffer.putChar(new_value[i]);

        if(i != length/2)
            buffer.putChar('\0');
    }

    Object getValue(ByteBuffer buffer){
        buffer.position(position);
        if (buffer.get() == 0)
            return null;
        int chars = length/2;
        char[] output = new char[chars];
        for(int i = 0; i < chars; i++){
            output[i] = buffer.getChar();
            if(output[i] == '\0')
                break;
        }
        return new String(output);
    }
}