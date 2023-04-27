/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jun.res23.ed.ed02;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.data.ResultSetUtils;
import jun.raspi.alive.UnitInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.StrainGaugeInfo;

/**
 *
 * @author jun
 */
public class R200Resample {

    private static final Logger logger = Logger.getLogger(R200Resample.class.getName());
    public static Path databaseDir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R140DatabaseQ");

    public static void main(String[] args) {

  //      main("D01Q01", 15, 12, 34, 45);
        main("D01Q02", 15, 12, 53, 0);
        main("D01Q03", 15, 13, 9, 45);
        main("D01Q04", 15, 13, 26, 10);
        main("D01Q05", 15, 13, 37, 0);
        main("D01Q06", 15, 13, 49, 0);
        main("D01Q08", 15, 14, 0, 40);
        main("D01Q09", 15, 14, 11, 40);
        main("D01Q10", 15, 16, 58, 40);
        main("D01Q11", 15, 17, 17, 50);
        main("D02Q01", 17, 13, 39, 40);
        main("D02Q02", 17, 13, 51, 55);
        main("D02Q03", 17, 14, 8, 40);
        main("D02Q05", 17, 14, 48, 20);
        main("D02Q06", 17, 15, 3, 50);
        main("D02Q07", 17, 15, 16, 15);
        main("D02Q08", 17, 15, 35, 30);
        main("D03Q01", 24, 13, 37, 30);
        main("D03Q02", 24, 13, 55, 20);
        main("D03Q03", 24, 14, 7, 10);
        main("D03Q04", 24, 14, 22, 30);
        main("D03Q05", 24, 14, 33, 15);
        main("D03Q06", 24, 14, 44, 25);
        main("D03Q08", 24, 15, 38, 20);
        main("D03Q09", 24, 15, 50, 10);

    }

