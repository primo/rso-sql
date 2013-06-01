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
        if(value == null){
            buffer.put(position, (byte) 0);
            return;
        }
        int new_value = (Integer) value;
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

class DoubleTableColumn extends TableColumn {
    DoubleTableColumn(int position) {
        super(9, position);
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

class CharTableColumn extends TableColumn {
    public CharTableColumn(int length, int position){
        super(2*(length)+1, position);
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