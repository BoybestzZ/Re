/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jun.res23.ed.ed02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.raspi.alive.UnitInfo;
import static jun.res23.ed.ed02.R200Resample.databaseDir;
import jun.res23.ed.util.EdefenseInfo;

/**
 *
 * @author jun
 */
public class R210TimeHistoryTcsv {

    private static final Logger logger = Logger.getLogger(R210TimeHistoryTcsv.class.getName());
    public static final Path csvdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R210TimeHistoryTcsv");
    public static final String outputTable = "R210Resample";

    public static void main(String[] args) {

//            main("D01Q01");
//        main("D01Q02");
//        main("D01Q03");
//        main("D01Q04");
//        main("D01Q05");
//        main("D01Q06");
//        main("D01Q08");
//        main("D01Q09");
//        main("D01Q10");
//        main("D01Q11");
//        main("D02Q01");
//        main("D02Q02");
//        main("D02Q03");
//        main("D02Q05");
//        main("D02Q06");
        main("D02Q07");
        main("D02Q08");
        main("D03Q01");
        main("D03Q02");
        main("D03Q03");
        main("D03Q04");
        main("D03Q05");
        main("D03Q06");
        main("D03Q08");
        main("D03Q09");

    }

    public static void main(String testname) {

        ZoneId zone = ZoneId.systemDefault();
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        try {
            // OpenDatabase
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            DatabaseMetaData md = con.getMetaData();
            Statement st = con.createStatement();

            st.executeUpdate("drop table if exists \"" + outputTable + "\"");
            st.executeUpdate("create table  \"" + outputTable + "\" (\"TIME[s]\" double primary key ,\"T[ms]\" long ,\"TIME\" timestamp )");
            boolean first = true;
            for (UnitInfo unit : EdefenseInfo.allunits) {
                for (int i = 0; i < 8; i++) {
                    String tablename = unit.getName() + "/0" + (i + 1);
                    ResultSet mdrs = md.getTables(null, "R200Resample", tablename, null);
                    if (!mdrs.next()) {
                        continue; // If no table, skip.
                    }
                    logger.log(Level.INFO, testname + " " + tablename);
                    if (first) {
                        ResultSet rs = st.executeQuery("select \"TIME[s]\",\"T[ms]\" from \"R200Resample\".\"" + tablename + "\"");
                        PreparedStatement ps = con.prepareStatement("insert into \"" + outputTable + "\" (\"TIME[s]\",\"T[ms]\",\"TIME\") values (?,?,?)");
                        while (rs.next()) {
                            double second = rs.getDouble(1);
                            long millis = rs.getLong(2);
                            Timestamp ts = Timestamp.valueOf(Instant.ofEpochMilli(millis).atZone(zone).toLocalDateTime());
                            ps.setDouble(1, second);
                            ps.setLong(2, millis);
                            ps.setTimestamp(3, ts);
                            ps.executeUpdate();
                        }
                        first = false;
                    }

                    mdrs = md.getColumns(null, "R200Resample", tablename, null);
                    while (mdrs.next()) {
                        String columnName = mdrs.getString("COLUMN_NAME");
                        if (columnName.equals("TIME[s]")) {
                            continue;
                        }
                        if (columnName.equals("T[ms]")) {
                            continue;
                        }

                        st.executeUpdate("alter table \"" + outputTable + "\" add column \"" + tablename + "_" + columnName + "\" double ");
                        st.executeUpdate("merge into  \"" + outputTable + "\" (\"TIME[s]\", \"" + tablename + "_" + columnName + "\") key (\"TIME[s]\")"
                                + " select \"TIME[s]\", \"" + columnName + "\" from \"R200Resample\".\"" + tablename + "\""
                                + "");
                    }
                }
            }
            logger.log(Level.INFO, "Creating CSV");
            if (!Files.exists(csvdir)) {
                Files.createDirectory(csvdir);
            }
            st.executeUpdate("call csvwrite('"
                    + csvdir.resolve(outputTable + "_" + testname + ".csv").toString() + "','select * from \"" + outputTable + "\"')");

            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(R210TimeHistoryTcsv.class.getName()).log(Level.SEVERE, "", ex);
        } catch (IOException ex) {
            Logger.getLogger(R210TimeHistoryTcsv.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
