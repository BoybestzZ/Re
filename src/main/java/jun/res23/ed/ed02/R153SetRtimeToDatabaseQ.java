/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed02;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jun.res23.ed.ed02.R151GraphUnitTimeHistoryR.main;

/**
 *
 * @author jun
 */
public class R153SetRtimeToDatabaseQ {

    public static void main(String[] args) {
        Path databaseDir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R140DatabaseQ");

        try {
            // main(databaseDir, "D01Q01", LocalDateTime.of(2023, 2, 15, 12, 35, 10, 0), 90);
//            main(databaseDir, "D03Q01", LocalDateTime.of(2023, 2, 24, 13, 37, 55, 0), 90);
            main(databaseDir, "D01Q09", LocalDateTime.of(2023, 2, 15, 14, 12, 10, 0), 90);
            main(databaseDir, "D01Q11", LocalDateTime.of(2023, 2, 15, 17, 18, 20, 0), 90);
            main(databaseDir, "D02Q05", LocalDateTime.of(2023, 2, 17, 14, 48, 40, 0), 90);
        } catch (SQLException ex) {
            Logger.getLogger(R153SetRtimeToDatabaseQ.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            //          R151GraphUnitTimeHistoryR.main(databaseDir, "D01Q01");
//            R151GraphUnitTimeHistoryR.main(databaseDir, "D03Q01"); 
            R151GraphUnitTimeHistoryR.main(databaseDir, "D01Q09");
            R151GraphUnitTimeHistoryR.main(databaseDir, "D01Q11");
            R151GraphUnitTimeHistoryR.main(databaseDir, "D02Q05");

        } catch (IOException ex) {
            Logger.getLogger(R153SetRtimeToDatabaseQ.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void main(Path databaseDir, String testname, LocalDateTime startTime, int i) throws SQLException {
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        Statement st = con.createStatement();
        LocalDateTime endTime = startTime.plusSeconds(i);
        ZoneId zone = ZoneId.systemDefault();
        long startTimeMillis = startTime.atZone(zone).toInstant().toEpochMilli();
        long endTimeMillis = endTime.atZone(zone).toInstant().toEpochMilli();
        st.executeUpdate("update \"R150Duration\" "
                + "set "
                + "STARTTIME='" + startTime.toString() + "',"
                + "STARTTIMEMILLIS=" + startTimeMillis + ","
                + "ENDTIME='" + endTime.toString() + "',"
                + "ENDTIMEMILLIS=" + endTimeMillis
                + " where TYPE='R'");

    }

}
