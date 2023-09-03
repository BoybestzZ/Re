/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea05;

import jun.res23.ed.ed08.*;
import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.res23.ed.util.EdefenseInfo;
import static jun.res23.ed.util.EdefenseInfo.*;
import jun.res23.ed.util.EdefenseKasinInfo;
import jun.res23.ed.util.ElementInfo;
import jun.res23.ed.util.SectionInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import org.jfree.data.xy.XYSeries;

/**
 * B220→B230 NSにもEWにも対応するように。NSはLA3S2、EWはLABS2の曲げモーメントが最大となった時刻を選択する。 B210→B220
 * 元データを T231からT400に変更。もともとこのプログラムはEW専用。部材の切り替えも手動。
 *
 *
 * @author jun
 *
 */
public class B230ElementShearLocalStiffness1 {

    //  private static final String T300Schema = T300Integ2StoryDisp.outputSchema;//"T300Integ2StoryDisp"
    public static final String dburl = "jdbc:h2:C:\\Users\\75496\\Documents\\E-Defense\\test/res22ed06v230815J";
    public static final String nmtable = "T400TimeHistoryNM";
    public static final String ed08dburl = "jdbc:h2:C:\\Users\\75496\\Documents\\E-Defense\\test/res22ed08v230815J;IFEXISTS=TRUE";
    public static final String outputTable = "B230ElementShearLocalStiffness";
    public static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed08防災科研/B230ElementShearLocalStiffness");

    private static final Logger logger = Logger.getLogger(B230ElementShearLocalStiffness1.class.getName());

    public static void main(String[] args) {
        main(EdefenseInfo.BeamA, "ew");
        main(EdefenseInfo.BeamB, "ew");
        main(EdefenseInfo.Beam3, "ns");
        main(EdefenseInfo.Beam4, "ns");

        main(EdefenseInfo.Column2FA3, "ew");
        main(EdefenseInfo.Column2FA4, "ew");
        main(EdefenseInfo.Column2FB3, "ew");
        main(EdefenseInfo.Column2FB4, "ew");
        main(EdefenseInfo.Column3FA3, "ew");
        main(EdefenseInfo.Column3FA4, "ew");
        main(EdefenseInfo.Column3FB3, "ew");
        main(EdefenseInfo.Column3FB4, "ew");

        main(EdefenseInfo.Column2FA3, "ns");
        main(EdefenseInfo.Column2FA4, "ns");
        main(EdefenseInfo.Column2FB3, "ns");
        main(EdefenseInfo.Column2FB4, "ns");
        main(EdefenseInfo.Column3FA3, "ns");
        main(EdefenseInfo.Column3FA4, "ns");
        main(EdefenseInfo.Column3FB3, "ns");
        main(EdefenseInfo.Column3FB4, "ns");
        
        
        

    }

