/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jun.res23.ed.ed02;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.data.ResultSetUtils;
import jun.fourier.FFTv2;
import jun.fourier.FourierTransformV2;

/**
 * wk35から移植。 加速度歪データをまずスペクトルSPECにしてから、その比率を撮って MNQSPECｗ作らなければならない。 →
 * cb10.P031から移植。e-defense向け。データベース読み込みは何くんのPCでやった。
 * このプログラムではまず、データベースに読み込まれたデータのフーリエ変換をすべて行う。
 * cb10ではピーク選択やMNQ計算までやっているっぽいが、ed.ed01.Q031ではピーク選択関係はすべて削除してある。
 *
 * @author jun
 */
public class Q031CreateSpec {

    private static final Logger log = Logger.getLogger(Q031CreateSpec.class.getName());
    public static final String specSchema = "Q031SPEC";

    public static void main(String args[]) {

        try {
            String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215Day1/database_HE/D01Q01_20230215_123406";

            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            // 全スペクトルを算出する。SPECスキーマ ======================================================
//            st.executeUpdate("drop schema if exists \"" + specSchema + "\"");            
            st.executeUpdate("create schema if not exists \"" + specSchema + "\"");
            Q031CreateSpec.calculateAll(dburl, "PUBLIC", /*startTime*/ null, /*endTime*/ null, specSchema, 0.10, 10.00, 0.005);
        } catch (SQLException ex) {
            Logger.getLogger(Q031CreateSpec.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * いまのデータベースのstr01とacc02をすべてスペクトル計算する。
     *
     * @param dburl
     * @param startTime
     * @param endTime
     * @throws SQLException
     */
    public static void calculateAll(String dburl, String inputSchema, ZonedDateTime startTime, ZonedDateTime endTime, String outputSchema, double f0, double f1, double df) throws SQLException {
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        DatabaseMetaData dbmd = con.getMetaData();
        // まずは str01をすべて計算する。
        {
        ResultSet rs = dbmd.getTables(null, inputSchema, "%/str01", null);
        while (rs.next()) {
            String inputTable = rs.getString("TABLE_NAME");
            log.log(Level.INFO, inputTable);
            calculateSingleStr(con, inputSchema, inputTable, startTime, endTime, outputSchema, f0, f1, df);
        }
        }
        {
            ResultSet rs = dbmd.getTables(null, null, "%/acc02", null);
            while (rs.next()) {
                String inputTable = rs.getString("TABLE_NAME");
                calculateSingleAcc(con, inputSchema, inputTable, startTime, endTime, outputSchema, f0, f1, df);
            }
            rs.close();

        }
        con.close();

    
    }
    public static void calculateSingleStr(Connection con, String inputSchema, String inputTable, ZonedDateTime startTime, ZonedDateTime endTime, String outputSchema,
            double f0, double f1, double df) {

        String where = "";
        if (startTime != null) {
            if (endTime != null) {
                long startTimeMillis = startTime.toEpochSecond() * 1000;
                long endTimeMillis = endTime.toEpochSecond() * 1000;
                where = " where \"T[mc]\" between " + startTimeMillis + " and " + endTimeMillis;
            } else {
                long startTimeMillis = startTime.toEpochSecond() * 1000;
                where = " where \"T[mc]\" >= " + startTimeMillis;
            }
        } else {
            if (endTime != null) {
                long endTimeMillis = endTime.toEpochSecond() * 1000;
                where = " where \"T[mc]\" <= " + endTimeMillis;
            }
        }

        try {
            Statement st = con.createStatement();
            String sql;
            ResultSet rs = st.executeQuery(sql = "select \"T[ms]\" , \"STRAIN[LSB]\" from \"" + inputSchema + "\".\"" + inputTable + "\" "
                    + where + " order by \"T[ms]\" ");
            double[][] ar = ResultSetUtils.createSeriesArray(rs);
            double dt;
            try {
                dt = (ar[0][100] - ar[0][0]) / 100.0 / 1000.0; // [sec]
            } catch (ArrayIndexOutOfBoundsException aee) {
                Logger.getLogger(Q031CreateSpec.class.getName()).log(Level.SEVERE, sql, aee);
                throw aee;
            }

            FourierTransformV2 ft = new FourierTransformV2(dt, FFTv2.zeroAverage(ar[1]));
            double[][] fta = ft.getAmplitudeSpectrum(f0, f1, df, /*coeff*/ false, /*phase*/ true);
            st.executeUpdate("drop  table if exists \"" + outputSchema + "\".\"" + inputTable + "\"");
            st.executeUpdate("create table \"" + outputSchema + "\".\"" + inputTable + "\" (_NO identity, \"FREQ[Hz]\" double, \"AMP[LSB*s]\" double, \"PHASE[rad]\" double)");
            PreparedStatement ps = con.prepareStatement("insert into \"" + outputSchema + "\".\"" + inputTable + "\" "
                    + "(\"FREQ[Hz]\" , \"AMP[LSB*s]\" , \"PHASE[rad]\" ) values (?,?,?)");

            for (int i = 0; i < fta[0].length; i++) {
                ps.setDouble(1, fta[0][i]); // freq
                ps.setDouble(2, fta[1][i]); // amp
                ps.setDouble(3, fta[2][i]); // phase
                ps.executeUpdate();
            }

            st.close();

        } catch (SQLException ex) {
            Logger.getLogger(Q031CreateSpec.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void calculateSingleAcc(Connection con, String inputSchema, String inputTable, ZonedDateTime startTime, ZonedDateTime endTime, String outputSchema,
            double f0, double f1, double df) {
        String where = "";
        if (startTime != null) {
            if (endTime != null) {
                long startTimeMillis = startTime.toEpochSecond() * 1000;
                long endTimeMillis = endTime.toEpochSecond() * 1000;
                where = " where \"T[mc]\" between " + startTimeMillis + " and " + endTimeMillis;
            } else {
                long startTimeMillis = startTime.toEpochSecond() * 1000;
                where = " where \"T[mc]\" >= " + startTimeMillis;
            }
        } else {
            if (endTime != null) {
                long endTimeMillis = endTime.toEpochSecond() * 1000;
                where = " where \"T[mc]\" <= " + endTimeMillis;
            }
        }

        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select \"T[ms]\" , \"X[gal]\", \"Y[gal]\",\"Z[gal]\" from \"" + inputSchema + "\".\"" + inputTable + "\" "
                    + where + " order by \"T[ms]\" ");
            double[][] ar = ResultSetUtils.createSeriesArray(rs);
            double dt = (ar[0][100] - ar[0][0]) / 100.0 / 1000.0; // [sec]
            FourierTransformV2 ftx = new FourierTransformV2(dt, FFTv2.zeroAverage(ar[1]));
            FourierTransformV2 fty = new FourierTransformV2(dt, FFTv2.zeroAverage(ar[2]));
            FourierTransformV2 ftz = new FourierTransformV2(dt, FFTv2.zeroAverage(ar[3]));
            double[][] ftax = ftx.getAmplitudeSpectrum(f0, f1, df, /*coeff*/ false, /*phase*/ true);
            double[][] ftay = fty.getAmplitudeSpectrum(f0, f1, df, /*coeff*/ false, /*phase*/ true);
            double[][] ftaz = ftz.getAmplitudeSpectrum(f0, f1, df, /*coeff*/ false, /*phase*/ true);
            st.executeUpdate("drop  table if exists \"" + outputSchema + "\".\"" + inputTable + "\"");
            st.executeUpdate("create table \"" + outputSchema + "\".\"" + inputTable + "\" (_NO identity, \"FREQ[Hz]\" double, "
                    + "\"AMPX[gal*s]\" double, \"PHASEX[rad]\" double,"
                    + "\"AMPY[gal*s]\" double, \"PHASEY[rad]\" double,"
                    + "\"AMPZ[gal*s]\" double, \"PHASEZ[rad]\" double"
                    + ")");
            PreparedStatement ps = con.prepareStatement("insert into \"" + outputSchema + "\".\"" + inputTable + "\" (\"FREQ[Hz]\" , "
                    + "\"AMPX[gal*s]\" , \"PHASEX[rad]\","
                    + "\"AMPY[gal*s]\" , \"PHASEY[rad]\","
                    + "\"AMPZ[gal*s]\" , \"PHASEZ[rad]\""
                    + " ) values (?,?,?,?,?,?,?)");

            for (int i = 0; i < ftax[0].length; i++) {
                ps.setDouble(1, ftax[0][i]); // freq
                ps.setDouble(2, ftax[1][i]); // amp
                ps.setDouble(3, ftax[2][i]); // phase
                ps.setDouble(4, ftay[1][i]); // amp
                ps.setDouble(5, ftay[2][i]); // phase
                ps.setDouble(6, ftaz[1][i]); // amp
                ps.setDouble(7, ftaz[2][i]); // phase
                ps.executeUpdate();
            }

            st.close();

        } catch (SQLException ex) {
            Logger.getLogger(Q031CreateSpec.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

}
