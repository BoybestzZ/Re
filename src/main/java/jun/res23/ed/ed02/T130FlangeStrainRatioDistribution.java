/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed02;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
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
import jun.res23.ed.util.BeamEndInfo;
import jun.res23.ed.util.BeamInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import jun.util.JunShapes;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author jun
 */
public class T130FlangeStrainRatioDistribution {

    private static final Logger logger = Logger.getLogger(T130FlangeStrainRatioDistribution.class.getName());
    static final Path svgdir = null; // Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/T130FrangeStrainRatioDistribution");
//    static final String inputSchema = "R151FourierR";
        static final String inputSchema = "R152FourierS";

    public static void main(String[] args) {
        try {
            main(new EdefenseKasinInfo[]{
                EdefenseInfo.D01Q01,// random
                EdefenseInfo.D01Q02,// kumamoto
                EdefenseInfo.D01Q08,// Kobe50
                EdefenseInfo.D01Q09,// random
                EdefenseInfo.D01Q11,// random                       
                EdefenseInfo.D02Q03,// kobe100
                EdefenseInfo.D02Q05,// random
                EdefenseInfo.D02Q08,// tohoku
            },
                    EdefenseInfo.BeamEnd3A);
        } catch (SQLException ex) {
            Logger.getLogger(T130FlangeStrainRatioDistribution.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void modify(JFreeChart chart) {

        AxisSpace space = new AxisSpace();
        space.setLeft(70);
        space.setRight(70);

        chart.getXYPlot().setFixedRangeAxisSpace(space);
    }

    public static void main(EdefenseKasinInfo[] kasins, BeamEndInfo beamend) throws SQLException {
        DefaultXYDataset dataset = new DefaultXYDataset();

        for (EdefenseKasinInfo kasin : kasins) {
            logger.log(Level.INFO, kasin.getName() + " : " + beamend.getName());
            double[][] ratio = T130FlangeStrainRatioDistribution.calculateFlangeStrainRatio(kasin.getName(), beamend);

            dataset.addSeries(kasin.getName(), ratio);
        }
        XYPlot plot = new XYPlot();
        plot.setDataset(dataset);
        plot.setDomainAxis(new NumberAxis("location"));
        plot.setRangeAxis(new NumberAxis("Strain ratio"));
        XYLineAndShapeRenderer re;
        plot.setRenderer(re = new XYLineAndShapeRenderer(true, true));
        re.setSeriesPaint(0, Color.red);
        re.setSeriesPaint(1, Color.BLUE);
        re.setSeriesPaint(2, new Color(0f,0.5f,0f));
        re.setSeriesPaint(3, Color.MAGENTA);

        //   matchRangeZero(plot);
        JFreeChart chart = new JFreeChart(plot);

        if (svgdir != null) {
            int w = 400, h = 200;
            try {
                if (!Files.exists(svgdir)) {
                    Files.createDirectory(svgdir);
                }

                JunChartUtil.svg(svgdir
                        .resolve(beamend.getName() + ".svg"),
                        500, 250, chart);
            } catch (IOException ex) {
                Logger.getLogger(T130FlangeStrainRatioDistribution.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JunChartUtil.show(beamend.getName(), chart);
        }
    }

    private static double[][] calculateFlangeStrainRatio(String testname, BeamEndInfo beamend) throws SQLException {

        String dburl = "jdbc:h2:file://" + R200Resample.databaseDir.resolve(testname + "q");
        Connection con = DriverManager.getConnection(dburl, "junapp", "");

        Statement st = con.createStatement();

        // R151FourierRを利用。
        // まず基準ピーク周波数を算出
        String baseSensor = "a03/01";// 短辺方向の場合。　長辺の場合は g02/01
        ResultSet rs = st.executeQuery("select * from \"" + inputSchema + "\".\"" + baseSensor + "\" order by 2 desc ");
        rs.next();
        double freq = rs.getDouble("Freq[Hz]");
        logger.log(Level.INFO, "freq="+freq);
        double[][] ans = new double[2][4];

        for (int i = 0; i < 4; i++) {

            String sensor1I = beamend.getGaugeShortName(i + 0); // 1I=0, 2I=1, 3I=2, 4I=3, 1O=5, ...

            rs = st.executeQuery("select * from \"" + inputSchema + "\".\"" + sensor1I + "\" where \"Freq[Hz]\"=" + freq);
            rs.next();
            double amp1I = rs.getDouble("Amp[με*s]");
            double phase1I = rs.getDouble("Phase[rad]");

            String sensor1O = beamend.getGaugeShortName(i + 4); // 1I=0, 2I=1, 3I=2, 4I=3, 1O=4, ...

            rs = st.executeQuery("select * from \"" + inputSchema + "\".\"" + sensor1O + "\" where \"Freq[Hz]\"=" + freq);
            rs.next();
            double amp1O = rs.getDouble("Amp[με*s]");
            double phase1O = rs.getDouble("Phase[rad]");

            double ratio1 = (amp1O-amp1I) / (amp1O+amp1I)*2;
            double phase1 = phase1I - phase1O;
            ans[0][i] = i;
            ans[1][i] = ratio1;
        }
        con.close();
        return ans;
    }

}