    public static void main(String testname, int day, int hour, int minute, int second) {
//        String testname = EdefenseInfo.testnames[0];
        ZoneId zone = ZoneId.systemDefault();
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        try {
            // OpenDatabase
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            ZonedDateTime startTime = ZonedDateTime.of(2023, 2, day, hour, minute, second, 0, zone);
            long startTimeMillis = startTime.toEpochSecond() * 1000;
            ResultSet rs = st.executeQuery("SELECT ENDTIMEMILLIS FROM \"R150Duration\" where TYPE='Q'");
            rs.next();
            long endTimeMillis = rs.getLong("ENDTIMEMILLIS");
            int dtMillis = 20;
            int n = (int) ((endTimeMillis - startTimeMillis) / dtMillis); // データの個数

            // すべてのstr01を探す。_ 79278
            DatabaseMetaData md = con.getMetaData();

            ResultSet rsmd = md.getTables(null, null, "%/str01", null);
            int strainGaugeCounter = 0;
            while (rsmd.next()) {
                String tablename = rsmd.getString("TABLE_NAME");
                String macaddresss = tablename.substring(0, 17);
                UnitInfo unit = EdefenseInfo.lookForUnit(macaddresss);
                int ch = Integer.parseInt(tablename.substring(19, 20));
                StrainGaugeInfo gi = StrainGaugeInfo.findStrainGaugeInfo(unit, ch);

                String sql = "select \"T[ms]\", \"STRAIN[LSB]\" from \"" + tablename + "\" where \"T[ms]\" >= " + (startTimeMillis - 100);

                double factor = Double.NaN;

                if (gi == null) {
                    logger.log(Level.INFO, "NotFound unit=" + unit.getName() + ", chno=" + ch);
                    continue;
                } else {
                    factor = 1.0 / gi.getGain() / Math.pow(2, 24) * 4 / gi.getGaugeFactor() * 1e6; // すみません、圧縮を正としたままとなります。
                }
                logger.log(Level.INFO, (++strainGaugeCounter) + ":" + gi.getShortName());

                rs = st.executeQuery(sql);
                double[][] array = ResultSetUtils.createSeriesArray(rs);
                long nextTimeMillis = startTimeMillis;

                double[] result = new double[n];
                int ncount = 0;
                for (int i = 0; i < array[0].length; i++) {
                    if (array[0][i] >= nextTimeMillis) {
                        double r0 = (array[0][i] - nextTimeMillis) / (array[0][i] - array[0][i - 1]);
                        double r1 = (nextTimeMillis - array[0][i - 1]) / (array[0][i] - array[0][i - 1]);
                        result[ncount++] = factor * (r0 * array[1][i - 1] + r1 * array[1][i]);
                        if (ncount == result.length) {
                            break;
                        }
                        nextTimeMillis += dtMillis;
                    }
                }
                long timeMillis = startTimeMillis;
                String valueunit = "με";
                String outputTable = "\"R200Resample\".\"" + gi.getShortName() + "\"";
                st.executeUpdate("create schema if not exists \"R200Resample\"");
                st.executeUpdate("drop table if exists " + outputTable);
                st.executeUpdate("create table " + outputTable + "(\"TIME[s]\" double, \"T[ms]\" long, \"Strain[" + valueunit + "]\" double)");
                for (int i = 0; i < ncount; i++) {
                    double time = i * dtMillis / 1000.0;
                    st.executeUpdate("insert into " + outputTable + " (\"TIME[s]\", \"T[ms]\", \"Strain[" + valueunit + "]\") values (" + time + "," + timeMillis + "," + result[i] + ")");
                    timeMillis += dtMillis;
                }

            }

            rsmd = md.getTables(null, null, "%/acc02", null);
            while (rsmd.next()) {
                String tablename = rsmd.getString("TABLE_NAME");
                String macaddresss = tablename.substring(0, 17);
                UnitInfo unit = EdefenseInfo.lookForUnit(macaddresss);
                logger.log(Level.INFO, unit.getName());

                String sql = "select \"T[ms]\", \"X[gal]\",\"Y[gal]\",\"Z[gal]\" from \"" + tablename + "\" where \"T[ms]\" >= " + (startTimeMillis - 100);

                rs = st.executeQuery(sql);
                double[][] array = ResultSetUtils.createSeriesArray(rs);
                long nextTimeMillis = startTimeMillis;

                double[][] result = new double[3][n];
                int ncount = 0;
                for (int i = 0; i < array[0].length; i++) {
                    if (array[0][i] >= nextTimeMillis) {
                        double r0 = (array[0][i] - nextTimeMillis) / (array[0][i] - array[0][i - 1]);
                        double r1 = (nextTimeMillis - array[0][i - 1]) / (array[0][i] - array[0][i - 1]);
                        for (int j = 0; j < 3; j++) {
                            result[j][ncount] = (r0 * array[1 + j][i - 1] + r1 * array[1 + j][i]);
                        }
                        ncount++;
                        if (ncount == result[0].length) {
                            break;
                        }
                        nextTimeMillis += dtMillis;
                    }
                }
                long timeMillis = startTimeMillis;
                String valueunit = "με";
                String outputTable = "\"R200Resample\".\"" + unit.getName() + "/01\"";
                st.executeUpdate("create schema if not exists \"R200Resample\"");
                st.executeUpdate("drop table if exists " + outputTable);
                st.executeUpdate("create table " + outputTable + "(\"TIME[s]\" double, \"T[ms]\" long, \"X[gal]\" double, \"Y[gal]\" double,\"Z[gal]\" double)");
                for (int i = 0; i < ncount; i++) {
                    double time = i * dtMillis / 1000.0;
                    st.executeUpdate("insert into " + outputTable + " (\"TIME[s]\", \"T[ms]\", \"X[gal]\" , \"Y[gal]\" ,\"Z[gal]\" )"
                            + "values (" + time + "," + timeMillis + "," + result[0][i] + "," + result[1][i] + "," + result[2][i] + ")");
                    timeMillis += dtMillis;
                }

            }

            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(R200Resample.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
