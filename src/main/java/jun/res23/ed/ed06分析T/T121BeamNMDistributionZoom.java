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
import jun.res23.ed.ed08.B101CompareToInteg2Disp;
import jun.res23.ed.util.BeamInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.BeamSectionInfo;
import jun.util.JunShapes;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * ed02.T121→ ed06.T121 ed06T211 で計算した結果を使って、梁応力分布の表示
 *
 * @author jun
 *
 *
 */
public class T121BeamNMDistributionZoom {

    private static final Logger logger = Logger.getLogger(T121BeamNMDistributionZoom.class.getName());
    static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed06分析T/T121BeamNMDistribution");

    public static void main(String[] args) {
        try {
//            main(EdefenseInfo.D01Q09.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D01Q02.toString(), EdefenseInfo.Beam3); // kumamoto
//            main(EdefenseInfo.D01Q03.toString(), EdefenseInfo.Beam3); // tohoku
            main(EdefenseInfo.D01Q04.toString(), EdefenseInfo.Beam3); // Kobe 25%
//            main(EdefenseInfo.D01Q05.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D01Q06.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D01Q08.toString(), EdefenseInfo.Beam3); // Kobe 50%
//            main(EdefenseInfo.D01Q09.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D01Q10.toString(), EdefenseInfo.Beam3); // Kobe 75%
//          main(EdefenseInfo.D01Q11.toString(), EdefenseInfo.Beam3); //random
//            main(EdefenseInfo.D02Q01.toString(), EdefenseInfo.Beam3); // kumamoto
//            main(EdefenseInfo.D02Q02.toString(), EdefenseInfo.Beam3); // tohoku
//            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.Beam3); // Kobe 100%
//            main(EdefenseInfo.D02Q05.toString(), EdefenseInfo.Beam3); //random
//            main(EdefenseInfo.D02Q06.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D02Q07.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D02Q08.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D03Q01.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D03Q02.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D03Q03.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D03Q04.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D03Q05.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D03Q06.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D03Q08.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D03Q09.toString(), EdefenseInfo.Beam3);

//            main(EdefenseInfo.D01Q01.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D01Q02.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D01Q03.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D01Q04.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D01Q05.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D01Q06.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D01Q08.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D01Q09.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D01Q10.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D01Q11.toString(), EdefenseInfo.BeamB);
//
//            main(EdefenseInfo.D02Q01.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D02Q02.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D02Q05.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D02Q06.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D02Q07.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D02Q08.toString(), EdefenseInfo.BeamB);
//
//            main(EdefenseInfo.D03Q01.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D03Q02.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D03Q03.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D03Q04.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D03Q05.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D03Q06.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D03Q08.toString(), EdefenseInfo.BeamB);
//            main(EdefenseInfo.D03Q09.toString(), EdefenseInfo.BeamB);
        } catch (SQLException ex) {
            Logger.getLogger(T121BeamNMDistributionZoom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void modify(JFreeChart chart) {

        AxisSpace space = new AxisSpace();
        space.setLeft(70);
        space.setRight(70);

        chart.getXYPlot().setFixedRangeAxisSpace(space);
    }

    public static void main(String testname, BeamInfo beam) throws SQLException {
        logger.log(Level.INFO, testname + " : " + beam.getName());

        // [0]=time[s], [1]=N[kN] [2]=Mx[kNm]
        double maxt = 0;
        double maxm = Double.NEGATIVE_INFINITY;
        double mint = 0;
        double minm = Double.POSITIVE_INFINITY;
        double endt;

        XYSeries mmin = new XYSeries("moment_min");
        XYSeries mtmin = new XYSeries("momentTotal_min", false);
        XYSeries amin = new XYSeries("axial_min");

        XYSeries mmax = new XYSeries("moment_max");
        XYSeries mtmax = new XYSeries("momentTotal_max", false);
        XYSeries amax = new XYSeries("axial_max");

        XYSeries mend = new XYSeries("axial_end");
        XYSeries aend = new XYSeries("axial_end");

        Connection con = DriverManager.getConnection(B101CompareToInteg2Disp.ed06dburl, "junapp", "");

        Statement st = con.createStatement();

        // まず最大と最小の時刻を決めないといけない。
        String mainSection = "LA" + beam.getName().charAt(4) + "S2";
        String sql = "select \"TimePerTest[s]\" "
                + " from \"" + T231CreateTimeHistoryBeamColumnNM.outputSchema + "\".\""+mainSection+"\""
                + " where TESTNAME='" + testname + "t' "
                + "order by \"BendingMomentPerTest[kNm]\"  limit 1";
        logger.log(Level.INFO, sql);
        ResultSet rs = st.executeQuery(sql);

        rs.next();
        double timeMin = Math.round(rs.getDouble(1) * 100) / 100.0;
        rs = st.executeQuery("select \"TimePerTest[s]\" "
                + " from \"" + T231CreateTimeHistoryBeamColumnNM.outputSchema + "\".\""+mainSection+"\""
                + " where TESTNAME='" + testname + "t' "
                + "order by \"BendingMomentPerTest[kNm]\" desc limit 1");

        rs.next();
        double timeMax = Math.round(rs.getDouble(1) * 100) / 100.0;
        logger.log(Level.INFO, "Time obtained.tmax=" + timeMax + ", tmin=" + timeMin);

        int startSection = 0;
        if (testname.equals("D02Q03")) {
            startSection = 1;
        }

        for (int i = startSection; i < beam.getSections().length; i++) {
            BeamSectionInfo section = beam.getBeamSections()[i];
            String sectionName = section.getName();

            // 最小なので、descは無い。　最大を取りたければ、desc をつける。
            rs = st.executeQuery("select \"AxialForcePerTest[kN]\", \"BendingMomentPerTest[kNm]\" "
                    + " from \"" + T231CreateTimeHistoryBeamColumnNM.outputSchema + "\".\"" + sectionName + "\""
                    + " where TESTNAME='" + testname + "t' and \"TimePerTest[s]\"=" + timeMin);
            rs.next();

            double axial = rs.getDouble(1);
            double moment = rs.getDouble(2);
            double x = beam.getLocation(i);
            mmin.add(x, moment);
            amin.add(x, axial);
            mtmin.add(x, moment + axial * (section.getHeight() + 0.11) * 0.5);

            rs = st.executeQuery("select \"AxialForcePerTest[kN]\", \"BendingMomentPerTest[kNm]\" "
                    + " from \"" + T231CreateTimeHistoryBeamColumnNM.outputSchema + "\".\"" + sectionName + "\""
                    + " where TESTNAME='" + testname + "t' and \"TimePerTest[s]\"=" + timeMax);
            rs.next();

            axial = rs.getDouble(1);
            moment = rs.getDouble(2);

            mmax.add(x, moment);
            amax.add(x, axial);
            mtmax.add(x, moment + axial * (section.getHeight() + 0.11) * 0.5);

// ここは残留を取得するのだが、とりあえず使わないので、 0　を入れておく。
            mend.add(x, 0);
            aend.add(x, 0);

        }

        // 曲げモーメントのデータ
        XYSeriesCollection mc = new XYSeriesCollection();
//        mc.addSeries(mmin);
//        mc.addSeries(mmax);
//        mc.addSeries(mend);
        mc.addSeries(mtmin);
        mc.addSeries(mtmax);
//        XYSeriesCollection ac = new XYSeriesCollection();
//        ac.addSeries(amin);
//        ac.addSeries(amax);
//        ac.addSeries(aend);
        XYPlot plot = new XYPlot();
        plot.setDataset(0, mc);
//        plot.setDataset(1, ac);
        NumberAxis maxis;

        plot.setRangeAxis(0, maxis = new NumberAxis("Moment [kNm]"));
        maxis.setInverted(true);
//        maxis.setRange(-500, 500);
        maxis.setFixedDimension(50);
//        NumberAxis aaxis;
//        plot.setRangeAxis(1, aaxis = new NumberAxis("Axial [kN]"));
//        aaxis.setInverted(true);
//        aaxis.setFixedDimension(50);
//        aaxis.setRange(-1000, 1000);
        plot.mapDatasetToRangeAxis(0, 0);
//        plot.mapDatasetToRangeAxis(1, 1);
        NumberAxis xaxis;

        plot.setDomainAxis(xaxis = new NumberAxis("Location[m]"));
        xaxis.setRange(0, beam.getLength());
        XYLineAndShapeRenderer re = new XYLineAndShapeRenderer(true, true);
        XYLineAndShapeRenderer re1 = new XYLineAndShapeRenderer(true, true);
        re.setSeriesPaint(0, Color.BLACK); //moment
        re.setSeriesPaint(1, Color.BLACK); //moment
        re.setSeriesPaint(2, Color.RED); //momenttotal
        re.setSeriesPaint(3, Color.BLACK); //momenttotal
        re.setSeriesPaint(4, Color.BLACK); //momenttotal
        re.setSeriesStroke(3, JunShapes.MEDIUM_LINE);
        re.setSeriesStroke(4, JunShapes.MEDIUM_LINE);
        re1.setSeriesPaint(0, Color.BLUE); //axial
        re1.setSeriesPaint(1, Color.BLUE);
        re1.setSeriesPaint(2, Color.BLUE);
        re1.setAutoPopulateSeriesStroke(false);
        re1.setDefaultStroke(JunShapes.NORMAL_DASHED);

        plot.setRenderer(0, re);
        plot.setRenderer(1, re1);
        plot.setRangeZeroBaselineVisible(true);

//        if (aaxis.getUpperBound() > 1000 || aaxis.getLowerBound() < -1000) {
//            aaxis.setRange(-1000, 1000);
//        }
        double u = maxis.getUpperBound();
        double l = maxis.getLowerBound();
        double ulm;
        if (u > 500 || l < -500) {
            ulm = 500;
        } else {
            ulm = (u > (-l)) ? u : -l;

        }
        maxis.setRange(-ulm, ulm);
//        JunChartUtil.matchRangeZero(plot);

// 反曲点位置の算出
        double prevY = Double.NaN;
        double minInflectionPoint = Double.NaN;
        {
            XYSeries s = mtmin;
            for (int sectionNo = 1; sectionNo < 5; sectionNo++) {
                double y = s.getY(sectionNo).doubleValue();
                if (Double.isNaN(prevY)) {
                    prevY = y;
                    continue;
                }
                if (prevY * y < 0.0) { // 反転した。
                    double px = s.getX(sectionNo - 1).doubleValue();
                    double x = s.getX(sectionNo).doubleValue();
                    minInflectionPoint = (-prevY) / (y - prevY) * (x - px) + px;
                    break;
                }
                prevY = y;
            }
        }

        prevY = Double.NaN;
        double maxInflectionPoint = Double.NaN;
        {
            XYSeries s = mtmax;
            for (int sectionNo = 1; sectionNo < 5; sectionNo++) {
                double y = s.getY(sectionNo).doubleValue();
                if (Double.isNaN(prevY)) {
                    prevY = y;
                    continue;
                }
                if (prevY * y < 0.0) { // 反転した。
                    double px = s.getX(sectionNo - 1).doubleValue();
                    double x = s.getX(sectionNo).doubleValue();
                    maxInflectionPoint = (-prevY) / (y - prevY) * (x - px) + px;
                    break;
                }
                prevY = y;
            }
        }

        double angleMin = 0;
        double angleMax = Math.PI;
        if (Math.abs(minInflectionPoint - maxInflectionPoint) < 0.150) {
            angleMax = 0;
            angleMin = Math.PI;
        }
        XYPointerAnnotation anoMin = new XYPointerAnnotation("", minInflectionPoint, -ulm / 3, angleMin);
        anoMin.setTipRadius(0);
        anoMin.setBaseRadius(20);

        plot.addAnnotation(anoMin);
        XYPointerAnnotation anoMax = new XYPointerAnnotation("", maxInflectionPoint, -ulm / 3, angleMax);
        plot.addAnnotation(anoMax);
        anoMax.setTipRadius(0);
        anoMax.setBaseRadius(20);
        XYTextAnnotation anoText = new XYTextAnnotation(
                String.format("%.2f m", Math.abs(minInflectionPoint - maxInflectionPoint)), (minInflectionPoint + maxInflectionPoint) * 0.5, -ulm / 2.5);
        anoText.setTextAnchor(TextAnchor.BOTTOM_CENTER);
        plot.addAnnotation(anoText);

        JFreeChart chart = new JFreeChart(plot);
        chart.setBackgroundPaint(Color.WHITE);

        if (svgdir != null) {
            int w = 300, h = 180;
            try {
                if (!Files.exists(svgdir)) {
                    Files.createDirectory(svgdir);
                }

                JunChartUtil.svg(svgdir
                        .resolve(testname + "_" + beam.getName() + ".svg"),
                        w, h, chart);
            } catch (IOException ex) {
                Logger.getLogger(T121BeamNMDistributionZoom.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JunChartUtil.show(testname + "_" + beam.getName(), chart);
        }
    }

}
