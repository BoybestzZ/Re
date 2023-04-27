/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed02;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunXYChartCreator2;
import jun.data.ResultSetUtils;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * これはひずみを層間変形で除した局所剛性を計算する。ただし、これだとあんまり傾向がよく読み取れな無いので、MとかNとかにしてから局所剛性を計算することとする。
 * @author jun
 */
public class R180Ratio {

    public static Path databaseDir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R140DatabaseQ");

    public static String[] testnames = {"D01Q01", "D01Q02"};///
    //, "D01Q03", "D01Q04", "D01Q05", "D01Q06", "D01Q08", "D01Q09", "D01Q10", "D01Q11",
//        "D02Q01", "D02Q02", "D02Q03", "D02Q05", "D02Q06", "D02Q07", "D02Q08",
//        "D03Q01", "D03Q02", "D03Q03", "D03Q04", "D03Q05", "D03Q06", "D03Q08", "D03Q09"
//    };
    private static final Logger logger = Logger.getLogger(R180Ratio.class.getName());

    public static void main(String[] args) {

        try {
            try (Connection con = DriverManager.getConnection("jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R180Ratio", "junapp", ""); Statement st = con.createStatement();) {
                if (true) {
                    st.executeUpdate("drop table if exists \"R180RatioS\"");
                    st.executeUpdate("drop table if exists \"R180RatioR\"");
                    st.executeUpdate("create table \"R180RatioS\" (_NO identity, TESTNAME varchar, \"Freq[Hz]\" double , AMP double , PHASE double, REAL double, IMAG double)");
                    st.executeUpdate("create table \"R180RatioR\" (_NO identity, TESTNAME varchar, \"Freq[Hz]\" double , AMP double , PHASE double, REAL double, IMAG double)");

                    // とりあえず短辺のみ。
                    for (String testname : testnames) {
                        {
                            double freq = getPeakFreq(testname, "R152FourierS", "g02/01", "Amp[με*s]");
                            Complex numerValue = get(testname, "R152FourierS", "g02/01", "Amp[με*s]", "Phase[rad]", freq);
                            Complex denomValue = get(testname, "PUBLIC", "R160StoryDriftS", "RelAmpX[gal*s]", "RelPhaseX[rad]", freq);

                            logger.log(Level.INFO, testname + " " + freq + "(Hz)");
                            Complex ratio = numerValue.divide(denomValue);

                            st.executeUpdate("insert into \"R180RatioS\" (TESTNAME,\"Freq[Hz]\", AMP,PHASE,REAL,IMAG) values ("
                                    + "'" + testname + "s'," + freq + "," + ratio.abs() + "," + ratio.getArgument() + "," + ratio.getReal() + "," + ratio.getImaginary() + ")");

                        }

                        { // 
                            double freq = getPeakFreq(testname, "R151FourierR", "g02/01", "Amp[με*s]");
                            Complex numerValue = get(testname, "R151FourierR", "g02/01", "Amp[με*s]", "Phase[rad]", freq);
                            Complex denomValue = get(testname, "PUBLIC", "R170StoryDriftR", "RelAmpX[gal*s]", "RelPhaseX[rad]", freq);

                            logger.log(Level.INFO, testname + " " + freq + "(Hz)");
                            Complex ratio = numerValue.divide(denomValue);

                            st.executeUpdate("insert into \"R180RatioR\" (TESTNAME,\"Freq[Hz]\", AMP,PHASE,REAL,IMAG) values ("
                                    + "'" + testname + "r'," + freq + "," + ratio.abs() + "," + ratio.getArgument() + "," + ratio.getReal() + "," + ratio.getImaginary() + ")");

                        }
                    }
                }
                ResultSet rs = st.executeQuery("select _NO, \"Freq[Hz]\", AMP from \"R180RatioR\"");
                double[][] array = ResultSetUtils.createSeriesArray(rs);

                DefaultXYDataset datasetRatio = new DefaultXYDataset();
                DefaultXYDataset datasetFreq = new DefaultXYDataset();
                datasetFreq.addSeries("R", new double[][]{array[0], array[1]});
                datasetRatio.addSeries("R", new double[][]{array[0], array[2]});

                rs = st.executeQuery("select _NO, \"Freq[Hz]\", AMP from \"R180RatioS\"");
                array = ResultSetUtils.createSeriesArray(rs);

                datasetFreq.addSeries("S", new double[][]{array[0], array[1]});
                datasetRatio.addSeries("S", new double[][]{array[0], array[2]});
                new JunXYChartCreator2().setDataset(datasetRatio).setRangeAxisLabel("Ratio").show();
                new JunXYChartCreator2().setDataset(datasetFreq).setRangeAxisLabel("Freq").show();

            }
        } catch (SQLException ex) {
            Logger.getLogger(R180Ratio.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static Complex get(String testname, String schema, String sensor, String ampcolumn, String phasecolumn, double freq) throws SQLException {
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        try (Connection con = DriverManager.getConnection(dburl, "junapp", "")) {
            double amp;
            double phase;
            try (Statement st = con.createStatement()) {
                String target = "\"" + schema + "\".\"" + sensor + "\"";
                ResultSet rs = st.executeQuery("select \"" + ampcolumn + "\" ,\"" + phasecolumn + "\" from " + target + " where \"Freq[Hz]\" = " + freq);
                rs.next();
                amp = rs.getDouble(1);
                phase = rs.getDouble(2);
            }
            return ComplexUtils.polar2Complex(amp, phase);
        }

    }

    public static double getPeakFreq(String testname, String schema, String sensor, String ampcolumn) throws SQLException {
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        try (Connection con = DriverManager.getConnection(dburl, "junapp", "")) {
            try (Statement st = con.createStatement()) {
                String target = "\"" + schema + "\".\"" + sensor + "\"";
                ResultSet rs = st.executeQuery("select \"" + ampcolumn + "\" ,\"Freq[Hz]\" from " + target + " where \"Freq[Hz]\" < 2.0 order by 1 desc limit 1;");
                rs.next();
//                amp = rs.getDouble(2);
                double freq = rs.getDouble(2);
                return freq;
            }

        }

    }
}
