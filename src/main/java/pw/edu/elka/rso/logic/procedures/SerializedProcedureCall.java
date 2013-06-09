package pw.edu.elka.rso.logic.procedures;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zewlak
 * Date: 09.06.13
 * Time: 11:52
 * To change this template use File | Settings | File Templates.
 */
public class SerializedProcedureCall implements Serializable {
    String name;
    List<String> params;

    public SerializedProcedureCall(String name, List<String> params){
        this.name=name;
        this.params=params;
    }

    public String getName(){
        return this.name;
    }

    public List<String> getParams() {
        return this.params;
    }
}
