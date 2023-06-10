/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed14分析UF;

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
import jun.res23.ed.util.BeamInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.BeamSectionInfo;
import jun.util.JunShapes;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * ed02.T121→ ed06.T121 ed06T211 で計算した結果を使って、梁応力分布の表示 ed06.T121 -> ed11分析F.F121
 *
 * @author jun
 *
 *
 */
public class A110BeamNMDistribution {

    private static final Logger logger = Logger.getLogger(A110BeamNMDistribution.class.getName());
    static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed14分析UF/A110BeamNMDistribution");
    static final String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res23/ed/ed14分析UF/ed14";
    static final String readTable = "A100SectionNM";

    public static void main(String[] args) {
        try {
            main(EdefenseInfo.D01Q01.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D01Q02.toString(), EdefenseInfo.Beam3); // kumamoto
            main(EdefenseInfo.D01Q03.toString(), EdefenseInfo.Beam3); // tohoku
            main(EdefenseInfo.D01Q04.toString(), EdefenseInfo.Beam3); // Kobe 25%
            main(EdefenseInfo.D01Q05.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D01Q06.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D01Q08.toString(), EdefenseInfo.Beam3); // Kobe 50%
            main(EdefenseInfo.D01Q09.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D01Q10.toString(), EdefenseInfo.Beam3); // Kobe 75%
            main(EdefenseInfo.D01Q11.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D02Q01.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D02Q02.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.Beam3); // Kobe 100%
            main(EdefenseInfo.D02Q05.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D02Q06.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D02Q07.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D02Q08.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D03Q01.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D03Q02.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D03Q03.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D03Q04.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D03Q05.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D03Q06.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D03Q08.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D03Q09.toString(), EdefenseInfo.Beam3);

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
            Logger.getLogger(A110BeamNMDistribution.class.getName()).log(Level.SEVERE, null, ex);
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

        XYSeries mAmp = new XYSeries("moment_amp");
        XYSeries mtAmp = new XYSeries("momentTotal_amp", false);
        XYSeries aAmp = new XYSeries("axial_amp");

        Connection con = DriverManager.getConnection(dburl, "junapp", "");

        Statement st = con.createStatement();

        int startSection = 0;

        for (int i = startSection; i < beam.getSections().length; i++) {
            double x = beam.getLocation(i);
            BeamSectionInfo section = beam.getBeamSections()[i];
            String sectionName = section.getName();
//
//            ResultSet rs = st.executeQuery("select "
//                    + "\"AxialA[N*s]\", \"AxialP[rad]\", "
//                    + "\"MomentXA[Nm*s]\",\"MomentXP[rad]\","
//                    + "\"StoryDriftA[gal*s]\",\"StoryDriftP[rad]\",  "
//                    +"\"Freq[Hz]\" "
//                    + " from \"" + readTable + "\""
//                    + " where TESTNAME='" + testname + "' and SECTION=\"" + sectionName + "\"");
            // これから除して計算してもいいけど、すでに F190ではstiffnessにしているので、それを使う。
            ResultSet rs = st.executeQuery("select "
                    + "\"StiffnessAxialA[N/m]\"*0.000001, \"StiffnessAxialP[rad]\", " // 1/1000000 しているので、 kN/mm になってる。
                    + "\"StiffnessMomentXA[Nm/m]\"*0.000001,\"StiffnessMomentXP[rad]\"," // 1/1000000 しているので、 kNm/mm になっている。
                    + "\"StoryDriftA[gal*s]\",\"StoryDriftP[rad]\",  "
                    +"\"Freq[Hz]\" "
                    + " from \"" + readTable + "\""
                    + " where TESTNAME='" + testname + "' and SECTION='" + sectionName + "'");
            rs.next();

            double freq=rs.getDouble(7);
            Complex axial = ComplexUtils.polar2Complex(rs.getDouble(1), rs.getDouble(2));
            Complex moment = ComplexUtils.polar2Complex(rs.getDouble(3), rs.getDouble(4));
            Complex driftAcc = ComplexUtils.polar2Complex(rs.getDouble(5), rs.getDouble(6));
  

            double momentR, axialR;
            mAmp.add(x, momentR=moment.getReal()); // 実数部だけを表示。
            aAmp.add(x, axialR=axial.getReal());
            mtAmp.add(x, momentR + axialR * (section.getHeight() + 0.11) * 0.5); 



        }

        // 曲げモーメントのデータ
        XYSeriesCollection mc = new XYSeriesCollection();
        mc.addSeries(mAmp);
        mc.addSeries(mtAmp);
        XYPlot plot = new XYPlot();
        plot.setDataset(0, mc);
        NumberAxis maxis;

        plot.setRangeAxis(0, maxis = new NumberAxis("Moment [kNm/mm]"));
        maxis.setInverted(true);
//        maxis.setRange(-500, 500);
        maxis.setFixedDimension(50);
        maxis.setRange(-10,6);
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
       
        double inflectionPoint = Double.NaN;
        {
            XYSeries s = mtAmp;
            for (int sectionNo = 1; sectionNo < 5; sectionNo++) {
                double y = s.getY(sectionNo).doubleValue();
                if (Double.isNaN(prevY)) {
                    prevY = y;
                    continue;
                }
                if (prevY * y < 0.0) { // 反転した。
                    double px = s.getX(sectionNo - 1).doubleValue();
                    double x = s.getX(sectionNo).doubleValue();
                    inflectionPoint = (-prevY) / (y - prevY) * (x - px) + px;
                    break;
                }
                prevY = y;
            }
        }

    
        XYPointerAnnotation anoMin = new XYPointerAnnotation(String.format("%.2f",inflectionPoint), inflectionPoint, 0, Math.PI*0.5);
        anoMin.setTipRadius(0);
        anoMin.setBaseRadius(20);

        plot.addAnnotation(anoMin);

      

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
                Logger.getLogger(A110BeamNMDistribution.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JunChartUtil.show(testname + "_" + beam.getName(), chart);
        }
    }

}
