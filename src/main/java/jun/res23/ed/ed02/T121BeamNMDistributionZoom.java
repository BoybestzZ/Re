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
import jun.res23.ed.util.BeamInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.BeamSectionInfo;
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
 * @deprecated 今日追記 これはこれで正しいが、図を書き直すならば ed06分析T のデータベース res22ed06.mv.db のなかに、
"T231TimeHistoryNM"スキーマの中に各断面の応力時刻歴が入っているので、それを使ったほうが速い。 
 */
public class T121BeamNMDistributionZoom {

    private static final Logger logger = Logger.getLogger(T121BeamNMDistributionZoom.class.getName());
    static final Path svgdir =null;//  Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/T121BeamNMDistribution");

    public static void main(String[] args) {
        try {
//            main(EdefenseInfo.D01Q01.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D01Q02.toString(), EdefenseInfo.Beam3); // kumamoto
//            main(EdefenseInfo.D01Q03.toString(), EdefenseInfo.Beam3); // tohoku
            main(EdefenseInfo.D01Q04.toString(), EdefenseInfo.Beam3); // Kobe 25%
//            main(EdefenseInfo.D01Q05.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D01Q06.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D01Q08.toString(), EdefenseInfo.Beam3); // Kobe 50%
//            main(EdefenseInfo.D01Q09.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D01Q10.toString(), EdefenseInfo.Beam3); // Kobe 75%
//            main(EdefenseInfo.D01Q11.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D02Q01.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D02Q02.toString(), EdefenseInfo.Beam3);
            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.Beam3); // Kobe 100%
//            main(EdefenseInfo.D02Q05.toString(), EdefenseInfo.Beam3);
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
        {
            double[][] nm = T100chartNMTimeHistory.calculateBeamNM(testname, beam.getBeamSections()[0]);
            for (int i = 0; i < nm[0].length; i++) {
                double value = nm[2][i];
                if (value > maxm) {
                    maxt = nm[0][i];
                    maxm = value;
                }
                if (value < minm) {
                    mint = nm[0][i];
                    minm = value;
                }
            }
            endt = nm[0][nm[0].length - 1];
        }

        XYSeries mmin = new XYSeries("moment_min");
        XYSeries mtmin = new XYSeries("momentTotal_min");
        XYSeries amin = new XYSeries("axial_min");

        XYSeries mmax = new XYSeries("moment_max");
        XYSeries mtmax = new XYSeries("momentTotal_max");
        XYSeries amax = new XYSeries("axial_max");

        XYSeries mend = new XYSeries("axial_end");
        XYSeries aend = new XYSeries("axial_end");

        for (int i = 0; i < beam.getSections().length; i++) {
            BeamSectionInfo section = beam.getBeamSections()[i];
            double[] nm = T100chartNMTimeHistory.calculateBeamNM(testname, beam.getBeamSections()[i], mint);
            double x = beam.getLocation(i);
            mmin.add(x, nm[1]);
            amin.add(x, nm[0]);
            mtmin.add(x, nm[1] + nm[0] * (0.350 + 0.11) * 0.5);

            nm = T100chartNMTimeHistory.calculateBeamNM(testname, beam.getBeamSections()[i], maxt);
            mmax.add(x, nm[1]);
            amax.add(x, nm[0]);
            mtmax.add(x, nm[1] + nm[0] * (0.350 + 0.11) * 0.5);

            nm = T100chartNMTimeHistory.calculateBeamNM(testname, beam.getBeamSections()[i], endt);
            mend.add(x, nm[1]);
            aend.add(x, nm[0]);

        }

        XYSeriesCollection mc = new XYSeriesCollection();
        XYSeriesCollection ac = new XYSeriesCollection();
        mc.addSeries(mmin);
        mc.addSeries(mmax);
        mc.addSeries(mend);
        mc.addSeries(mtmin);
        mc.addSeries(mtmax);

        ac.addSeries(amin);
        ac.addSeries(amax);
        ac.addSeries(aend);
        XYPlot plot = new XYPlot();
        plot.setDataset(0, mc);
        plot.setDataset(1, ac);
        NumberAxis maxis;

        plot.setRangeAxis(0, maxis = new NumberAxis("Moment [kNm]"));
        maxis.setInverted(true);
//        maxis.setRange(-500, 500);
        maxis.setFixedDimension(50);
        NumberAxis aaxis;
        plot.setRangeAxis(1, aaxis = new NumberAxis("Axial [kN]"));
        aaxis.setInverted(true);
        aaxis.setFixedDimension(50);
//        aaxis.setRange(-1000, 1000);
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);
        NumberAxis xaxis;

        plot.setDomainAxis(xaxis = new NumberAxis("Location[m]"));
        xaxis.setRange(0, beam.getLength());
        XYLineAndShapeRenderer re = new XYLineAndShapeRenderer(true, true);
        XYLineAndShapeRenderer re1 = new XYLineAndShapeRenderer(true, true);
        re.setSeriesPaint(0, Color.RED); //moment
        re.setSeriesPaint(1, Color.RED); //moment
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

        if (aaxis.getUpperBound() > 1000 || aaxis.getLowerBound() < -1000) {
            aaxis.setRange(-1000, 1000);
        }

        if (maxis.getUpperBound() > 500 || maxis.getLowerBound() < -500) {
            maxis.setRange(-500, 500);
        }
        JunChartUtil.matchRangeZero(plot);
        JFreeChart chart = new JFreeChart(plot);

        if (svgdir != null) {
            int w = 400, h = 200;
            try {
                if (!Files.exists(svgdir)) {
                    Files.createDirectory(svgdir);
                }

                JunChartUtil.svg(svgdir
                        .resolve(testname + "_" + beam.getName() + ".svg"),
                        500, 250, chart);
            } catch (IOException ex) {
                Logger.getLogger(T121BeamNMDistributionZoom.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JunChartUtil.show(testname + "_" + beam.getName(), chart);
        }
    }

}
