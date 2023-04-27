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
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.BeamSectionInfo;
import org.jfree.chart.JFreeChart;
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
public class T105GraphStrainInSection {

    private static final Logger logger = Logger.getLogger(T105GraphStrainInSection.class.getName());
    static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/T104GraphWeakAxisStrain");

    public static void main(String[] args) {
        try {
            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.LA3S1);
            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.LA3S2);
            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.LA3S3);
            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.LA3S4);
            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.LA3S5);

        } catch (SQLException ex) {
            Logger.getLogger(T105GraphStrainInSection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static JFreeChart chartStrainInSection(String testname, BeamSectionInfo section) throws SQLException {

        double[][] ar = T105GraphStrainInSection.obtainStrainsInSection(testname, section);

        DefaultXYDataset dataset = new DefaultXYDataset();
        dataset.addSeries("UL", new double[][]{ar[0], ar[1]});
        dataset.addSeries("UR", new double[][]{ar[0], ar[2]});
        dataset.addSeries("LL", new double[][]{ar[0], ar[3]});
        dataset.addSeries("LR", new double[][]{ar[0], ar[4]});

        NumberAxis domainAxis = new NumberAxis("Time [s]");
        NumberAxis rangeAxis = new NumberAxis("Strain [με]");
      //  domainAxis.setRange(5, 35);
        XYLineAndShapeRenderer r = new XYLineAndShapeRenderer(true, false);

        r.setSeriesPaint(0, Color.RED);
        r.setSeriesPaint(1, Color.BLUE);
        r.setSeriesPaint(2, Color.GREEN);
        r.setSeriesPaint(3, Color.MAGENTA);
        XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, r);
        JFreeChart chart = new JFreeChart(plot);
        chart.setBackgroundPaint(Color.WHITE);

        return chart;
    }

    public static void main(String testname, BeamSectionInfo section) throws SQLException {
        JFreeChart chart = chartStrainInSection(testname, section);
        if (false) {
            try {
                if (!Files.exists(svgdir)) {
                    Files.createDirectory(svgdir);
                }
                JunChartUtil.svg(chart, svgdir
                        .resolve(testname + "_" + section.getName() + ".svg"),
                        400, 200);
            } catch (IOException ex) {
                Logger.getLogger(T105GraphStrainInSection.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JunChartUtil.show(testname + "_" + section.getName(), chart);
        }

    }

    /**
     * かならず0が含まれる。
     *
     * @param plot
     */
    public static void matchRangeZero(XYPlot plot) {
        int ac = plot.getRangeAxisCount();
        double currentRatio = 0;
        for (int i = 0; i < ac; i++) {
            ValueAxis ra = plot.getRangeAxis(i);
            Range range = ra.getRange();
            double min = range.getLowerBound(); // ここでは軸の最大最小をとっているが、データの最大最小を取る方法もある。
            double max = range.getUpperBound();
            double ratio = -min / (max - min);  // 基本的に 0から1の値を取る。なるべく0.5に近いものが望ましい。
            if (Math.abs(currentRatio - 0.5) > Math.abs(ratio - 0.5)) {
                currentRatio = ratio;
            }
        }

        for (int i = 0; i < ac; i++) {
            ValueAxis ra = plot.getRangeAxis(i);
            Range range = ra.getRange();
            double min = range.getLowerBound(); // ここでは軸の最大最小をとっているが、データの最大最小を取る方法もある。
            double max = range.getUpperBound();
            if (max > -min) { // 正側が大きいので、負側を変更する。
                double newmin = -max / (1 - currentRatio) * currentRatio;
                ra.setLowerBound(newmin);
            } else { // 負側が大きいので側を変更する。
                double newmax = (-min) / currentRatio * (1 - currentRatio);
                ra.setUpperBound(newmax);
            }
        }

    }

    /**
     * 下フランジの弱軸曲げひずみを算出する。初期値を０としている。また、床スラブの効果は考慮していない。
     *
     * @param testname
     * @param section
     * @return [1]=下フランジ弱軸まげひずみ[με]
     */
    private static double[][] obtainStrainsInSection(String testname, BeamSectionInfo section) throws SQLException {

        String dburl = "jdbc:h2:file://" + R200Resample.databaseDir.resolve(testname + "q");

//        SectionInfo section = EdefenseInfo.LA3S1;
        double EA4 = -1.0;//-1e-3 * 1e-6 * 0.25 * section.getE() * section.getA(); // kN / με
        double EZx4 = 1.0;//1e-3 * 1e-6 * 0.25 * section.getE() * section.getZx();//  kNm /με 強軸周り
        double EZy4 = 1.0;// 1e-3 * 1e-6 * 0.25 * section.getE() * section.getZy();//  kNm /με 弱軸周り

//        String axialForce = "(" + EA4 + ")*("
//                + "\"" + section.getULname() + "_Strain[με]\"+"
//                + "\"" + section.getURname() + "_Strain[με]\"+"
//                + "\"" + section.getLLname() + "_Strain[με]\"+"
//                + "\"" + section.getLRname() + "_Strain[με]\""
//                + ")"; // 引張が正。 (ひずみ値は圧縮が正であるが、EA4を負にしているため、引張軸力が正となる。）
        String bendingStrainWeakAxis = "(" + EZx4 + ")*("
                + ")"; //左側圧縮が正。
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        Statement st = con.createStatement();

        ResultSet rs;
        rs = st.executeQuery("select "
                + "avg(\"" + section.getULname() + "_Strain[με]\"),"
                + "avg(\"" + section.getURname() + "_Strain[με]\"),"
                + "avg(\"" + section.getLLname() + "_Strain[με]\"),"
                + "avg(\"" + section.getLRname() + "_Strain[με]\")"
                + " from "
                + "\"" + R210TimeHistoryTcsv.outputTable + "\""
                + " where \"TIME[s]\"<2.0");
        rs.next();
        double ulave = rs.getDouble(1);
        double urave = rs.getDouble(2);
        double llave = rs.getDouble(3);
        double lrave = rs.getDouble(4);

        rs = st.executeQuery("select \"TIME[s]\","
                + "\"" + section.getULname() + "_Strain[με]\"-(" + ulave + "),"
                + "\"" + section.getURname() + "_Strain[με]\"-(" + urave + "),"
                + "\"" + section.getLLname() + "_Strain[με]\"-(" + llave + "),"
                + "\"" + section.getLRname() + "_Strain[με]\"-(" + lrave + ")"
                + " from "
                + "\"" + R210TimeHistoryTcsv.outputTable + "\"");

        double[][] ar = ResultSetUtils.createSeriesArray(rs);

        con.close();
        return ar;

    }

}
