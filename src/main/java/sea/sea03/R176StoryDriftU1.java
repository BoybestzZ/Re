/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea03;

import jun.res23.ed.ed02.*;
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
 * R175 : R170からコピーして作成したが、k1,k2 と、 k3,4,5,6, で向きが異なることを考慮してなかったので、 R175から修正。
 * R175->R176 Calculate story drift 2F and 3F separately.
 *
 * @author jun
 */
public class R176StoryDriftU1 {

    private static final Logger logger = Logger.getLogger(R176StoryDriftU1.class.getName());

    public static void main(String[] args) {
        try {
            Path databaseDir = Path.of("C:\\Users\\75496\\Documents\\E-Defense\\R140DatabaseQ");
            String inputSchema = "R155FourierU";
            String outputTable = "R176StoryDriftU";
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
            Logger.getLogger(R176StoryDriftU1.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void main(Path databaseDir, String testname, String schema, String tablename) throws SQLException {
        ZoneId zone = ZoneId.systemDefault();
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        logger.log(Level.INFO, testname);

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
        rs = st.executeQuery("select \"Freq[Hz]\",\"AmpX[gal*s]\",\"PhaseX[rad]\",\"AmpY[gal*s]\",\"PhaseY[rad]\" from \"" + schema + "\".\"k05/01\" order by 1");
        double[][] k05array = ResultSetUtils.createSeriesArray(rs);
        rs = st.executeQuery("select \"Freq[Hz]\",\"AmpX[gal*s]\",\"PhaseX[rad]\",\"AmpY[gal*s]\",\"PhaseY[rad]\" from \"" + schema + "\".\"k06/01\" order by 1");
        double[][] k06array = ResultSetUtils.createSeriesArray(rs);

        st.executeUpdate("drop table if exists \"" + tablename + "\"");
        st.executeUpdate("create table \"" + tablename + "\" (\"Freq[Hz]\" real, "
                + "\"RelAmpEW[gal*s]\" real, \"RelPhaseEW[rad]\" real,"
                + "\"RelAmpNS[gal*s]\" real, \"RelPhaseNS[rad]\" real,"
                + "\"RelAmp2EW[gal*s]\" real, \"RelPhase2EW[rad]\" real,"
                + "\"RelAmp2NS[gal*s]\" real, \"RelPhase2NS[rad]\" real,"
                + "\"RelAmp3EW[gal*s]\" real, \"RelPhase3EW[rad]\" real,"
                + "\"RelAmp3NS[gal*s]\" real, \"RelPhase3NS[rad]\" real)"
        );
        String sql = "insert into \"" + tablename + "\" (\"Freq[Hz]\", "
                + "\"RelAmpEW[gal*s]\", \"RelPhaseEW[rad]\","
                + "\"RelAmpNS[gal*s]\", \"RelPhaseNS[rad]\","
                + "\"RelAmp2EW[gal*s]\", \"RelPhase2EW[rad]\","
                + "\"RelAmp2NS[gal*s]\", \"RelPhase2NS[rad]\","
                + "\"RelAmp3EW[gal*s]\", \"RelPhase3EW[rad]\","
                + "\"RelAmp3NS[gal*s]\", \"RelPhase3NS[rad]\")"
                + " values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(sql);

        for (int i = 0; i < k01array[0].length; i++) {
            Complex k01x = ComplexUtils.polar2Complex(k01array[1][i], k01array[2][i]);
            Complex k02x = ComplexUtils.polar2Complex(k02array[1][i], k02array[2][i]);
            Complex k03x = ComplexUtils.polar2Complex(k03array[1][i], k03array[2][i]);
            Complex k04x = ComplexUtils.polar2Complex(k04array[1][i], k04array[2][i]);
            Complex k05x = ComplexUtils.polar2Complex(k05array[1][i], k05array[2][i]);
            Complex k06x = ComplexUtils.polar2Complex(k06array[1][i], k06array[2][i]);
            Complex diffx = k01x.add(k02x).add(k03x).add(k04x); // ( (1+2) - (3+4) ) * 0.5 としている。すなわち、2層分の変位である。ちなみにX方向は逆向きなのでsubtractじゃなくて、addになってる。
            diffx = diffx.multiply(-0.5); // ((-k01x-k02x) - (k03x+k04x)) * 0.5 これで EW方向が正になる。
            Complex diff2x = (k05x.add(k06x)).subtract(k03x.add(k04x));
            diff2x = diff2x.multiply(0.5);
            Complex diff3x = k01x.add(k02x).add(k05x).add(k06x);
            diff3x = diff3x.multiply(-0.5);
            Complex k01y = ComplexUtils.polar2Complex(k01array[3][i], k01array[4][i]);
            Complex k02y = ComplexUtils.polar2Complex(k02array[3][i], k02array[4][i]);
            Complex k03y = ComplexUtils.polar2Complex(k03array[3][i], k03array[4][i]);
            Complex k04y = ComplexUtils.polar2Complex(k04array[3][i], k04array[4][i]);
            Complex k05y = ComplexUtils.polar2Complex(k05array[3][i], k05array[4][i]);
            Complex k06y = ComplexUtils.polar2Complex(k06array[3][i], k06array[4][i]);
            Complex diffy = k01y.add(k02y).subtract(k03y).subtract(k04y);
            diffy = diffy.multiply(0.5); // ((k01y+k02y)-(k03y+k04y))*0.5 これで NS方向が正。
            Complex diff2y = k05y.add(k06y).subtract(k03y).subtract(k04y);
            diff2y = diff2y.multiply(0.5);
            Complex diff3y = k01y.add(k02y).subtract(k05y).subtract(k06y);
            diff3y = diff3y.multiply(0.5);

            ps.setDouble(1, k01array[0][i]);
            ps.setDouble(2, diffx.abs());
            ps.setDouble(3, diffx.getArgument());
            ps.setDouble(4, diffy.abs());
            ps.setDouble(5, diffy.getArgument());
            ps.setDouble(6, diff2x.abs());
            ps.setDouble(7, diff2x.getArgument());
            ps.setDouble(8, diff2y.abs());
            ps.setDouble(9, diff2y.getArgument());
            ps.setDouble(10, diff3x.abs());
            ps.setDouble(11, diff3x.getArgument());
            ps.setDouble(12, diff3y.abs());
            ps.setDouble(13, diff3y.getArgument());
            ps.executeUpdate();

        }

        con.close();

    }

}
