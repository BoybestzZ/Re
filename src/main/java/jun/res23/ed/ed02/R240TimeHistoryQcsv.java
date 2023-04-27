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
import jun.res23.ed.util.StrainGaugeInfo;

/**
 *
 * @author jun
 */
public class R240TimeHistoryQcsv {

    private static final Logger logger = Logger.getLogger(R240TimeHistoryQcsv.class.getName());
    public static final Path csvdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R240TimeHistoryQcsv");
    public static final String outputTable = "R240TimeHistoryQ";
    public static final String inputSchema = "PUBLIC";

    public static void main(String[] args) {

            main("D01Q01");
        main("D01Q02");
        main("D01Q03");
        main("D01Q04");
        main("D01Q05");
        main("D01Q06");
        main("D01Q08");
        main("D01Q09");
        main("D01Q10");
        main("D01Q11");
        main("D02Q01");
        main("D02Q02");
        main("D02Q03");
        main("D02Q05");
        main("D02Q06");
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
        String dburl = "jdbc:h2:file://" + databaseDir.resolve(testname + "q");
        try {
            // OpenDatabase
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            DatabaseMetaData md = con.getMetaData();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT  STARTTIMEMILLIS, ENDTIMEMILLIS FROM \"R150Duration\" where TYPE='Q'");
            rs.next();
            long startTimeMillis = rs.getLong(1);
            long endTimeMillis = rs.getLong(2);

            st.executeUpdate("drop table if exists \"" + outputTable + "\"");
            st.executeUpdate("create table  \"" + outputTable + "\" (NO int primary key )");
            boolean first = true;
            int counter = 0;
            for (UnitInfo unit : EdefenseInfo.allunits) {
                for (int i = 0; i < 8; i++) {
                    String tablename = unit.getHardwareAddress() + "/0" + (i + 1);
                    logger.log(Level.INFO, testname + " " + counter + " (" + tablename + ")");
                    ResultSet mdrs = md.getTables(null, inputSchema, tablename + "/str01", null);
                    if (mdrs.next()) {
                        mainStr01(con, md, unit, i + 1, startTimeMillis, endTimeMillis);
                        counter++;
                    } else {
                        mdrs = md.getTables(null, inputSchema, tablename + "/acc02", null);
                        if (mdrs.next()) {
                            mainAcc02(con, md, unit, i + 1, startTimeMillis, endTimeMillis);
                            counter++;
                        } else {
                            continue;
                        }
                    }

//                    if (first) {
//                        ResultSet rs = st.executeQuery("select from \""+inputSchema+"\".\"" + tablename + "\"");
//                        PreparedStatement ps = con.prepareStatement("insert into \"" + outputTable + "\" (\"TIME[s]\",\"T[ms]\",\"TIME\") values (?,?,?)");
//                        while (rs.next()) {
//                            double second = rs.getDouble(1);
//                            long millis = rs.getLong(2);
//                            Timestamp ts = Timestamp.valueOf(Instant.ofEpochMilli(millis).atZone(zone).toLocalDateTime());
//                            ps.setDouble(1, second);
//                            ps.setLong(2, millis);
//                            ps.setTimestamp(3, ts);
//                            ps.executeUpdate();
//                        }
//                        first = false;
//                    }
                }
//                if (counter==1) break;
            }
            logger.log(Level.INFO, "Creating CSV");
            if (!Files.exists(csvdir)) {
                Files.createDirectory(csvdir);
            }
            st.executeUpdate("call csvwrite('"
                    + csvdir.resolve(outputTable + "_" + testname + "q.csv").toString() + "','select * from \"" + outputTable + "\"')");

            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(R240TimeHistoryQcsv.class.getName()).log(Level.SEVERE, "", ex);
        } catch (IOException ex) {
            Logger.getLogger(R240TimeHistoryQcsv.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void mainStr01(Connection con, DatabaseMetaData md, UnitInfo unit, int ch, long startTimeMillis, long endTimeMillis) throws SQLException {
        StrainGaugeInfo gi = StrainGaugeInfo.findStrainGaugeInfo(unit, ch);
        if (gi == null) {
            return;
        }
        String inputTableName = unit.getHardwareAddress() + "/0" + ch + "/str01";
        String shortName = gi.getShortName();
        double factor = 1.0 / gi.getGain() / Math.pow(2, 24) * 4 / gi.getGaugeFactor() * 1e6; // このプロジェクトでは圧縮を正として統一する。
        // T[ms]とSTRAIN[LSB]だけを読む。
        Statement st = con.createStatement();

        st.executeUpdate("alter table \"" + outputTable + "\" add column "
                + "(\"" + shortName + "_Time[s]" + "\" real ,\"" + shortName + "_Strain[με]\" real)");
        PreparedStatement ps = con.prepareStatement("merge into \"" + outputTable + "\" (NO,\"" + shortName + "_Time[s]" + "\"  ,\"" + shortName + "_Strain[με]\") "
                + " key (NO) values (?,?,?)");

        ResultSet rs = st.executeQuery("select \"T[ms]\",\"STRAIN[LSB]\" from \"" + inputTableName + "\" "
                + " where \"T[ms]\" between " + startTimeMillis + " and " + endTimeMillis + " "
                + " order by 1");
        int counter = 0;
        while (rs.next()) {
            double time = 0.001 * (rs.getDouble(1) - startTimeMillis); // ms
            double strainme = rs.getDouble(2) * factor; // με
            ps.setInt(1, counter);
            ps.setDouble(2, time);
            ps.setDouble(3, strainme);
            ps.addBatch();
            counter++;
        }
        ps.executeBatch();
        st.close();
        ps.close();
    }

    private static void mainAcc02(Connection con, DatabaseMetaData md, UnitInfo unit, int ch, long startTimeMillis, long endTimeMillis) throws SQLException {
        String inputTableName = unit.getHardwareAddress() + "/0" + ch + "/acc02";
        String shortName = unit.getName() + "/" + ch;
        // T[ms]とX[gal], Y[gal], Z[gal]を読む。
        Statement st = con.createStatement();

        st.executeUpdate("alter table \"" + outputTable + "\" add column "
                + "(\"" + shortName + "_Time[ms]" + "\" real ,"
                + "\"" + shortName + "_X[gal]\" real,"
                + "\"" + shortName + "_Y[gal]\" real,"
                + "\"" + shortName + "_Z[gal]\" real"
                + ")");
        PreparedStatement ps = con.prepareStatement("merge into \"" + outputTable + "\" "
                + "(NO,\"" + shortName + "_Time[ms]" + "\"  ,\"" + shortName + "_X[gal]\" ,"
                + "\"" + shortName + "_Y[gal]\" ,"
                + "\"" + shortName + "_Z[gal]\")"
                + " key (NO) values (?,?,?,?,?)");

        ResultSet rs = st.executeQuery("select \"T[ms]\",\"X[gal]\",\"Y[gal]\",\"Z[gal]\" from \"" + inputTableName + "\" "
                + " where \"T[ms]\" between " + startTimeMillis + " and " + endTimeMillis + " "
                + " order by 1");
        int counter = 0;
        while (rs.next()) {
            double time = (rs.getDouble(1) - startTimeMillis) * 0.001; // ms
            double galx = rs.getDouble(2); // gal
            double galy = rs.getDouble(3); // gal
            double galz = rs.getDouble(4); // gal

            ps.setInt(1, counter);
            ps.setDouble(2, time);
            ps.setDouble(3, galx);
            ps.setDouble(4, galy);
            ps.setDouble(5, galz);
            ps.addBatch();
            counter++;
        }
        ps.executeBatch();
        st.close();
        ps.close();
    }

}
