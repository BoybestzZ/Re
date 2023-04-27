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
import jun.res23.ed.util.EdefenseKasinInfo;
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
public class T106GraphSectionStrains {

    private static final Logger logger = Logger.getLogger(T106GraphSectionStrains.class.getName());
    static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/T106SetionStrains");

    public static void main(String[] args) {
        
        
        try {
            
            BeamSectionInfo[] sections={
                EdefenseInfo.LA3S1,EdefenseInfo.LA3S2,EdefenseInfo.LA3S3,EdefenseInfo.LA3S4,EdefenseInfo.LA3S5,
                EdefenseInfo.LABS1,EdefenseInfo.LABS2,EdefenseInfo.LABS3,EdefenseInfo.LABS4,EdefenseInfo.LABS5            
            };
            EdefenseKasinInfo[] all=EdefenseInfo.alltests;
            for (EdefenseKasinInfo kasin:all) {
                for (BeamSectionInfo section:sections) {
                    main(kasin.getName(),section);
                }
            }

            
            
            

        } catch (SQLException ex) {
            Logger.getLogger(T106GraphSectionStrains.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void modify(JFreeChart chart) {

        AxisSpace space = new AxisSpace();
        space.setLeft(70);
        space.setRight(70);

        chart.getXYPlot().setFixedRangeAxisSpace(space);
    }

    public static void main(String testname, BeamSectionInfo section) throws SQLException {

        JFreeChart chartNMforce = T100chartNMTimeHistory.chartNMTimeHistory(testname, section);
        JFreeChart chartNMStrain = T103GraphBendingAxialStrain.chartBendingAxialStrainInBeamSection(testname, section);
        JFreeChart chartFlangeStrain = T104GraphFlangeNMInPlane.chartFlangeMNInPlane(testname, section);
        JFreeChart chartStrains = T105GraphStrainInSection.chartStrainInSection(testname, section);

        modify(chartNMforce);
        modify(chartNMStrain);
        modify(chartFlangeStrain);
        modify(chartStrains);

        if (true) {
            int w = 400, h = 200;
            try {
                if (!Files.exists(svgdir)) {
                    Files.createDirectory(svgdir);
                }

                JunChartUtil.svg(svgdir
                        .resolve(testname + "_" + section.getName() + ".svg"),
                        400, 200, new JFreeChart[]{chartNMforce, chartNMStrain, chartFlangeStrain, chartStrains});
            } catch (IOException ex) {
                Logger.getLogger(T106GraphSectionStrains.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JunChartUtil.show(testname + "_" + section.getName(), new JFreeChart[][]{{chartNMforce}, {chartNMStrain},{chartFlangeStrain}, {chartStrains}});
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
