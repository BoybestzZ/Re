/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed14分析UF;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.chart.JunXYChartCreator2;
import jun.data.ResultSetUtils;
import jun.fourier.FourierTransformV2;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class A200DispSpecCompare {

    private static final Logger logger = Logger.getLogger(A200DispSpecCompare.class.getName());
    static final String databaseQdir = "/home/jun/Dropbox (SSLUoT)/res23/ed/ed02/R140DatabaseQ";
    static final String nieddb = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res23/ed/ed08防災科研/res22ed08";
    static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed14分析UF/A200DispSpecCompare");

    public static void main(String[] args) {
        EdefenseKasinInfo[] tests = EdefenseInfo.alltests;
        for (EdefenseKasinInfo t : tests) {
            main(t, "NS", "X");
            main(t, "EW", "Y");
        }
    }

    public static void main(EdefenseKasinInfo test, String ns, String x) {
        try {
            String testname = test.getName(); // "D01Q08";
            String dbq = "jdbc:h2:tcp://localhost/" + databaseQdir + "/" + testname + "q";
            String storyDrift = "R175StoryDriftU";
            Connection con = DriverManager.getConnection(dbq, "junapp", "");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select \"Freq[Hz]\", \"RelAmp"+ns+"[gal*s]\" from \"" + storyDrift + "\"");

            double[][] ar = ResultSetUtils.createSeriesArray(rs);
            for (int i = 0; i < ar[0].length; i++) {
                double freq = ar[0][i]; //Hz
                double omega = 2 * Math.PI * freq;
                double acc = ar[1][i];
                double disp = acc / omega / omega * 10 / 2.0; // cm → mm 、かつ２層の半分に。
                ar[1][i] = disp;
            }

            DefaultXYDataset dataset = new DefaultXYDataset();
            dataset.addSeries("raspi", ar);

            rs = st.executeQuery("select STARTTIMEMILLIS from \"B300Duration\" where TYPE='N'");
            rs.next();
            long startTimeMillisN = rs.getLong(1);
            rs = st.executeQuery("select STARTTIMEMILLIS, ENDTIMEMILLIS from \"B300Duration\" where TYPE='U'");
            rs.next();
            long startTimeMillisU = rs.getLong(1);
            long endTimeMillisU = rs.getLong(2);

            double startSec = (startTimeMillisU - startTimeMillisN) / 1000.0;
            double durationSec = (endTimeMillisU - startTimeMillisU) / 1000.0;

            con.close();
            con = DriverManager.getConnection(nieddb, "junapp", "");
            st = con.createStatement();
            String sql;
            rs = st.executeQuery(sql = "select \"Time[s]\",\"StoryDispW_"+x+"[mm]\" from \"" + testname + "_isd2\" where \"Time[s]\" between " + startSec + " and " + (startSec + durationSec) + " order by 1");
            ar = ResultSetUtils.createSeriesArray(rs);
            logger.log(Level.INFO, sql);
            double f0 = 0.1;
            double f1 = 10.0;
            double df = 0.01;

            FourierTransformV2 ft = new FourierTransformV2(0.001, ar[1]);
            double[][] spec = ft.getAmplitudeSpectrum(f0, f1, df);

            dataset.addSeries("nied", new double[][]{spec[0], spec[1]});

            JFreeChart chart = new JunXYChartCreator2().setDataset(dataset)
                    .setDomainAxisRange(0, 5)
                    .setRangeAxisLabel("Spectral Disp[mm*s]")
                    .setDomainAxisLabel("Freq [Hz]")
                    .create();
            if (svgdir != null) {
                try {
                    JunChartUtil.svg(svgdir.resolve(testname + "_"+ns+".svg"), 500, 250, chart);
                } catch (IOException ex) {
                    Logger.getLogger(A200DispSpecCompare.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JunChartUtil.show(chart);
            }
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A200DispSpecCompare.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
