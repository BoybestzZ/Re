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
import jun.data.ResultSetUtils;
import jun.fourier.FFTv2;
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
public class T104GraphFlangeNMInPlane {

    private static final Logger logger = Logger.getLogger(T104GraphFlangeNMInPlane.class.getName());
    static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/T104GraphWeakAxisStrain");

    public static void main(String[] args) {
        try {
            main(EdefenseInfo.D01Q10.toString(), EdefenseInfo.LA3S1);
            main(EdefenseInfo.D01Q10.toString(), EdefenseInfo.LA3S2);
            main(EdefenseInfo.D01Q10.toString(), EdefenseInfo.LA3S3);
            main(EdefenseInfo.D01Q10.toString(), EdefenseInfo.LA3S4);
            main(EdefenseInfo.D01Q10.toString(), EdefenseInfo.LA3S5);

        } catch (SQLException ex) {
            Logger.getLogger(T104GraphFlangeNMInPlane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String testname, BeamSectionInfo section) throws SQLException {

        JFreeChart chart
                = chartFlangeMNInPlane(testname, section);

        if (false)
        try {
            if (!Files.exists(svgdir)) {
                Files.createDirectory(svgdir);
            }
            JunChartUtil.svg(chart, svgdir
                    .resolve(testname + "_" + section.getName() + ".svg"),
                    400, 200);
        } catch (IOException ex) {
            Logger.getLogger(T104GraphFlangeNMInPlane.class.getName()).log(Level.SEVERE, null, ex);
        } else {
            JunChartUtil.show(testname + "_" + section.getName(), chart);

        }

    }

    /**
     * 複数の軸が含まれるときに、0の位置を合致させる。 かならず0が含まれる。
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
     * @return [0]=Time[s], [1]=下フランジ軸ひずみ[με]、[2]=上フランジ軸ひずみ[με],
     * [3]=下フランジ弱軸まげひずみ[με] [4] 上フランジ弱軸曲げひずみ[με]
     */
    private static double[][] calculateFlangeBendingMomentInPlane(String testname, BeamSectionInfo section) throws SQLException {

        String dburl = "jdbc:h2:file://" + R200Resample.databaseDir.resolve(testname + "q");

//        SectionInfo section = EdefenseInfo.LA3S1;
        double EA4 = -0.5;//-1e-3 * 1e-6 * 0.25 * section.getE() * section.getA(); // kN / με
        double EZx4 = 0.5;//1e-3 * 1e-6 * 0.25 * section.getE() * section.getZx();//  kNm /με 強軸周り
        double EZy4 = 0.5;// 1e-3 * 1e-6 * 0.25 * section.getE() * section.getZy();//  kNm /με 弱軸周り

//        String axialForce = "(" + EA4 + ")*("
//                + "\"" + section.getULname() + "_Strain[με]\"+"
//                + "\"" + section.getURname() + "_Strain[με]\"+"
//                + "\"" + section.getLLname() + "_Strain[με]\"+"
//                + "\"" + section.getLRname() + "_Strain[με]\""
//                + ")"; // 引張が正。 (ひずみ値は圧縮が正であるが、EA4を負にしているため、引張軸力が正となる。）
        String axialForceLowerFlange = "(" + EA4 + ")*("
                + "\"" + section.getLLname() + "_Strain[με]\"+"
                + "\"" + section.getLRname() + "_Strain[με]\""
                + ")"; //もともと圧縮正だが EA4が負だから、引張正になっている。

        String axialForceUpperFlange = "(" + EA4 + ")*("
                + "\"" + section.getULname() + "_Strain[με]\"+"
                + "\"" + section.getURname() + "_Strain[με]\""
                + ")";//もともと圧縮正だが EA4が負だから、引張正になっている。

        String bendingMomentLowerFlange = "(" + EZx4 + ")*("
                + "\"" + section.getLLname() + "_Strain[με]\"-"
                + "\"" + section.getLRname() + "_Strain[με]\""
                + ")"; //左側圧縮が正。

        String bendingMomentUpperFlange = "(" + EZx4 + ")*("
                + "\"" + section.getULname() + "_Strain[με]\"-"
                + "\"" + section.getURname() + "_Strain[με]\""
                + ")"; //左側圧縮が正。

        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        Statement st = con.createStatement();
        double aveAL = 0;
        double aveAU = 0;
        double aveL = 0;
        double aveU = 0;
//            aveAxial=-516.8244321318311; aveMoment=30.26672208983202;
        ResultSet rs;
        rs = st.executeQuery("select "
                + " avg(" + axialForceLowerFlange + ")-(" + aveAL + "),"
                + " avg(" + axialForceUpperFlange + ")-(" + aveAU + "),"
                + " avg(" + bendingMomentLowerFlange + ")-(" + aveL + "),"
                + " avg(" + bendingMomentUpperFlange + ")-(" + aveU + ")"
                + ""
                + " from "
                + "\"" + R210TimeHistoryTcsv.outputTable + "\""
                + " where \"TIME[s]\"<2.0");
        rs.next();
        aveAL = rs.getDouble(1);
        aveAU = rs.getDouble(2);
        aveL = rs.getDouble(3);
        aveU = rs.getDouble(4);
        rs = st.executeQuery("select \"TIME[s]\","
                + " (" + axialForceLowerFlange + ")-(" + aveAL + "),"
                + " (" + axialForceUpperFlange + ")-(" + aveAU + "),"
                + "(" + bendingMomentLowerFlange + ")-(" + aveL + "),"
                + "(" + bendingMomentUpperFlange + ")-(" + aveU + ")"
                + " from "
                + "\"" + R210TimeHistoryTcsv.outputTable + "\"");

        double[][] ar = ResultSetUtils.createSeriesArray(rs);

        con.close();
        return ar;

    }

    static JFreeChart chartFlangeMNInPlane(String testname, BeamSectionInfo section) throws SQLException {
        //ここで梁断面の NMを算出する。
        double[][] ar = T104GraphFlangeNMInPlane.calculateFlangeBendingMomentInPlane(testname, section);

//        logger.log(Level.INFO, "aveAxial=" + StatUtils.mean(ar[1]) + "; aveMoment=" + StatUtils.mean(ar[2]) + ";");
//        ar[1] = FFTv2.zeroAverage(ar[1]);
//        ar[2] = FFTv2.zeroAverage(ar[2]);
        DefaultXYDataset datasetM = new DefaultXYDataset();
        DefaultXYDataset datasetA = new DefaultXYDataset();

        datasetA.addSeries("AxialLower", new double[][]{ar[0], ar[1]});
        datasetA.addSeries("AxialUpper", new double[][]{ar[0], ar[2]});

        datasetM.addSeries("MomentLower", new double[][]{ar[0], ar[3]});
        datasetM.addSeries("MomentUpper", new double[][]{ar[0], ar[4]});

        XYPlot plot = new XYPlot();
        plot.setDataset(0, datasetM);
        plot.setDataset(1, datasetA);
        NumberAxis xaxis = new NumberAxis("Time[s]");
        NumberAxis maxis = new NumberAxis("Flange Bending strain [με]");
        NumberAxis aaxis = new NumberAxis("Flange Axial strain[με]");
        plot.setDomainAxis(xaxis);
        plot.setRangeAxis(0, maxis);
        plot.setRangeAxis(1, aaxis);
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);

        XYLineAndShapeRenderer r0 = new XYLineAndShapeRenderer(true, false);
        XYLineAndShapeRenderer r1 = new XYLineAndShapeRenderer(true, false);
        r0.setSeriesPaint(0, Color.RED);
        r0.setSeriesPaint(1, Color.BLUE);
        r1.setSeriesPaint(0, Color.GREEN);
        r1.setSeriesPaint(1, Color.MAGENTA);
        plot.setRenderer(0, r0);
        plot.setRenderer(1, r1);
        plot.setRangeZeroBaselineVisible(true);

        JFreeChart chart = new JFreeChart(plot);
        chart.setBackgroundPaint(Color.WHITE);

//        JunChartUtil.show(chart);
        matchRangeZero(plot);
        return chart;
    }

}
