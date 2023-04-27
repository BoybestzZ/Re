/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed02;

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
import org.apache.commons.math3.stat.StatUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author jun
 */
public class T101SectionHisteresisT {

    private static final Logger logger = Logger.getLogger(T101SectionHisteresisT.class.getName());
    public static final String timeHistoryTTable = "R210Resample";

    public static void main(String[] args) {
        main(EdefenseInfo.D02Q06.toString(), EdefenseInfo.LABS2);
    }

    public static void main(String testname, BeamSectionInfo section) {

        try {
            String dburl = "jdbc:h2:file://" + databaseDir.resolve(testname + "q");

//        SectionInfo section = EdefenseInfo.LA3S1;
            double EA4 = -1e-3 * 1e-6 * 0.25 * section.getE() * section.getA(); // kN / με
            double EZ4 = 1e-3 * 1e-6 * 0.25 * section.getE() * section.getInnerZx();//  kNm /με

            String axialForce = "(" + EA4 + ")*("
                    + "\"" + section.getULname() + "_Strain[με]\"+"
                    + "\"" + section.getURname() + "_Strain[με]\"+"
                    + "\"" + section.getLLname() + "_Strain[με]\"+"
                    + "\"" + section.getLRname() + "_Strain[με]\""
                    + ")"; // 引張が正。 (ひずみ値は圧縮が正であるが、EA4を負にしているため、引張軸力が正となる。）
            String bendingMoment = "(" + EZ4 + ")*("
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
            rs = st.executeQuery("select avg(" + axialForce + ")-(" + aveAxial + "),avg(" + bendingMoment + ")-(" + aveMoment + ") from \"" + timeHistoryTTable + "\""
                    + " where \"TIME[s]\"<2.0");
            rs.next();
            aveAxial = rs.getDouble(1);
            aveMoment = rs.getDouble(2);
            rs = st.executeQuery("select \"TIME[s]\",(" + axialForce + ")-(" + aveAxial + "),(" + bendingMoment + ")-(" + aveMoment + ") from \"" + timeHistoryTTable + "\"");

            double[][] ar = ResultSetUtils.createSeriesArray(rs);
            logger.log(Level.INFO, "aveAxial=" + StatUtils.mean(ar[1]) + "; aveMoment=" + StatUtils.mean(ar[2]) + ";");
//            ar[1] = FFTv2.zeroAverage(ar[1]);
//            ar[2] = FFTv2.zeroAverage(ar[2]);
            XYSeries m = new XYSeries("m");

            int w = 6;
            for (int i = w; i < ar[0].length - w - 1; i++) {
                m.add(ar[2][i], ar[1][i]);
            }

            XYSeriesCollection c = new XYSeriesCollection();
            c.addSeries(m);
            XYSeries line=new XYSeries("line",false);
            line.add(-2.4,-20);
            line.add(2.4,+20);
            line.add(2.4,Double.NaN);
            line.add(-2.8,-20);
            line.add(2.8,+20);
            c.addSeries(line);

            XYLineAndShapeRenderer r = new XYLineAndShapeRenderer(false, true);
            r.setSeriesLinesVisible(1, true);
            r.setSeriesShapesVisible(1, false);
            JFreeChart chart = new JunXYChartCreator2().setDataset(c).setRangeZeroBaselineVisible(true)
                    .setDomainAxisLabel("Bending moment [kNm]")
                    .setRangeAxisLabel("Axial force [kN]")
                    .setRenderer(r)
                    .setTitle(testname)
                    .create();
            
            
//            JunChartUtil.addRegressionCurves(chart, 0, 0, -10);
            JunChartUtil.show(chart);
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(T101SectionHisteresisT.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
