/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed08;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunXYChartCreator;
import jun.chart.JunXYChartCreator2;
import jun.data.ResultSetUtils;
import static jun.res23.ed.ed08.B100ReadCSV.dburl;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class B101CompareToInteg2Disp {

    public static final String ed06dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/res22ed06";
    public static final String ed08dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/ed/ed08防災科研/res22ed08";
    public static final String schemaInteg2Disp = "T301Integ2StoryDisp";

    public static void main(String[] args) {
        B101CompareToInteg2Disp.main(EdefenseInfo.D01Q04);
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
            double avg2EX =rs.getDouble(1);
            double avg2EY =rs.getDouble(2);
            double avg2WX =rs.getDouble(3);
            double avg2WY = rs.getDouble(4);

            rs = st.executeQuery("select \"Time[s]\"-(" + diffTimeSec + "),"
                    + "\"StoryDispE_X[mm]\"-(" + avg2EX + ") ,"
                    + "\"StoryDispE_Y[mm]\" -(" + avg2EY + ") ,"
                    + "\"StoryDispW_X[mm]\" -(" + avg2WX + ") ,"
                    + "\"StoryDispW_Y[mm]\" -(" + avg2WY + ") "
                    + " from \"" + test.getTestName() + "_isd2\"");
            double[][] ar = ResultSetUtils.createSeriesArray(rs);

            DefaultXYDataset dataset = new DefaultXYDataset();
            dataset.addSeries("X2", new double[][]{ar[0], ar[3]}); // timeT, time, storydispE_X,storydispE_Y, storydispW_X, storydispW_Y[mm]

            rs = st.executeQuery("select avg(\"StoryDispE_X[mm]\") ,avg(\"StoryDispE_Y[mm]\") ,"
                    + "avg(\"StoryDispW_X[mm]\") ,avg(\"StoryDispW_Y[mm]\") from \"" + test.getTestName() + "_isd3\" where \"Time[s]\"<=2.0");
            rs.next();
            double avg3EX = rs.getDouble(1);
            double avg3EY = rs.getDouble(2);
            double avg3WX =rs.getDouble(3);
            double avg3WY =rs.getDouble(4);
            rs = st.executeQuery("select \"Time[s]\"-(" + diffTimeSec + "),"
                    + "\"StoryDispE_X[mm]\"-(" + avg3EX + ") ,"
                    + "\"StoryDispE_Y[mm]\" -(" + avg3EY + ") ,"
                    + "\"StoryDispW_X[mm]\" -(" + avg3WX + ") ,"
                    + "\"StoryDispW_Y[mm]\" -(" + avg3WY + ") "
                    + " from \"" + test.getTestName() + "_isd3\"");
            ar = ResultSetUtils.createSeriesArray(rs);

            dataset.addSeries("X3", new double[][]{ar[0], ar[3]});

            con.close();

            con = DriverManager.getConnection(ed06dburl, "junapp", "");

            st = con.createStatement();

            rs = st.executeQuery("select \"TimePerTest[s]\",\"RelDisp[cm]\"*(-5) from \"" + schemaInteg2Disp + "\".\"" + test.getTestName() + "t\"");
            ar = ResultSetUtils.createSeriesArray(rs);
            dataset.addSeries("Integ2Disp[mm]", ar);

            new JunXYChartCreator2()
                    .setRangeAxisLabel("ISD[mm]")
                    .setDataset(dataset).show(test.getTestName() + ":" + test.getWaveName());
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(B101CompareToInteg2Disp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
