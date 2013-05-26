package pw.edu.elka.rso.storage;

import java.nio.ByteBuffer;

abstract class TableColumn {
    final int length;
    final int position;

    protected TableColumn(int length, int position) {
        this.length = length;
        this.position = position;
    }

    abstract void setValue(ByteBuffer ba, Object value);
    abstract Object getValue(ByteBuffer ba);
}

class IntTableColumn extends TableColumn {
    IntTableColumn(int position) {
        super(5, position);
    }

    void setValue(ByteBuffer buffer, Object value){
        int new_value = (Integer) value;
        buffer.put(position, (byte) 1);
        buffer.putInt(position+1, new_value);
    }

    Object getValue(ByteBuffer buffer){
        if (buffer.get(position) == (byte) 0)
            return null;
        else
            return buffer.getInt(position + 1);
    }
}

class DoubleTableColumn extends TableColumn {
    DoubleTableColumn(int position) {
        super(9, position);
    }

    void setValue(ByteBuffer buffer, Object value){
        double new_value = (Double) value;
        buffer.put(position, (byte) 1);
        buffer.putDouble(position + 1, new_value);
    }

    Object getValue(ByteBuffer buffer){
        if (buffer.get(position) == (byte) 0)
            return null;
        else
            return buffer.getDouble(position+1);
    }
}

class CharTableColumn extends TableColumn {
    public CharTableColumn(int length, int position){
        super(2*(length + 1)+1, position);
    }

    void setValue(ByteBuffer buffer, Object object){
        buffer.put(position, (byte) 1);

        char[] new_value = ((String)object).toCharArray();
        int i;
        for(i = 0; i < Math.min(new_value.length, length/2); i++){
            buffer.putChar(position + 2 * i + 1, new_value[i]);
        }
        buffer.putChar(position + 2 * i + 1, '\0');
    }

    Object getValue(ByteBuffer buffer){
        if (buffer.get(position) == (byte)0)
            return null;
        char[] output = new char[length/2];
        for(int i = 0; i < length/2; i++){
            output[i] = buffer.getChar(position + 2*i);
            if(output[i] == '\0')
                break;
        }
        return new String(output);
    }
}