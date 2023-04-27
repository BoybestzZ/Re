/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

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
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import jun.util.JunShapes;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 * T270の値からグラフを表示する。
 *
 * @author jun
 */
public class T271GraphTimeHistoryLAMoment {

    private static final Logger logger = Logger.getLogger(T271GraphTimeHistoryLAMoment.class.getName());
    public static final String res22ed06url = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res23/ed/ed06分析T/res22ed06";
    public static final String outputSchema = "T270TimeHistoryLAM";
    public static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed06分析T/T271TimeHistoryLAMoment");
    //  public static final String outputDb = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/res22ed06";

    public static void main(String[] args) {
        try {
            //        BeamSectionInfo[] sections = new BeamSectionInfo[]{EdefenseInfo.LA3S1, EdefenseInfo.LA3S2, EdefenseInfo.LA3S3, EdefenseInfo.LA3S4, EdefenseInfo.LA3S5};
            show(EdefenseInfo.D01Q01, 49.5, EdefenseInfo.LA3S2); //random
            show(EdefenseInfo.D01Q09, 49, EdefenseInfo.LA3S2); //random
            show(EdefenseInfo.D01Q11, 49, EdefenseInfo.LA3S2); //random
            show(EdefenseInfo.D02Q05, 43.5, EdefenseInfo.LA3S2); //random
//            show(EdefenseInfo.D03Q01, EdefenseInfo.LA3S2); //random
        } catch (SQLException ex) {
            Logger.getLogger(T271GraphTimeHistoryLAMoment.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *
     */
    public static void show(EdefenseKasinInfo test, double startTime, BeamSectionInfo section) throws SQLException {

        Connection cono = DriverManager.getConnection(res22ed06url, "junapp", "");
        Statement sto = cono.createStatement();

        ResultSet rs = sto.executeQuery("select "
                + " TESTNAME ,\"TimePerTest[s]\" ,\"TotalTime[s]\" ,\"TMRTime[s]\" , "
                + "\"UpperAxialStrainPerTest[με]\" , \"LowerAxialStrainPerTest[με]\", "
                + "-\"UpperBendingStrainPerTest[με]\" ,- \"LowerBendingStrainPerTest[με]\" " // 大会の図はここが5倍になってた！　直した。符号はう2023/04/11 
                + " from \"" + outputSchema + "\".\"" + section.getName() + "\" where TESTNAME='" + test.getTestName() + "t'"
        );

        double[][][] ar = ResultSetUtils.createSeriesArray(rs, new int[][]{{2, 5}, {2, 7}, {2, 6}, {2, 8}}); //upper

        DefaultXYDataset dataset1 = new DefaultXYDataset();
        DefaultXYDataset dataset2 = new DefaultXYDataset();
//        dataset.addSeries("UpperAxial", ar[0]);
//        dataset.addSeries("UpperBending", ar[1]);
        dataset1.addSeries("Axial", ar[2]);
        dataset2.addSeries("Bending", ar[3]);

        NumberAxis xa = new NumberAxis("Time[s]");
        xa.setRange(startTime, startTime + 4);
        NumberAxis ya1 = new NumberAxis("AxialStrain[με]");
        NumberAxis ya2 = new NumberAxis("BendingStrain[με]");

        ya1.setFixedDimension(33);
        ya2.setFixedDimension(33);

        XYLineAndShapeRenderer r1 = new XYLineAndShapeRenderer(true, false);

        XYLineAndShapeRenderer r2 = new XYLineAndShapeRenderer(true, false);
        r2.setSeriesStroke(0, JunShapes.MEDIUM_LINE);
        XYPlot plot = new XYPlot();

        plot.setDomainAxis(xa);
        plot.setRangeAxis(1, ya1);
        plot.setRangeAxis(0, ya2);
        plot.setRangeZeroBaselineVisible(true);
        plot.setRenderer(0, r1);
        plot.setRenderer(1, r2);
        plot.setDataset(0, dataset1);
        plot.setDataset(1, dataset2);
        plot.mapDatasetToRangeAxis(0, 1);
        plot.mapDatasetToRangeAxis(1, 0);

        double[] min1 = obtain(dataset1, 0, startTime, 1.0, Math.PI);
        double[] min2 = obtain(dataset2, 0, startTime, 2.0, 0);

        XYPointerAnnotation pa1 = new XYPointerAnnotation(String.format("%.1f", min1[1]), min1[0], min1[1] * 0.1, 0);
        pa1.setTextAnchor(TextAnchor.CENTER_LEFT);
        pa1.setTipRadius(5);
        pa1.setBaseRadius(20);
        XYPointerAnnotation pa2 = new XYPointerAnnotation(String.format("%.1f (%.0f%%)", min2[1], min2[1] / min1[1] * 100), min2[0], min2[1], Math.PI);
        pa2.setTipRadius(5);
        pa2.setBaseRadius(20);
        pa2.setTextAnchor(TextAnchor.CENTER_RIGHT);

        plot.addAnnotation(pa1);
        plot.addAnnotation(pa2);

        JFreeChart chart = new JFreeChart(plot);
        chart.setBackgroundPaint(Color.WHITE);
        LegendTitle le = chart.getLegend();
        chart.removeLegend();
        XYTitleAnnotation ta = new XYTitleAnnotation(0.99, 0.99, le, RectangleAnchor.TOP_RIGHT);

        plot.addAnnotation(ta);

        ya2.setUpperBound(0.1 * ya1.getUpperBound());
        ya2.setLowerBound(0.1 * ya1.getLowerBound());

        if (svgdir != null) {
            try {
                if (!Files.exists(svgdir)) {
                    Files.createDirectory(svgdir);
                }
                Path svgfile = svgdir.resolve(test.getTestName() + "_" + section.getName() + ".svg");

                JunChartUtil.svg(svgfile, 270, 150, chart);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {

            JunChartUtil.show(test.getTestName() + "_" + section.getName(), chart);
        }
        cono.close();

    }

    public static double[] obtain(XYDataset dataset, int series, double startTime, double factor, double angle) {

        double minx = 0;
        double miny = Double.POSITIVE_INFINITY;
        int count = dataset.getItemCount(series);
        for (int i = 0; i < count; i++) {
            double x = dataset.getXValue(series, i);
            if (x < startTime) {
                continue;
            }
            if (x > startTime + 5) {
                break;
            }
            double y = dataset.getYValue(series, i);
            if (y < miny) {
                miny = y;
                minx = x;
            }
        }
        return new double[]{minx, miny};

    }

}
