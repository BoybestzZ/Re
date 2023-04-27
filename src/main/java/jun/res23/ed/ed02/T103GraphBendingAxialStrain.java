/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed02;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.BeamSectionInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author jun
 */
public class T103GraphBendingAxialStrain {

    private static final Logger logger = Logger.getLogger(T103GraphBendingAxialStrain.class.getName());

    public static void main(String[] args) {
        try {
            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.LA3S1);
            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.LA3S2);
            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.LA3S3);
            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.LA3S4);
            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.LA3S5);

        } catch (SQLException ex) {
            Logger.getLogger(T103GraphBendingAxialStrain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String testname, BeamSectionInfo section) throws SQLException {

        JFreeChart chart = chartBendingAxialStrainInBeamSection(testname, section);

        if (false) {
            try {
                JunChartUtil.svg(chart, Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/T103GraphTimeHistorySection")
                        .resolve(testname + "_" + section.getName() + ".svg"),
                        400, 200);
            } catch (IOException ex) {
                Logger.getLogger(T103GraphBendingAxialStrain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JunChartUtil.show(testname + "_" + section.getName(), chart);
        }

    }

    public static JFreeChart chartBendingAxialStrainInBeamSection(String testname, BeamSectionInfo section) throws SQLException {

        //ここで梁断面の NMを算出する。
        double[][] ar = T100chartNMTimeHistory.calculateBeamNMStrain(testname, section);

//        logger.log(Level.INFO, "aveAxial=" + StatUtils.mean(ar[1]) + "; aveMoment=" + StatUtils.mean(ar[2]) + ";");
//            ar[1] = FFTv2.zeroAverage(ar[1]);
//            ar[2] = FFTv2.zeroAverage(ar[2]);
        XYSeries m = new XYSeries("m");
        XYSeries a = new XYSeries("a");
        XYSeries s = new XYSeries("ratio");
        int w = 6;
        for (int i = w; i < ar[0].length - w - 1; i++) {
            boolean found = true;
            double target = Math.abs(ar[1][i]);
            for (int j = i - w; j <= i + w; j++) {
                if (target < Math.abs(ar[1][j])) {
                    found = false;
                    break;
                }
            }
            if (found) {
                s.add(ar[0][i], Math.abs(ar[1][i]) / ar[2][i]);

            }
            m.add(ar[0][i], ar[2][i]);
            a.add(ar[0][i], ar[1][i]);
        }

        XYSeriesCollection cm = new XYSeriesCollection();
        XYSeriesCollection ca = new XYSeriesCollection();
        cm.addSeries(m);
        ca.addSeries(a);
//            c.addSeries(s);

        XYPlot plot = new XYPlot();
        plot.setDataset(0, cm);
        plot.setDataset(1, ca);
        NumberAxis xaxis = new NumberAxis("Time[s]");
        NumberAxis maxis = new NumberAxis("Bending strain [με]");
        NumberAxis aaxis = new NumberAxis("Axial strain[με]");
        plot.setDomainAxis(xaxis);
        plot.setRangeAxis(0, maxis);
        plot.setRangeAxis(1, aaxis);
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);
//        xaxis.setRange(5, 35);

        XYLineAndShapeRenderer r0 = new XYLineAndShapeRenderer(true, false);
        XYLineAndShapeRenderer r1 = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer(0, r0);
        plot.setRenderer(1, r1);
        plot.setRangeZeroBaselineVisible(true);

        JFreeChart chart = new JFreeChart(plot);
        chart.setBackgroundPaint(Color.WHITE);
//        JunChartUtil.show(chart);

        matchRangeZero(plot);
        return chart;
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

}
