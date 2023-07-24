/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed14分析UF;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.data.ResultSetUtils;
import jun.fourier.FFTv2;
import jun.fourier.FourierTransformV2;
import jun.fourier.HanningWindow;
import jun.raspi.alive.UnitInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import jun.res23.ed.util.StrainGaugeInfo;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author jun
 */
public class Z101CheckDiff {

    static final String databaseQdir = "/home/jun/Dropbox (SSLUoT)/res23/ed/ed02/R140DatabaseQ";
    private static final Logger logger = Logger.getLogger(Z101CheckDiff.class.getName());

    public static void main(String[] args) {

        try {
            String dburl = "jdbc:h2:tcp://localhost//" + databaseQdir + "/D02Q05q";
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            double[][] ar = diff(EdefenseInfo.k01, EdefenseInfo.k03, st);

            DefaultXYDataset dataset = new DefaultXYDataset();
            dataset.addSeries("diff", new double[][]{ar[0], ar[1]});

            JunChartUtil.show("spec", dataset);
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(Z101CheckDiff.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static double[][] diff(UnitInfo upper, UnitInfo lower, Statement st) throws SQLException {
        String schema = "R155FourierU";

        String upperTable = "\"" + schema + "\".\"" + upper.getName() + "/01\"";
        String lowerTable = "\"" + schema + "\".\"" + lower.getName() + "/01\"";

//            long startTimeMillis = 1676432110000L;
//            long endTimeMillis = 1676432200000L;
        ResultSet rs = st.executeQuery("select \"Freq[Hz]\", \"AmpX[gal*s]\", \"PhaseX[rad]\" from " + upperTable + " order by 1");
        double[][] ua = ResultSetUtils.createSeriesArray(rs);
        rs = st.executeQuery("select \"Freq[Hz]\", \"AmpX[gal*s]\", \"PhaseX[rad]\" from " + lowerTable + " order by 1");
        double[][] la = ResultSetUtils.createSeriesArray(rs);

        int len = ua[0].length;
        Complex[] diffc = new Complex[len];
        for (int i = 0; i < len; i++) {
            Complex uc = ComplexUtils.polar2Complex(ua[1][i], ua[2][i]);
            Complex lc = ComplexUtils.polar2Complex(la[1][i], la[2][i]);
            diffc[i] = uc.add(lc);
        }

        double[] diffamp = new double[len];
        double[] diffphase = new double[len];
        for (int i = 0; i < len; i++) {
            diffamp[i] = diffc[i].abs();
            diffphase[i] = diffc[i].getArgument();
            double freq=ua[0][i];
            double w=2*Math.PI*freq;
            diffamp[i]/=w*w;
        }

        return new double[][]{ua[0], diffamp, diffphase};

    }

}
