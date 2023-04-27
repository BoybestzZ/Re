/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartCreator;
import jun.chart.JunChartUtil;
import jun.chart.JunXYChartCreator2;
import jun.res23.ed.util.BeamInfo;
import jun.res23.ed.util.EdefenseInfo;
import static jun.res23.ed.util.EdefenseInfo.*;
import jun.res23.ed.util.EdefenseKasinInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author jun
 */
public class T310BeamShearLocalStiffness {

    private static final String T300Schema = T300Integ2StoryDisp.outputSchema;//"T300Integ2StoryDisp"
    public static final String dburl = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/res22ed06";
    private static final Path svgfile = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/T310BeamShearLocalStiffness/Beam3.svg");

    public static void main(String[] args) {

        BeamInfo beam = EdefenseInfo.Beam3;

        XYSeries s = new XYSeries("localstiffness");

        try {
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            EdefenseKasinInfo[] kumamotos = new EdefenseKasinInfo[]{
                D01Q01, D01Q02, D01Q03, D01Q09, D01Q11,
                D02Q01, D02Q02, D02Q05, D02Q06, D02Q07, D03Q01, D03Q02, D03Q03, D03Q09
            };
            EdefenseKasinInfo[] kobes = new EdefenseKasinInfo[]{
                D01Q04, D01Q08, D01Q10,
                D02Q03, D03Q04, D03Q08
            };

            EdefenseKasinInfo[] randoms = new EdefenseKasinInfo[]{
                D01Q01, D01Q09, D01Q11,
                D02Q05, D03Q01, D03Q09
            };

            DefaultCategoryDataset datasetRandoms = new DefaultCategoryDataset();
            DefaultCategoryDataset datasetKobe = new DefaultCategoryDataset();

            for (EdefenseKasinInfo test : alltests) {

                ResultSet rs = st.executeQuery("select \"TimePerTest[s]\",\"BendingMomentPerTest[kNm]\", \"AxialForcePerTest[kN]\""
                        + " from \"T231TimeHistoryNM\".\"" + EdefenseInfo.LA3S2.getName() + "\" "
                        + " where TESTNAME='" + test.getName() + "t' "
                        + "order by \"BendingMomentPerTest[kNm]\" desc limit 1");

                rs.next();
                double time = rs.getDouble(1);
                time = Math.round(time * 100) / 100.0;
                
                

                double momentS2 = rs.getDouble(2);
                double momentTotalS2 = momentS2 + rs.getDouble(3) * (0.11 + 0.35) * 0.5;
                String sql;
                rs = st.executeQuery(sql = "select \"TimePerTest[s]\",\"BendingMomentPerTest[kNm]\",\"AxialForcePerTest[kN]\""
                        + " from \"T231TimeHistoryNM\".\"" + EdefenseInfo.LA3S3.getName() + "\" "
                        + "where \"TimePerTest[s]\" =" + time + "  and TESTNAME='" + test.getTestName() + "t'");
                System.out.println(sql);
                rs.next();
                double momentS3 = rs.getDouble(2);
                double momentTotalS3 = momentS3 + rs.getDouble(3) * (0.11 + 0.35) * 0.5;

                double shearSteel = (momentS2 - momentS3) / (beam.getLocation(2) - beam.getLocation(1)); //kN
                double shearTotal = (momentTotalS2 - momentTotalS3) / (beam.getLocation(2) - beam.getLocation(1)); //kN

                rs = st.executeQuery(sql = "select \"RelDisp[cm]\" from \"" + T300Schema + "\".\"" + test.getTestName() + "t\" "
                        + " where \"TimePerTest[s]\"=" + time);
                System.out.println(sql);
                rs.next();
                double disp = rs.getDouble(1) * 10; // unit:mm
                if (isTestsIn(test, randoms)) {
                    datasetRandoms.addValue(shearSteel / disp, "steelRandom", test.getTestName() + test.getWaveName());
                    datasetRandoms.addValue(shearTotal / disp, "totalRandom", test.getTestName() + test.getWaveName());
                } else if (isTestsIn(test, kobes)) {

//                    datasetRandoms.addValue(shearSteel / disp, "steelKobe", test.getTestName() + test.getWaveName());
//                    datasetRandoms.addValue(shearTotal / disp, "totalKobe", test.getTestName() + test.getWaveName());
                }
//                s.add(disp, shear);
            }

            CategoryAxis xaxis = new CategoryAxis("Test");
            xaxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
            NumberAxis yaxis = new NumberAxis("");
            LineAndShapeRenderer renderer = new LineAndShapeRenderer(true, true);
            CategoryPlot plot = new CategoryPlot(datasetRandoms, xaxis, yaxis, renderer);
            plot.setDomainGridlinesVisible(true);

            JFreeChart chart = new JFreeChart(plot);

            try {
                //            JunChartUtil.show(chart);
                JunChartUtil.svg(svgfile, 500, 400, chart);
            } catch (IOException ex) {
                Logger.getLogger(T310BeamShearLocalStiffness.class.getName()).log(Level.SEVERE, null, ex);
            }

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(T310BeamShearLocalStiffness.class.getName()).log(Level.SEVERE, null, ex);
        }

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
