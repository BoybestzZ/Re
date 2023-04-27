/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed08;

import java.awt.Color;
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
import jun.design.Column;
import static jun.res23.ed.ed08.B210ElementShearLocalStiffness.ed08dburl;
import static jun.res23.ed.ed08.B210ElementShearLocalStiffness.outputTable;
import jun.res23.ed.util.BeamInfo;
import jun.res23.ed.util.ColumnInfo;
import jun.res23.ed.util.EdefenseInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * B210での計算結果を使う。
 *
 * @author jun
 */
public class B212GraphElementShearLocalStiffness {

    static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed08防災科研/B212ElementShaerLocalStiffness");

    public static void main(String[] args) {

//        BeamInfo element = EdefenseInfo.Beam3;
        ColumnInfo element = EdefenseInfo.Column2FA3;
        try {
            Connection con08 = DriverManager.getConnection(ed08dburl, "junapp", "");
            Statement st08 = con08.createStatement();

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            ResultSet rs = st08.executeQuery("select \"ElementName\" varchar,  \"TESTNAME\" varchar, \"PositiveDirection\" boolean, "
                    + "\"TimePerTest[s]\" real , \"MomentS2[kNm]\" real,\"MomentS3[kNm]\" real, \"ShearForce[kN]\" real,\"Story2Drift[mm]\"real,\"LocalStiffness[kN/mm]\" real "
                    + " from \"" + outputTable + "\""
                    + " where \"ElementName\"='" + element.getName() + "'");

            while (rs.next()) {
                String testname = rs.getString("TESTNAME");
                String wavename = EdefenseInfo.lookForTestName(testname).getWaveName();
                if (wavename.endsWith("Y)")) {
                    continue;
                }
                String typename = wavename.substring(0, 4);
                double localStiffness = rs.getDouble("LocalStiffness[kN/mm]");
                String direction = rs.getBoolean("PositiveDirection") ? "P" : "N";
                dataset.addValue(localStiffness, typename + direction, testname + wavename);
            }
            con08.close();
            LineAndShapeRenderer renderer = new LineAndShapeRenderer(false, true);
            renderer.setSeriesPaint(0, Color.RED);
            renderer.setSeriesPaint(1, Color.BLUE);
            renderer.setSeriesPaint(2, new Color(0, 0.8f, 0));
            renderer.setSeriesPaint(3, Color.MAGENTA);
            renderer.setSeriesPaint(4, Color.RED);
            renderer.setSeriesPaint(5, Color.BLUE);
            renderer.setSeriesPaint(6, new Color(0, 0.8f, 0));
            renderer.setSeriesPaint(7, Color.MAGENTA);
            renderer.setSeriesShapesFilled(0, Boolean.TRUE);
            renderer.setSeriesShapesFilled(1, Boolean.TRUE);
            renderer.setSeriesShapesFilled(2, Boolean.TRUE);
            renderer.setSeriesShapesFilled(3, Boolean.TRUE);
            renderer.setSeriesShapesFilled(4, Boolean.FALSE);
            renderer.setSeriesShapesFilled(5, Boolean.FALSE);
            renderer.setSeriesShapesFilled(6, Boolean.FALSE);
            renderer.setSeriesShapesFilled(7, Boolean.FALSE);

            CategoryAxis xaxis = new CategoryAxis("Test");
            xaxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
            NumberAxis yaxis = new NumberAxis("");

            CategoryPlot plot = new CategoryPlot(dataset, xaxis, yaxis, renderer);
            plot.setDomainGridlinesVisible(true);
            plot.getRangeAxis().setFixedDimension(50);
            JFreeChart chart = new JFreeChart(plot);

            //            JunChartUtil.show(element.getName(), chart);
            if (svgdir != null)
            try {

                JunChartUtil.svg(svgdir.resolve(element.getName() + ".svg"), 600, 400, chart);
            } catch (IOException ex) {
                Logger.getLogger(B212GraphElementShearLocalStiffness.class.getName()).log(Level.SEVERE, null, ex);
            } else {
                JunChartUtil.show(element.getName(), chart);
            }
        } catch (SQLException ex) {
            Logger.getLogger(B212GraphElementShearLocalStiffness.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
