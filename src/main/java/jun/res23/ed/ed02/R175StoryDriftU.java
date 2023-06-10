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
 * R175 : R170からコピーして作成したが、k1,k2 と、 k3,4,5,6, で向きが異なることを考慮してなかったので、 R175から修正。
 * @author jun
 */
public class R175StoryDriftU {
    private static final Logger logger = Logger.getLogger(R175StoryDriftU.class.getName());
    public static void main(String[] args) {
        try {
            Path databaseDir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed02/R140DatabaseQ");
            String inputSchema="R155FourierU";
            String outputTable="R175StoryDriftU";
            main(databaseDir, "D01Q01",inputSchema, outputTable);
            main(databaseDir, "D01Q02",  inputSchema, outputTable);
            main(databaseDir, "D01Q03",  inputSchema, outputTable);
            main(databaseDir, "D01Q04",  inputSchema, outputTable);
            main(databaseDir, "D01Q05",  inputSchema, outputTable);
            main(databaseDir, "D01Q06",  inputSchema, outputTable);
            main(databaseDir, "D01Q08",  inputSchema, outputTable);
            main(databaseDir, "D01Q09",  inputSchema, outputTable);
            main(databaseDir, "D01Q10",  inputSchema, outputTable);
            main(databaseDir, "D01Q11",  inputSchema, outputTable);
            main(databaseDir, "D02Q01",  inputSchema, outputTable);
            main(databaseDir, "D02Q02",  inputSchema, outputTable);
            main(databaseDir, "D02Q03",  inputSchema, outputTable);
            main(databaseDir, "D02Q05",  inputSchema, outputTable);
            main(databaseDir, "D02Q06",  inputSchema, outputTable);
            main(databaseDir, "D02Q07",  inputSchema, outputTable);
            main(databaseDir, "D02Q08",  inputSchema, outputTable);
            main(databaseDir, "D03Q01",  inputSchema, outputTable);
            main(databaseDir, "D03Q02",  inputSchema, outputTable);
            main(databaseDir, "D03Q03",  inputSchema, outputTable);
            main(databaseDir, "D03Q04",  inputSchema, outputTable);
            main(databaseDir, "D03Q05",  inputSchema, outputTable);
            main(databaseDir, "D03Q06",  inputSchema, outputTable);
            main(databaseDir, "D03Q08",  inputSchema, outputTable);
            main(databaseDir, "D03Q09",  inputSchema, outputTable);
        } catch (SQLException ex) {
            Logger.getLogger(R175StoryDriftU.class.getName()).log(Level.SEVERE, null, ex);
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

        st.executeUpdate("drop table if exists \"" + tablename + "\"");
        st.executeUpdate("create table \"" + tablename + "\" (\"Freq[Hz]\" double, \"RelAmpEW[gal*s]\" double, \"RelPhaseEW[rad]\" double,\"RelAmpNS[gal*s]\" double,"
                + " \"RelPhaseNS[rad]\" double)");
        String sql = "insert into \"" + tablename + "\" (\"Freq[Hz]\", \"RelAmpEW[gal*s]\", \"RelPhaseEW[rad]\",\"RelAmpNS[gal*s]\", \"RelPhaseNS[rad]\")"
                + " values (?,?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(sql);

        for (int i = 0; i < k01array[0].length; i++) {
            Complex k01x = ComplexUtils.polar2Complex(k01array[1][i], k01array[2][i]);
            Complex k02x = ComplexUtils.polar2Complex(k02array[1][i], k02array[2][i]);
            Complex k03x = ComplexUtils.polar2Complex(k03array[1][i], k03array[2][i]);
            Complex k04x = ComplexUtils.polar2Complex(k04array[1][i], k04array[2][i]);
            Complex diffx = k01x.add(k02x).add(k03x).add(k04x); // ( (1+2) - (3+4) ) * 0.5 としている。すなわち、2層分の変位である。ちなみにX方向は逆向きなのでsubtractじゃなくて、addになってる。
            diffx = diffx.multiply(-0.5); // ((-k01x-k02x) - (k03x+k04x)) * 0.5 これで EW方向が正になる。
            Complex k01y = ComplexUtils.polar2Complex(k01array[3][i], k01array[4][i]);
            Complex k02y = ComplexUtils.polar2Complex(k02array[3][i], k02array[4][i]);
            Complex k03y = ComplexUtils.polar2Complex(k03array[3][i], k03array[4][i]);
            Complex k04y = ComplexUtils.polar2Complex(k04array[3][i], k04array[4][i]);
            Complex diffy = k01y.add(k02y).subtract(k03y).subtract(k04y);
            diffy = diffy.multiply(0.5); // ((k01y+k02y)-(k03y+k04y))*0.5 これで NS方向が正。

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
