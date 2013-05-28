package pw.edu.elka.rso.procedures;

/**
 * Created with IntelliJ IDEA.
 * User: zewlak
 * Date: 28.05.13
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */

import pw.edu.elka.rso.logic.procedures.Procedure;

public class SelectFromClients extends Procedure {

    public final String sql = "SELECT NumberOfSeats, COUNT(ReserveID) " +
            "FROM Flight AS F, Reservation AS R " +
            "WHERE F.FlightID=R.FlightID AND R.FlightID=? " +
            "GROUP BY NumberOfSeats;";

}
