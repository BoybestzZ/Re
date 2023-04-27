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
public class T120BeamNMDistribution {

    private static final Logger logger = Logger.getLogger(T120BeamNMDistribution.class.getName());
    static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/T120BeamNMDistribution");

    public static void main(String[] args) {
        try {
//            main(EdefenseInfo.D01Q01.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D01Q02.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D01Q03.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D01Q04.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D01Q05.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D01Q06.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D01Q08.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D01Q09.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D01Q10.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D01Q11.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D02Q01.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D02Q02.toString(), EdefenseInfo.Beam3);
//            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.Beam3);
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

            main(EdefenseInfo.D01Q01.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D01Q02.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D01Q03.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D01Q04.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D01Q05.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D01Q06.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D01Q08.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D01Q09.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D01Q10.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D01Q11.toString(), EdefenseInfo.BeamB);

            main(EdefenseInfo.D02Q01.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D02Q02.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D02Q05.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D02Q06.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D02Q07.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D02Q08.toString(), EdefenseInfo.BeamB);

            main(EdefenseInfo.D03Q01.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D03Q02.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D03Q03.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D03Q04.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D03Q05.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D03Q06.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D03Q08.toString(), EdefenseInfo.BeamB);
            main(EdefenseInfo.D03Q09.toString(), EdefenseInfo.BeamB);

        } catch (SQLException ex) {
            Logger.getLogger(T120BeamNMDistribution.class.getName()).log(Level.SEVERE, null, ex);
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
//         応力が最大となる時刻を探索する。
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
        NumberAxis aaxis;
        plot.setRangeAxis(1, aaxis = new NumberAxis("Axial [kN]"));
        aaxis.setInverted(true);
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);

        plot.setDomainAxis(new NumberAxis("location[m]"));
        XYLineAndShapeRenderer re = new XYLineAndShapeRenderer(true, true);
        XYLineAndShapeRenderer re1 = new XYLineAndShapeRenderer(true, true);
        re.setSeriesPaint(0, Color.RED); //moment
        re.setSeriesPaint(1, Color.RED); //moment
        re.setSeriesPaint(2, Color.RED); //momenttotal
        re.setSeriesPaint(3, Color.ORANGE); //momenttotal
        re.setSeriesPaint(4, Color.ORANGE); //momenttotal
        re1.setSeriesPaint(0, Color.BLUE); //axial
        re1.setSeriesPaint(1, Color.BLUE);
        re1.setSeriesPaint(2, Color.BLUE);

        plot.setRenderer(0, re);
        plot.setRenderer(1, re1);
        plot.setRangeZeroBaselineVisible(true);
        matchRangeZero(plot);
        JFreeChart chart = new JFreeChart(plot);

        if (svgdir != null) {
            int w = 400, h = 200;
            try {
                if (!Files.exists(svgdir)) {
                    Files.createDirectory(svgdir);
                }

                JunChartUtil.svg(svgdir
                        .resolve(testname + "t_" + beam.getName() + ".svg"), // 時刻歴から作ってるので t という名称を加えておく。ただしいまできているものは tがついていない。
                        500, 250, chart);
            } catch (IOException ex) {
                Logger.getLogger(T120BeamNMDistribution.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JunChartUtil.show(testname + "t_" + beam.getName(), chart);
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
