/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed08;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.chart.JunXYChartCreator;
import jun.chart.JunXYChartCreator2;
import jun.data.ResultSetUtils;
import static jun.res23.ed.ed08.B100ReadCSV.dburl;
import static jun.res23.ed.ed08.B101CompareToInteg2Disp.schemaInteg2Disp;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class B102CompareToMoment {

    public static final String ed06dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res23/ed/ed06分析T/res22ed06";
    public static final String ed08dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res23/ed/ed08防災科研/res22ed08";
    public static final String schemaInteg2Disp = "T301Integ2StoryDisp";
    public static final Path svgdir = null;// Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed08防災科研/B102CompareToMoment");

    public static void main(String[] args) {
//        EdefenseKasinInfo[] tests = EdefenseInfo.alltests;
        EdefenseKasinInfo[] tests = {EdefenseInfo.D02Q06};

        for (EdefenseKasinInfo te : tests) {
            B102CompareToMoment.main(te);
        }
    }

    public static void main(EdefenseKasinInfo test) {

        try {
            Connection con = DriverManager.getConnection(ed08dburl, "junapp", "");
            Statement st = con.createStatement();
//            EdefenseKasinInfo test = EdefenseInfo.D01Q01;
            double diffTimeSec = test.getNiedTimeDiffSeconds();// 7.9; // Nied 時刻からこの値を引くと t時刻に一致する。
            ResultSet rs = st.executeQuery("select avg(\"StoryDispE_X[mm]\") ,avg(\"StoryDispE_Y[mm]\") ,"
                    + "avg(\"StoryDispW_X[mm]\") ,avg(\"StoryDispW_Y[mm]\") from \"" + test.getTestName() + "_isd2\" where \"Time[s]\"<=2.0");
            rs.next();
            double avg2EX =  rs.getDouble(1);
            double avg2EY =  rs.getDouble(2);
            double avg2WX =  rs.getDouble(3);
            double avg2WY =  rs.getDouble(4);
            

            rs = st.executeQuery("select \"Time[s]\"-(" + diffTimeSec + "),"
                    + "\"StoryDispE_X[mm]\"-(" + avg2EX + ") ,"
                    + "\"StoryDispE_Y[mm]\" -(" + avg2EY + ") ,"
                    + "\"StoryDispW_X[mm]\" -(" + avg2WX + ") ,"
                    + "\"StoryDispW_Y[mm]\" -(" + avg2WY + ") "
                    + " from \"" + test.getTestName() + "_isd2\"");
            double[][] ar2 = ResultSetUtils.createSeriesArray(rs);

            DefaultXYDataset dataset = new DefaultXYDataset();
            //  dataset.addSeries("X2", new double[][]{ar[0], ar[3]}); // timeT, time, storydispE_X,storydispE_Y, storydispW_X, storydispW_Y[mm]

            rs = st.executeQuery("select avg(\"StoryDispE_X[mm]\") ,avg(\"StoryDispE_Y[mm]\") ,"
                    + "avg(\"StoryDispW_X[mm]\") ,avg(\"StoryDispW_Y[mm]\") from \"" + test.getTestName() + "_isd3\" where \"Time[s]\"<=2.0");
            rs.next();
            double avg3EX = rs.getDouble(1);
            double avg3EY = rs.getDouble(2);
            double avg3WX = rs.getDouble(3);
            double avg3WY = rs.getDouble(4);
            rs = st.executeQuery("select \"Time[s]\"-(" + diffTimeSec + "),"
                    + "\"StoryDispE_X[mm]\"-(" + avg3EX + ") ,"
                    + "\"StoryDispE_Y[mm]\" -(" + avg3EY + ") ,"
                    + "\"StoryDispW_X[mm]\" -(" + avg3WX + ") ,"
                    + "\"StoryDispW_Y[mm]\" -(" + avg3WY + ") "
                    + " from \"" + test.getTestName() + "_isd3\"");
            double[][] ar3 = ResultSetUtils.createSeriesArray(rs);

            //   dataset.addSeries("X3", new double[][]{ar3[0], ar3[3]});
            for (int i = 0; i < ar3[0].length; i++) {
                ar3[3][i] = (ar2[3][i] + ar3[3][i]) * 0.5; // 2WXと 3WXの平均をとっている。(NS方向） 
            }

            dataset.addSeries("LaserAvg", new double[][]{ar3[0], ar3[3]});

            con.close();

            con = DriverManager.getConnection(ed06dburl, "junapp", "");

            st = con.createStatement();

            rs = st.executeQuery("select \"TimePerTest[s]\",-\"BendingMomentPerTest[kNm]\"/5.0 from \"T231TimeHistoryNM\".\"LA3S1\" where TESTNAME='" + test.getTestName() + "t'");
            double[][] ar = ResultSetUtils.createSeriesArray(rs);
            dataset.addSeries("MomentLA3S1[kNm](negative)", ar);

            rs = st.executeQuery("select \"TimePerTest[s]\",\"RelDisp[cm]\"*(-5) from \"" + schemaInteg2Disp + "\".\"" + test.getTestName() + "t\"");
            ar = ResultSetUtils.createSeriesArray(rs);
            dataset.addSeries("Integ2Disp[mm]", ar);

            JFreeChart chart = new JunXYChartCreator2()
                    .setRangeAxisLabel("ISD[mm]")
                    .setDataset(dataset)
                    .setRangeZeroBaselineVisible(true).create();
            if (svgdir != null) {
                try {
                    JunChartUtil.svg(svgdir.resolve(test.getTestName() + "_" + test.getWaveName() + ".svg"), 500, 250, chart);
                } catch (IOException ex) {
                    Logger.getLogger(B102CompareToMoment.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JunChartUtil.show(test.getTestName() + ":" + test.getWaveName(), chart);
            }
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(B102CompareToMoment.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
