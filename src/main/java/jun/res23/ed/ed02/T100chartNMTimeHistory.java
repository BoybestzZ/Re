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
import jun.fourier.FFTv2;
import static jun.res23.ed.ed02.R200Resample.databaseDir;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.ColumnSectionInfo;
import org.apache.commons.math3.stat.StatUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author jun
 * @deprecated これはこれで正しいが、図を書き直すならば ed06分析T のデータベース res22ed06.mv.db のなかに、
"T231TimeHistoryNM"スキーマの中に各断面の応力時刻歴が入っているので、それを使ったほうが速い。 
 */
public class T100chartNMTimeHistory {

    private static final Logger logger = Logger.getLogger(T100chartNMTimeHistory.class.getName());
    public static final String timeHistoryTTable = "R210Resample";
    public static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/T100NMTimeHistory");

    public static void main(String[] args) {
        try {
            main(EdefenseInfo.D02Q03.toString(), EdefenseInfo.LABS3);
        } catch (SQLException ex) {
            Logger.getLogger(T100chartNMTimeHistory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String testname, BeamSectionInfo section) throws SQLException {

        JFreeChart chart = chartNMTimeHistory(testname, section);

        if (false) {
            try {
                if (!Files.exists(svgdir)) {
                    Files.createDirectory(svgdir);
                }
                JunChartUtil.svg(chart, svgdir.resolve(testname + "_" + section.getName() + ".svg"), 400, 200);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }

        JunChartUtil.show(testname + "_" + section.getName(), chart);

    }

    public static JFreeChart chartNMTimeHistory(String testname, BeamSectionInfo section) throws SQLException {

        double[][] ar = calculateBeamNM(testname, section);
        logger.log(Level.INFO, "aveAxial=" + StatUtils.mean(ar[1]) + "; aveMoment=" + StatUtils.mean(ar[2]) + ";");
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

        XYPlot plot = new XYPlot();

        plot.setDataset(0, cm);
        plot.setDataset(1, ca);
        plot.setDomainAxis(new NumberAxis("Time [s]"));
        plot.setRangeAxis(0, new NumberAxis("Moment [kNm]"));
        plot.setRangeAxis(1, new NumberAxis("AxialForce [kN]"));
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);
        XYLineAndShapeRenderer r0 = new XYLineAndShapeRenderer(true, false);
        XYLineAndShapeRenderer r1 = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer(0, r0);
        plot.setRenderer(1, r1);

        JunChartUtil.matchRangeZero(plot);
        JFreeChart chart = new JFreeChart(plot);

        chart.setBackgroundPaint(Color.WHITE);

        return chart;

    }

    /**
     * 梁の軸力および曲げモーメントを算出する。各加振時の初期値を０としている。また、床スラブの効果は考慮していない。
     *
     * @param testname
     * @param section
     * @return [0]=N[kN] [1]=Mx[kNm] 現在は弱軸回りのモーメントは計算していない。
     */
    public static double[][] calculateBeamNM(String testname, BeamSectionInfo section) throws SQLException {

        String dburl = "jdbc:h2:file://" + databaseDir.resolve(testname + "q");

//        SectionInfo section = EdefenseInfo.LA3S1;
        double EA4 = -1e-3 * 1e-6 * 0.25 * section.getE() * section.getA(); // kN / με
        double EZx4 = 1e-3 * 1e-6 * 0.25 * section.getE() * section.getInnerZx();//  kNm /με 強軸周り
        double EZy4 = 1e-3 * 1e-6 * 0.25 * section.getE() * section.getZy();//  kNm /με 弱軸周り

        String axialForce = "(" + EA4 + ")*("
                + "\"" + section.getULname() + "_Strain[με]\"+"
                + "\"" + section.getURname() + "_Strain[με]\"+"
                + "\"" + section.getLLname() + "_Strain[με]\"+"
                + "\"" + section.getLRname() + "_Strain[με]\""
                + ")"; // 引張が正。 (ひずみ値は圧縮が正であるが、EA4を負にしているため、引張軸力が正となる。）
        String bendingMomentX = "(" + EZx4 + ")*("
                + "\"" + section.getULname() + "_Strain[με]\"+"
                + "\"" + section.getURname() + "_Strain[με]\"-"
                + "\"" + section.getLLname() + "_Strain[με]\"-"
                + "\"" + section.getLRname() + "_Strain[με]\""
                + ")"; // 上側圧縮、すなわち下側引張が正。
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        Statement st = con.createStatement();
        double aveAxial = 0;
        double aveMoment = 0;
//            aveAxial=-516.8244321318311; aveMoment=30.26672208983202;
        ResultSet rs;
        rs = st.executeQuery("select avg(" + axialForce + ")-(" + aveAxial + "),avg(" + bendingMomentX + ")-(" + aveMoment + ") from \"" + timeHistoryTTable + "\""
                + " where \"TIME[s]\"<2.0");
        rs.next();
        aveAxial = rs.getDouble(1);
        aveMoment = rs.getDouble(2);
        rs = st.executeQuery("select \"TIME[s]\",(" + axialForce + ")-(" + aveAxial + "),(" + bendingMomentX + ")-(" + aveMoment + ") from \"" + timeHistoryTTable + "\"");

        double[][] ar = ResultSetUtils.createSeriesArray(rs);

        con.close();
        return ar;

    }

    /**
     * ある時刻の梁の軸力および曲げモーメントを算出する。初期値を０としている。また、床スラブの効果は考慮していない。
     *
     * @param testname
     * @param section
     * @param t 時刻
     * @return [0]=N[kN] [1]=Mx[kNm] 現在は弱軸回りのモーメントは計算していない。
     */
    public static double[] calculateBeamNM(String testname, BeamSectionInfo section, double t) throws SQLException {

        String dburl = "jdbc:h2:file://" + databaseDir.resolve(testname + "q");

//        SectionInfo section = EdefenseInfo.LA3S1;
        double EA4 = -1e-3 * 1e-6 * 0.25 * section.getE() * section.getA(); // kN / με
        double EZx4 = 1e-3 * 1e-6 * 0.25 * section.getE() * section.getInnerZx();//  kNm /με 強軸周り
        double EZy4 = 1e-3 * 1e-6 * 0.25 * section.getE() * section.getZy();//  kNm /με 弱軸周り

        String axialForce = "(" + EA4 + ")*("
                + "\"" + section.getULname() + "_Strain[με]\"+"
                + "\"" + section.getURname() + "_Strain[με]\"+"
                + "\"" + section.getLLname() + "_Strain[με]\"+"
                + "\"" + section.getLRname() + "_Strain[με]\""
                + ")"; // 引張が正。 (ひずみ値は圧縮が正であるが、EA4を負にしているため、引張軸力が正となる。）
        String bendingMomentX = "(" + EZx4 + ")*("
                + "\"" + section.getULname() + "_Strain[με]\"+"
                + "\"" + section.getURname() + "_Strain[με]\"-"
                + "\"" + section.getLLname() + "_Strain[με]\"-"
                + "\"" + section.getLRname() + "_Strain[με]\""
                + ")"; // 上側圧縮、すなわち下側引張が正。
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        Statement st = con.createStatement();
        double aveAxial = 0;
        double aveMoment = 0;
//            aveAxial=-516.8244321318311; aveMoment=30.26672208983202;
        ResultSet rs;
        rs = st.executeQuery("select avg(" + axialForce + ")-(" + aveAxial + "),avg(" + bendingMomentX + ")-(" + aveMoment + ") from \"" + timeHistoryTTable + "\""
                + " where \"TIME[s]\"<2.0");
        rs.next();
        aveAxial = rs.getDouble(1);
        aveMoment = rs.getDouble(2);
        rs = st.executeQuery("select \"TIME[s]\",(" + axialForce + ")-(" + aveAxial + "),(" + bendingMomentX + ")-(" + aveMoment + ") from \"" + timeHistoryTTable + "\""
                + "where \"TIME[s]\" = " + t);

        double[][] ar = ResultSetUtils.createSeriesArray(rs);

        con.close();
        return new double[]{ar[1][0], ar[2][0]};

    }

    /**
     * 梁の軸力および曲げモーメントを算出する。初期値を０としている。また、床スラブの効果は考慮していない。
     *
     * @param testname
     * @param section
     * @return [0]=N[kN] [1]=Mx[kNm] 現在は弱軸回りのモーメントは計算していない。
     */
    public static double[][] calculateBeamNMStrain(String testname, BeamSectionInfo section) throws SQLException {

        String dburl = "jdbc:h2:file://" + databaseDir.resolve(testname + "q");

//        SectionInfo section = EdefenseInfo.LA3S1;
        double EA4 = -0.25;//*-1e-3 * 1e-6* section.getE() * section.getA(); // kN / με
        double EZx4 = 0.25;//*1e-3 * 1e-6  * section.getE() * section.getZx();//  kNm /με 強軸周り
        double EZy4 = 0.25;// * 1e-3 * 1e-6  * section.getE() * section.getZy();//  kNm /με 弱軸周り

        String axialForce = "(" + EA4 + ")*("
                + "\"" + section.getULname() + "_Strain[με]\"+"
                + "\"" + section.getURname() + "_Strain[με]\"+"
                + "\"" + section.getLLname() + "_Strain[με]\"+"
                + "\"" + section.getLRname() + "_Strain[με]\""
                + ")"; // 引張が正。 (ひずみ値は圧縮が正であるが、EA4を負にしているため、引張軸力が正となる。）
        String bendingMomentX = "(" + EZx4 + ")*("
                + "\"" + section.getULname() + "_Strain[με]\"+"
                + "\"" + section.getURname() + "_Strain[με]\"-"
                + "\"" + section.getLLname() + "_Strain[με]\"-"
                + "\"" + section.getLRname() + "_Strain[με]\""
                + ")"; // 上側圧縮、すなわち下側引張が正。
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        Statement st = con.createStatement();
        double aveAxial = 0;
        double aveMoment = 0;
//            aveAxial=-516.8244321318311; aveMoment=30.26672208983202;
        ResultSet rs;
        rs = st.executeQuery("select avg(" + axialForce + ")-(" + aveAxial + "),avg(" + bendingMomentX + ")-(" + aveMoment + ") from \"" + timeHistoryTTable + "\""
                + " where \"TIME[s]\"<2.0");
        rs.next();
        aveAxial = rs.getDouble(1);
        aveMoment = rs.getDouble(2);
        rs = st.executeQuery("select \"TIME[s]\",(" + axialForce + ")-(" + aveAxial + "),(" + bendingMomentX + ")-(" + aveMoment + ") from \"" + timeHistoryTTable + "\"");

        double[][] ar = ResultSetUtils.createSeriesArray(rs);

        con.close();
        return ar;

    }

    /**
     * 柱限定
     *
     * @param testname
     * @param section
     * @return [0]=N [1]=Mns [2]=Mew [kN]or[kNm]
     */
    public static double[][] calculateColumnNM(String testname, ColumnSectionInfo section) throws SQLException {

        String dburl = "jdbc:h2:file://" + databaseDir.resolve(testname + "q");

//        SectionInfo section = EdefenseInfo.LA3S1;
        double EA4 = -1e-3 * 1e-6 * 0.25 * section.getE() * section.getA(); // kN / με
        double EZns4 = 1e-3 * 1e-6 * 0.25 * section.getE() * section.getZns();//  kNm /με 強軸周り
        double EZew4 = 1e-3 * 1e-6 * 0.25 * section.getE() * section.getZew();//  kNm /με 弱軸周り

        String axialForce = "(" + EA4 + ")*("
                + "\"" + section.getNorthName() + "_Strain[με]\"+"
                + "\"" + section.getEastName() + "_Strain[με]\"+"
                + "\"" + section.getWestName() + "_Strain[με]\"+"
                + "\"" + section.getSouthName() + "_Strain[με]\""
                + ")"; // 引張が正。 (ひずみ値は圧縮が正であるが、EA4を負にしているため、引張軸力が正となる。）
        String bendingMomentNS = "(" + EZns4 + ")*("
                + "\"" + section.getNorthName() + "_Strain[με]\"-"
                + "\"" + section.getSouthName() + "_Strain[με]\""
                + ")"; // 北側圧縮、すなわち南側引張が正。

        String bendingMomentEW = "(" + EZew4 + ")*("
                + "\"" + section.getEastName() + "_Strain[με]\"-"
                + "\"" + section.getWestName() + "_Strain[με]\""
                + ")"; // 東側圧縮、すなわち西側引張が正。        

        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        Statement st = con.createStatement();
        double aveAxial = 0;
        double aveMomentNS = 0;
        double aveMomentEW = 0;
//            aveAxial=-516.8244321318311; aveMoment=30.26672208983202;
        ResultSet rs;
        rs = st.executeQuery("select avg(" + axialForce + ")-(" + aveAxial + "),"
                + "avg(" + bendingMomentNS + ")-(" + aveMomentNS + "),"
                + "avg(" + bendingMomentEW + ")-(" + aveMomentEW + "),"
                + " from \"" + timeHistoryTTable + "\""
                + " where \"TIME[s]\"<2.0");
        rs.next();
        aveAxial += rs.getDouble(1);
        aveMomentNS += rs.getDouble(2);
        aveMomentEW += rs.getDouble(2);
        rs = st.executeQuery("select \"TIME[s]\",(" + axialForce + ")-(" + aveAxial + "),"
                + "(" + bendingMomentNS + ")-(" + aveMomentNS + "),"
                + "(" + bendingMomentEW + ")-(" + aveMomentEW + ")"
                + " from \"" + timeHistoryTTable + "\"");

        double[][] ar = ResultSetUtils.createSeriesArray(rs);

        con.close();
        return ar;

    }

}
