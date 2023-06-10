/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed08;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.file.Path;
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
import jun.util.SVGWriter;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class B101CompareToInteg2Disp {

    public static final String ed06dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res23/ed/ed06分析T/res22ed06";
    public static final String ed08dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res23/ed/ed08防災科研/res22ed08";
    public static final String schemaInteg2Disp = "T301Integ2StoryDisp";
    public static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed08防災科研/B101CompareToInteg2Disp");

    ;

    public static void main(String[] args) {
        EdefenseKasinInfo[] tests = EdefenseInfo.alltests;
        for (EdefenseKasinInfo te : tests) {
            B101CompareToInteg2Disp.main(te);
        }
    }

    public static void main(EdefenseKasinInfo test) {

        try {
            Connection con08 = DriverManager.getConnection(ed08dburl, "junapp", "");
            Statement st08 = con08.createStatement();
//            EdefenseKasinInfo test = EdefenseInfo.D01Q01;
            st08.executeUpdate("create table if not exists \"B101CompareToInteg2Disp\" (TESTNAME varchar,WAVENAME varchar, TIME real,\"Integ2Disp[mm]\" real, "
                    + "\"W_NS_isd2[mm]\" real, \"W_NS_isd3[mm]\" real)");
            st08.executeUpdate("delete from \"B101CompareToInteg2Disp\" where TESTNAME='"+test.getName()+"'");
            double diffTimeSec = test.getNiedTimeDiffSeconds();// 7.9; // Nied 時刻からこの値を引くと t時刻に一致する。
            ResultSet rs = st08.executeQuery("select avg(\"StoryDispE_X[mm]\") ,avg(\"StoryDispE_Y[mm]\") ,"
                    + "avg(\"StoryDispW_X[mm]\") ,avg(\"StoryDispW_Y[mm]\") from \"" + test.getTestName() + "_isd2\" where \"Time[s]\"<=2.0");
            rs.next();
            double avg2EX = rs.getDouble(1);
            double avg2EY = rs.getDouble(2);
            double avg2WX = rs.getDouble(3);
            double avg2WY = rs.getDouble(4);

            rs = st08.executeQuery("select \"Time[s]\"-(" + diffTimeSec + "),"
                    + "\"StoryDispE_X[mm]\"-(" + avg2EX + ") ,"
                    + "\"StoryDispE_Y[mm]\" -(" + avg2EY + ") ,"
                    + "\"StoryDispW_X[mm]\" -(" + avg2WX + ") ,"
                    + "\"StoryDispW_Y[mm]\" -(" + avg2WY + ") "
                    + " from \"" + test.getTestName() + "_isd2\"");
            double[][] ar2 = ResultSetUtils.createSeriesArray(rs);

            DefaultXYDataset dataset = new DefaultXYDataset();
            dataset.addSeries("W_NS_isd2", new double[][]{ar2[0], ar2[3]}); // timeT, time, storydispE_X,storydispE_Y, storydispW_X, storydispW_Y[mm]

            rs = st08.executeQuery("select avg(\"StoryDispE_X[mm]\") ,avg(\"StoryDispE_Y[mm]\") ,"
                    + "avg(\"StoryDispW_X[mm]\") ,avg(\"StoryDispW_Y[mm]\") from \"" + test.getTestName() + "_isd3\" where \"Time[s]\"<=2.0");
            rs.next();
            double avg3EX = rs.getDouble(1);
            double avg3EY = rs.getDouble(2);
            double avg3WX = rs.getDouble(3);
            double avg3WY = rs.getDouble(4);
            rs = st08.executeQuery("select \"Time[s]\"-(" + diffTimeSec + "),"
                    + "\"StoryDispE_X[mm]\"-(" + avg3EX + ") ,"
                    + "\"StoryDispE_Y[mm]\" -(" + avg3EY + ") ,"
                    + "\"StoryDispW_X[mm]\" -(" + avg3WX + ") ,"
                    + "\"StoryDispW_Y[mm]\" -(" + avg3WY + ") "
                    + " from \"" + test.getTestName() + "_isd3\"");
            double[][] ar3 = ResultSetUtils.createSeriesArray(rs);

            dataset.addSeries("W_NS_isd3", new double[][]{ar3[0], ar3[3]});

            Connection con06 = DriverManager.getConnection(ed06dburl, "junapp", "");

            Statement st06 = con06.createStatement();

            //ここのreldisp は2層分なので、0.5倍する。だけど、cmなのでそれをmmに変換するために10ばいしないといけない。結局5倍することになる。
            rs = st06.executeQuery("select \"TimePerTest[s]\",\"RelDisp[cm]\"*(-5) from \"" + schemaInteg2Disp + "\".\"" + test.getTestName() + "t\"");
            double[][] arinteg = ResultSetUtils.createSeriesArray(rs);
            dataset.addSeries("Integ2Disp(average of 2 stories)", arinteg);

            // 最大値付近を探す。
            rs = st06.executeQuery("select \"TimePerTest[s]\",abs(\"RelDisp[cm]\")*(5) from \"" + schemaInteg2Disp + "\".\"" + test.getTestName() + "t\" order by 2 desc limit 1");
            rs.next();
            double maxtime = rs.getDouble(1);
            double maxinteg = rs.getDouble(2);

            // ar2 のこの時刻付近の値を探す
            double value2 = 0;
            double time2diff = Double.POSITIVE_INFINITY;
            for (int i = 0; i < ar2[0].length; i++) {
                double time = ar2[0][i];
                double value = Math.abs(ar2[3][i]);
                if (Math.abs(maxtime - time) < time2diff) {
                    time2diff = Math.abs(maxtime - time);
                    value2 = value;
                } else {
                    break;
                }
            }

            double value3 = 0;
            double time3diff = Double.POSITIVE_INFINITY;
            for (int i = 0; i < ar3[0].length; i++) {
                double time = ar3[0][i];
                double value = Math.abs(ar3[3][i]);
                if (Math.abs(maxtime - time) < time3diff) {
                    time3diff = Math.abs(maxtime - time);
                    value3 = value;
                } else {
                    break;
                }
            }

            st08.executeUpdate("insert into \"B101CompareToInteg2Disp\" (TESTNAME,WAVENAME, TIME,\"Integ2Disp[mm]\", \"W_NS_isd2[mm]\", \"W_NS_isd3[mm]\")"
                    + " values "
                    + "('" + test.getName() + "','"+test.getWaveName()+"'," + maxtime + "," + maxinteg + "," + value2 + "," + value3 + ")");

            JFreeChart chart = new JunXYChartCreator2()
                    .setRangeAxisLabel("ISD[mm]")
                    .setDataset(dataset).create();

            if (svgdir != null) {
                Path svgfile = svgdir.resolve(test.getTestName() + test.getWaveName() + "NS.svg");
                SVGGraphics2D g2d = SVGWriter.prepareGraphics2D(500, 500);
                chart.draw(g2d, new Rectangle2D.Double(0, 0, 500, 250));

                chart.getXYPlot().getDomainAxis().setRange(maxtime - 4, maxtime + 4);
                chart.draw(g2d, new Rectangle2D.Double(0, 250, 500, 250));

                try {
                    SVGWriter.outputSVG(g2d, svgfile);
                } catch (IOException ex) {
                    Logger.getLogger(B101CompareToInteg2Disp.class.getName()).log(Level.SEVERE, null, ex);
                }
                g2d.dispose();

            }
            con06.close();
            con08.close();

        } catch (SQLException ex) {
            Logger.getLogger(B101CompareToInteg2Disp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