    public static void main(ElementInfo element, String ns) {
        try {
//            ElementInfo element = EdefenseInfo.Column2FA3;
//            ElementInfo element = EdefenseInfo.Column3FA3;
//            ElementInfo element = EdefenseInfo.Column3FA4;
//                        ElementInfo element = EdefenseInfo.Column2FA4;
//            ElementInfo element = EdefenseInfo.Beam3;
//            ElementInfo element = EdefenseInfo.BeamA;
//            ElementInfo element = ;
//            String ns = "ew";

            clearOutputTable(element,ns, /*dropTable*/ false);
            JFreeChart[] positiveCharts = createChart(element, ns, true);
            JFreeChart[] negativeCharts = createChart(element, ns, false);
            if (svgdir != null) {
                try {
                    if (!Files.exists(svgdir)) {
                        Files.createDirectory(svgdir);
                    }

                    final Path svgfile = svgdir.resolve(element.getName() + "_" + ns + ".svg");

                    //            JunChartUtil.show(chart);
                    JunChartUtil.svg(svgfile, 500, 350, new JFreeChart[][]{positiveCharts, negativeCharts});
                } catch (IOException ex) {
                    Logger.getLogger(B230ElementShearLocalStiffness1.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JunChartUtil.show(new JFreeChart[][]{positiveCharts, negativeCharts});
            }
        } catch (SQLException ex) {
            Logger.getLogger(B230ElementShearLocalStiffness1.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void clearOutputTable(ElementInfo element, String ns, boolean dropTable) throws SQLException {

        Connection con08 = DriverManager.getConnection(ed08dburl, "junapp", "");
        Statement st08 = con08.createStatement();
        if (dropTable) {
            st08.executeUpdate("drop table if exists \"" + outputTable + "\"");
        }
        st08.executeUpdate("create table if not exists \"" + outputTable + "\" (TIMESTAMP timestamp,\"ElementName\" varchar, DIRECTION varchar,  \"TESTNAME\" varchar, \"PositiveDirection\" boolean, "
                + "\"TimePerTest[s]\" real , \"MomentS2[kNm]\" real,\"MomentS3[kNm]\" real, \"ShearForce[kN]\" real,\"Story2Drift[mm]\" real, \"2ndStoryDrift[mm]\" real, \"3rdStoryDrift[mm]\" real, \"LocalStiffness[kN/mm]\" real)");
        st08.executeUpdate("delete \"" + outputTable + "\" where \"ElementName\"='" + element.getName() + "' and DIRECTION='"+ns+"'");
        con08.close();

    }

    public static JFreeChart[] createChart(ElementInfo element, String ns, boolean directionPositive) throws SQLException {
        SectionInfo[] sections = element.getSections();
        String NS = ns.toUpperCase();
        if (element.isColumn()) { // 柱の場合は NSorEW方向のモーメントを計算。
            return createChart(element.getName(), ns, sections[1].getName(), sections[2].getName(),
                    element.getLocation(2) - element.getLocation(1),
                    directionPositive, "BendingMoment" + NS + "PerTest[kNm]", 0.0);
        } else {
            return createChart(element.getName(), ns, sections[1].getName(), sections[2].getName(),
                    element.getLocation(2) - element.getLocation(1),
                    directionPositive, "BendingMomentPerTest[kNm]", sections[1].getHeight() * 0.5 + 0.11 * 0.5);
        }
    }

    public static JFreeChart[] createChart(String elementName, String ns, String sectionName1, String sectionName2, double shearCalculationSpan, boolean directionPositive, String momentColumnName, double slabArmLength) throws SQLException {

        // 方向 NS も指定しないといけない。
        String XY;
        String baseSection;

        if (ns.equals("ns")) {
            XY = "X";
            baseSection = EdefenseInfo.LA3S2.getName();
        } else {
            XY = "Y";
            baseSection = EdefenseInfo.LABS2.getName();
        }
        XYSeries s = new XYSeries("localstiffness");

        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        Statement st = con.createStatement();
        Connection con08 = DriverManager.getConnection(ed08dburl, "junapp", "");
        Statement st08 = con08.createStatement();

        EdefenseKasinInfo[] kumamotos = new EdefenseKasinInfo[]{
            D01Q02,
            D02Q01, D02Q06, D03Q02,};

        EdefenseKasinInfo[] tohoku = new EdefenseKasinInfo[]{
            D01Q03, D02Q02, D02Q08, D03Q03
        };
        EdefenseKasinInfo[] kobes = new EdefenseKasinInfo[]{
            D01Q04, D01Q08, D01Q10,
            D02Q03, D03Q04, D03Q08
        };

        EdefenseKasinInfo[] randoms = new EdefenseKasinInfo[]{
            D01Q01, D01Q09, D01Q11,
            D02Q05, D03Q01, D03Q09
        };

        DefaultCategoryDataset datasetStiffness = new DefaultCategoryDataset();
        DefaultCategoryDataset datasetDisp = new DefaultCategoryDataset();
        DefaultCategoryDataset datasetForce = new DefaultCategoryDataset();
        DefaultCategoryDataset datasetDispAbs = new DefaultCategoryDataset();
        DefaultCategoryDataset datasetForceAbs = new DefaultCategoryDataset();

        st08.executeUpdate("create table if not exists \"" + outputTable + "\" (TIMESTAMP timestamp,\"ElementName\" varchar, DIRECTION varchar,  \"TESTNAME\" varchar, \"PositiveDirection\" boolean, "
                + "\"TimePerTest[s]\" real , \"MomentS2[kNm]\" real,\"MomentS3[kNm]\" real, \"ShearForce[kN]\" real,\"Story2Drift[mm]\" real, \"2ndStoryDrift[mm]\" real, \"3rdStoryDrift[mm]\" real, \"LocalStiffness[kN/mm]\" real)");

        String timestamp = LocalDateTime.now().toString();
        double firstStiffness = Double.NaN;
        for (EdefenseKasinInfo test : alltests) {

            String desc = directionPositive ? "desc" : "";
            ResultSet rs = st.executeQuery("select \"TimePerTest[s]\",\"BendingMomentPerTest[kNm]\", \"AxialForcePerTest[kN]\""
                    + " from \"" + nmtable + "\".\"" + baseSection + "\" "
                    + " where TESTNAME='" + test.getName() + "t' and \"TimePerTest[s]\" < (120+" + test.getNiedTimeDiffSeconds() + ") "
                    + "order by \"BendingMomentPerTest[kNm]\" " + desc + " limit 1");

            rs.next();
            double time = rs.getDouble(1); // found the time where the bending moment is the maximum.
            time = Math.round(time * 100) / 100.0;

            rs = st.executeQuery("select \"TimePerTest[s]\",\"" + momentColumnName + "\", \"AxialForcePerTest[kN]\""
                    + " from \"" + nmtable + "\".\"" + sectionName1 + "\" "
                    + "where \"TimePerTest[s]\" =" + time + "  and TESTNAME='" + test.getTestName() + "t'");
            rs.next();

            double momentS2 = rs.getDouble(2);
            double momentTotalS2 = momentS2 + rs.getDouble(3) * slabArmLength;// 柱の場合はslabArmLengthは0になってる。
            String sql;
            rs = st.executeQuery(sql = "select \"TimePerTest[s]\",\"" + momentColumnName + "\",\"AxialForcePerTest[kN]\""
                    + " from \"" + nmtable + "\".\"" + sectionName2 + "\" "
                    + "where \"TimePerTest[s]\" =" + time + "  and TESTNAME='" + test.getTestName() + "t'");
            System.out.println(sql);
            rs.next();
            double momentS3 = rs.getDouble(2);
            double momentTotalS3 = momentS3 + rs.getDouble(3) * slabArmLength; // 柱の場合はslabArmLengthは0になってる。

            // double shearSteel = (momentS3 - momentS2) / shearCalculationSpan; //kN
            double shearTotal = (momentTotalS3 - momentTotalS2) / shearCalculationSpan;

            rs = st08.executeQuery(sql = "select"
                    + " avg(\"Story2DispW_" + XY + "[mm]\"+\"Story3DispW_" + XY + "[mm]\")*0.5, "
                             + "avg(\"Story2DispW_" + XY + "[mm]\"), avg(\"Story3DispW_" + XY + "[mm]\"), "
                    + " from \"" + test.getTestName() + "\" where \"Time[s]\"<2.0");
            rs.next();
            double niedavg = rs.getDouble(1);
            double niedavg2 = rs.getDouble(2);
            double niedavg3 = rs.getDouble(3);
            
            // 一番近い時刻の値を持ってくる。
            // rs = st08.executeQuery(sql = "select \"Time[s]\", (\"Story2DispW_" + XY + "[mm]\"+\"Story3DispW_" + XY + "[mm]\")*0.5 "
            //       + " from \"" + test.getTestName() + "\" order by abs(\"Time[s]\"-(" + (time + test.getNiedTimeDiffSeconds()) + ")) limit 1");
            // 前後 プラスマイナス 0.02秒の範囲での絶対値が最大となるものを探してくる。
            rs = st08.executeQuery(sql = "select \"Time[s]\", "
                    + "(\"Story2DispW_" + XY + "[mm]\"+\"Story3DispW_" + XY + "[mm]\")*0.5," // Calculate the average of the two inter story disp.
                    + " \"Story2DispW_" + XY + "[mm]\", \"Story3DispW_" + XY + "[mm]\"" // Output separately the 2nd and 3rd interstory drift.
                    + " from \"" + test.getTestName() + "\" where \"Time[s]\" between " + (time + test.getNiedTimeDiffSeconds() - 0.02) + " and " + (time + test.getNiedTimeDiffSeconds() + 0.02)
                    + " order by abs((\"Story2DispW_" + XY + "[mm]\"+\"Story3DispW_" + XY + "[mm]\")*0.5) desc limit 1;");

            System.out.println(sql);
            rs.next();
            double niedtime = rs.getDouble(1);
            double disp = rs.getDouble(2) - niedavg; // unit:mm
            double disp2=rs.getDouble(3) - niedavg2; // unit:mm
            double disp3=rs.getDouble(4)-niedavg3; 
            logger.log(Level.INFO, "NIED=" + disp + " @ niedtime= " + niedtime + " , time=" + (time + test.getNiedTimeDiffSeconds()));

            if (test.getName().endsWith("Y)")) {
                continue;
            }

            // if (isTestsIn(test, randoms)) {
//                    datasetRandoms.addValue(shearTotal, "shearTotal", test.getTestName() + test.getWaveName());
//                }
            // if (!test.getWaveName().endsWith("Y")) {
            String wavetype = "notset";
            if (test.getWaveName().startsWith("Ran")) {
                wavetype = "random";
            } else if (test.getWaveName().startsWith("Kobe")) {
                wavetype = "Kobe";
            } else if (test.getWaveName().startsWith("KMM")) {
                wavetype = "kumamoto";
            } else if (test.getWaveName().startsWith("FKS")) {
                wavetype = "tohoku";
            }
            datasetStiffness.addValue(shearTotal / disp, wavetype, test.getTestName() + test.getWaveName());
            datasetDisp.addValue(disp, wavetype, test.getTestName() + test.getWaveName());
            datasetForce.addValue(shearTotal, wavetype, test.getTestName() + test.getWaveName());
            datasetDispAbs.addValue(Math.abs(disp), wavetype, test.getTestName() + test.getWaveName());
            datasetForceAbs.addValue(Math.abs(shearTotal), wavetype, test.getTestName() + test.getWaveName());
            if (Double.isNaN(firstStiffness)) {
                firstStiffness = shearTotal / disp; // 軸の正負を定めるため、一番最初の値を覚えておく。
            }
//            }
            st08.executeUpdate("insert into \"" + outputTable + "\" "
                    + "(TIMESTAMP,\"ElementName\", DIRECTION, TESTNAME,\"PositiveDirection\",\"TimePerTest[s]\", \"MomentS2[kNm]\", \"MomentS3[kNm]\", \"ShearForce[kN]\","
                    + "\"Story2Drift[mm]\",\"2ndStoryDrift[mm]\", \"3rdStoryDrift[mm]\", \"LocalStiffness[kN/mm]\") "
                    + "values ('" + timestamp + "','" + elementName + "','" + ns + "','" + test.getTestName() + "'," + directionPositive + "," + time + "," + momentS2 + "," + momentS3
                    + "," + shearTotal + "," + disp + ", " + disp2 + ", " + disp3 + ", " + shearTotal / disp
                    + ")");

        }

        CategoryAxis xaxis = new CategoryAxis("Test");
        xaxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
        NumberAxis yaxis = new NumberAxis("LocalShearStiffness [kN/mm]");

        LineAndShapeRenderer renderer = new LineAndShapeRenderer(false, true);
        renderer.setSeriesPaint(3, Color.MAGENTA);
        CategoryPlot plot = new CategoryPlot(datasetStiffness, xaxis, yaxis, renderer);
        plot.setDomainGridlinesVisible(true);
        yaxis.setFixedDimension(50);
        if (firstStiffness > 0) {
            yaxis.setLowerBound(0);
        } else {
            yaxis.setUpperBound(0);
        }
        JFreeChart chart = new JFreeChart(plot);

        LineAndShapeRenderer rendererDisp = new LineAndShapeRenderer(false, true);
        rendererDisp.setSeriesPaint(3, Color.MAGENTA);
        CategoryPlot dispPlot = new CategoryPlot(datasetDisp, xaxis, new NumberAxis("Disp[mm]"), rendererDisp);
        dispPlot.setDomainGridlinesVisible(true);
        dispPlot.getRangeAxis().setFixedDimension(50);
        JFreeChart dispChart = new JFreeChart(dispPlot);

        LineAndShapeRenderer rendererForce = new LineAndShapeRenderer(false, true);
        rendererForce.setSeriesPaint(3, Color.MAGENTA);
        CategoryPlot forcePlot = new CategoryPlot(datasetForce, xaxis, new NumberAxis("Force[kN]"), rendererForce);
        forcePlot.setDomainGridlinesVisible(true);
        forcePlot.getRangeAxis().setFixedDimension(50);
        JFreeChart forceChart = new JFreeChart(forcePlot);

        LineAndShapeRenderer rendererDispAbs = new LineAndShapeRenderer(false, true);
        rendererDisp.setSeriesPaint(3, Color.MAGENTA);
        CategoryPlot dispPlotAbs = new CategoryPlot(datasetDispAbs, xaxis, new LogarithmicAxis("Disp[mm]"), rendererDispAbs);
        dispPlotAbs.setDomainGridlinesVisible(true);
        dispPlotAbs.getRangeAxis().setFixedDimension(50);
        JFreeChart dispAbsChart = new JFreeChart(dispPlotAbs);

        LineAndShapeRenderer rendererForceAbs = new LineAndShapeRenderer(false, true);
        rendererForceAbs.setSeriesPaint(3, Color.MAGENTA);
        CategoryPlot forcePlotAbs = new CategoryPlot(datasetForceAbs, xaxis, new LogarithmicAxis("Force[kN]"), rendererForceAbs);
        forcePlotAbs.setDomainGridlinesVisible(true);
        forcePlotAbs.getRangeAxis().setFixedDimension(50);
        JFreeChart forceAbsChart = new JFreeChart(forcePlotAbs);

        con.close();

        con08.close();

        return new JFreeChart[]{chart, dispAbsChart, dispChart, forceAbsChart, forceChart};

    }

    public static boolean isTestsIn(EdefenseKasinInfo test, EdefenseKasinInfo[] tests) {
        for (EdefenseKasinInfo t : tests) {
            if (t == test) {
                return true;
            }
        }
        return false;

    }

}
