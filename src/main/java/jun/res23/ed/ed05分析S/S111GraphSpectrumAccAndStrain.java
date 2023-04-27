/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed05分析S;

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
import jun.fourier.FourierUtils;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class S111GraphSpectrumAccAndStrain {

    private static final Logger logger = Logger.getLogger(S111GraphSpectrumAccAndStrain.class.getName());

    final static String direction = "X"; // or Y
    final static String accName="k03/01";
    final static String gaugeName = "g02/01"; // or a03/01
//     final static String storyDriftTable = "R160StoryDriftS";

    public static void main(String[] args) {
        try {
            final EdefenseKasinInfo testname = EdefenseInfo.D01Q06s;
            final String dburl = "jdbc:h2:tcp://localhost/" + S100SectionNM.inputDatabaseDir.resolve(testname.getTestName()+"q");


            logger.log(Level.INFO, "connecting.");
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();
            logger.log(Level.INFO, "connected.");

            ResultSet rs = st.executeQuery("select \"Freq[Hz]\", 4*\"Amp" + direction + "[gal*s]\"/\"Freq[Hz]\"/\"Freq[Hz]\", \"Phase" + direction + "[rad]\" from \"R152FourierS\".\"" + accName + "\"");

            double[][] ar = ResultSetUtils.createSeriesArray(rs);
            DefaultXYDataset dataset = new DefaultXYDataset();

            dataset.addSeries("Acc", new double[][]{ar[0], ar[1]});

            rs = st.executeQuery("SELECT \"Freq[Hz]\", \"Amp[με*s]\",\"Phase[rad]\" FROM \"R152FourierS\".\"" + gaugeName + "\"");

            double[][] ar2 = ResultSetUtils.createSeriesArray(rs);

            dataset.addSeries("Strain", new double[][]{ar2[0], ar2[1]});

            for (int i = 0; i < ar[0].length; i++) {
                ar[2][i] = ar[2][i] - ar2[2][i];
            }
            FourierUtils.normalizePhase(ar[2]);

            DefaultXYDataset dataset2 = new DefaultXYDataset();
            dataset2.addSeries("Phase diff", new double[][]{ar[0], ar[2]});

            NumberAxis xa = new NumberAxis("Freq[Hz]");
            NumberAxis y1a = new NumberAxis("Amp");
            NumberAxis y2a = new NumberAxis("PhaseDiff");

            y1a.setFixedDimension(40);
            y2a.setFixedDimension(40);

            JFreeChart chart1 = new JunXYChartCreator2()
                    .setDomainAxis(xa).setRangeAxis(y1a)
                    .setDataset(dataset).create();
            JFreeChart chart2 = new JunXYChartCreator2().setDataset(dataset2)
                    .setDomainAxis(xa).setRangeAxis(y2a)
                    .create();

            JunChartUtil.show("S110" + testname + direction, chart1, chart2);

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(S111GraphSpectrumAccAndStrain.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
