/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed02;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.data.ResultSetUtils;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;

/**
 *
 * @author jun
 */
public class R160StoryDriftS {

    public static void main(String[] args) {
        try {
            Path databaseDir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R140DatabaseQ");
            String inputSchema = "R152FourierS";
            String outputTable = "R160StoryDriftS";
            main(databaseDir, "D01Q01", inputSchema, outputTable);
            main(databaseDir, "D01Q02", inputSchema, outputTable);
            main(databaseDir, "D01Q03", inputSchema, outputTable);
            main(databaseDir, "D01Q04", inputSchema, outputTable);
            main(databaseDir, "D01Q05", inputSchema, outputTable);
            main(databaseDir, "D01Q06", inputSchema, outputTable);
            main(databaseDir, "D01Q08", inputSchema, outputTable);
            main(databaseDir, "D01Q09", inputSchema, outputTable);
            main(databaseDir, "D01Q10", inputSchema, outputTable);
            main(databaseDir, "D01Q11", inputSchema, outputTable);
            main(databaseDir, "D02Q01", inputSchema, outputTable);
            main(databaseDir, "D02Q02", inputSchema, outputTable);
            main(databaseDir, "D02Q03", inputSchema, outputTable);
            main(databaseDir, "D02Q05", inputSchema, outputTable);
            main(databaseDir, "D02Q06", inputSchema, outputTable);
            main(databaseDir, "D02Q07", inputSchema, outputTable);
            main(databaseDir, "D02Q08", inputSchema, outputTable);
            main(databaseDir, "D03Q01", inputSchema, outputTable);
            main(databaseDir, "D03Q02", inputSchema, outputTable);
            main(databaseDir, "D03Q03", inputSchema, outputTable);
            main(databaseDir, "D03Q04", inputSchema, outputTable);
            main(databaseDir, "D03Q05", inputSchema, outputTable);
            main(databaseDir, "D03Q06", inputSchema, outputTable);
            main(databaseDir, "D03Q08", inputSchema, outputTable);
            main(databaseDir, "D03Q09", inputSchema, outputTable);

        } catch (SQLException ex) {
            Logger.getLogger(R160StoryDriftS.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void main(Path databaseDir, String testname, String schema, String tablename) throws SQLException {
        ZoneId zone = ZoneId.systemDefault();
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");

        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery("select \"Freq[Hz]\",\"AmpX[gal*s]\",\"PhaseX[rad]\",\"AmpY[gal*s]\",\"PhaseY[rad]\" from \"" + schema + "\".\"k01/01\" order by 1");
        double[][] k01array = ResultSetUtils.createSeriesArray(rs);
        rs = st.executeQuery("select \"Freq[Hz]\",\"AmpX[gal*s]\",\"PhaseX[rad]\",\"AmpY[gal*s]\",\"PhaseY[rad]\" from \"" + schema + "\".\"k02/01\" order by 1");
        double[][] k02array = ResultSetUtils.createSeriesArray(rs);
        rs = st.executeQuery("select \"Freq[Hz]\",\"AmpX[gal*s]\",\"PhaseX[rad]\",\"AmpY[gal*s]\",\"PhaseY[rad]\" from \"" + schema + "\".\"k03/01\" order by 1");
        double[][] k03array = ResultSetUtils.createSeriesArray(rs);
        rs = st.executeQuery("select \"Freq[Hz]\",\"AmpX[gal*s]\",\"PhaseX[rad]\",\"AmpY[gal*s]\",\"PhaseY[rad]\" from \"" + schema + "\".\"k04/01\" order by 1");
        double[][] k04array = ResultSetUtils.createSeriesArray(rs);

        st.executeUpdate("drop table if exists \"" + tablename + "\"");
        st.executeUpdate("create table \"" + tablename + "\" (\"Freq[Hz]\" double, \"RelAmpX[gal*s]\" double, \"RelPhaseX[rad]\" double,\"RelAmpY[gal*s]\" double,"
                + " \"RelPhaseY[rad]\" double)");
        String sql = "insert into \"" + tablename + "\" (\"Freq[Hz]\", \"RelAmpX[gal*s]\", \"RelPhaseX[rad]\",\"RelAmpY[gal*s]\", \"RelPhaseY[rad]\")"
                + " values (?,?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(sql);

        for (int i = 0; i < k01array[0].length; i++) {
            Complex k01x = ComplexUtils.polar2Complex(k01array[1][i], k01array[2][i]);
            Complex k02x = ComplexUtils.polar2Complex(k02array[1][i], k02array[2][i]);
            Complex k03x = ComplexUtils.polar2Complex(k03array[1][i], k03array[2][i]);
            Complex k04x = ComplexUtils.polar2Complex(k04array[1][i], k04array[2][i]);
            Complex diffx = k01x.add(k02x).subtract(k03x).subtract(k04x);
            diffx = diffx.multiply(0.5);
            Complex k01y = ComplexUtils.polar2Complex(k01array[3][i], k01array[4][i]);
            Complex k02y = ComplexUtils.polar2Complex(k02array[3][i], k02array[4][i]);
            Complex k03y = ComplexUtils.polar2Complex(k03array[3][i], k03array[4][i]);
            Complex k04y = ComplexUtils.polar2Complex(k04array[3][i], k04array[4][i]);
            Complex diffy = k01y.add(k02y).subtract(k03y).subtract(k04y);
            diffy = diffy.multiply(0.5);

            ps.setDouble(1, k01array[0][i]);
            ps.setDouble(2, diffx.abs());
            ps.setDouble(3, diffx.getArgument());
            ps.setDouble(4, diffy.abs());
            ps.setDouble(5, diffy.getArgument());
            ps.executeUpdate();

        }

        con.close();

    }

}
