import java.nio.ByteBuffer;

abstract class ColumnType {
    abstract public void putValue(ByteBuffer byte_buffer, Object object);
    //abstract public Object getValue()
    abstract int getLength();
}

class TableColumn {
    ColumnType columnType;
    public final int position;

    public TableColumn(ColumnType a_column_type, int a_position) {
        columnType = a_column_type;
        position = a_position;
    }

    public void putValue(ByteBuffer byte_buffer, Object object) {
        columnType.putValue(byte_buffer, object);
    }

    /*public Object getValue(ByteBuffer byte_buffer) {
        return columnType.getValue(byte_buffer);
    }*/
}

//----------------------------------------------------

class IntColumnType extends ColumnType {
    public int getLength(){
        return 4;
    }
    public void putValue(ByteBuffer byte_buffer, Object new_value) {
        byte_buffer.putInt((Integer) new_value);
    }
}

class DoubleColumnType extends ColumnType {
    public int getLength(){
        return 8;
    }
    public void putValue(ByteBuffer byte_buffer, Object new_value){
        byte_buffer.putDouble((Double) new_value);
    }
}

class CharColumnType extends ColumnType {
    public int length;
    public CharColumnType(int new_length){
        length = new_length;
    }
    public int getLength(){
        return length;
    }

    public void putValue(ByteBuffer byte_buffer, Object object){
        String string = (String) object;
        char[] new_value = string.toCharArray();
        for(int i = 0; i < Math.min(new_value.length, length); ++i){
            byte_buffer.putChar(new_value[i]);
        }
    }
}

