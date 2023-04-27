/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartCreator;
import jun.chart.JunChartUtil;
import jun.res23.ed.util.BeamInfo;
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;

/**
 * T232の結果を使う。
 *
 * @author jun
 */
public class T233GraphNeutralAxis2 {

    private static final Logger logger = Logger.getLogger(T233GraphNeutralAxis2.class.getName());
    private static final String inputTable = "T232NeutralAxis";

    public static void main(String[] args) {
        try {
            final BeamSectionInfo[] sections = {EdefenseInfo.LA3S1, EdefenseInfo.LA3S2, EdefenseInfo.LA3S3, EdefenseInfo.LA3S4, EdefenseInfo.LA3S5};
            final String dburl = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/res22ed06";

            String section = "LA3S1";

            final Path svgfile = null;// Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/T233NeutralAxis2/beam3" + (directionPositive ? "Positive" : "Negative") + ".svg");

            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            boolean directionPositive = true;
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            add(dataset, con, section, true);
            add(dataset, con, section, false);
            
  

            LineAndShapeRenderer re = new LineAndShapeRenderer(false, true);
            re.setSeriesPaint(0, Color.RED);
            re.setSeriesPaint(1, Color.BLUE);
            re.setSeriesPaint(2, new Color(0, 0.8f, 0));
            re.setSeriesPaint(3, Color.MAGENTA);
            re.setSeriesPaint(4, Color.RED);
            re.setSeriesPaint(5, Color.BLUE);
            re.setSeriesPaint(6, new Color(0, 0.8f, 0));
            re.setSeriesPaint(7, Color.MAGENTA);

            re.setSeriesShapesFilled(0, true);
            re.setSeriesShapesFilled(1, true);
            re.setSeriesShapesFilled(2, true);
            re.setSeriesShapesFilled(3, true);
            re.setSeriesShapesFilled(4, false);
            re.setSeriesShapesFilled(5, false);
            re.setSeriesShapesFilled(6, false);
            re.setSeriesShapesFilled(7, false);


            JFreeChart chart = new JunChartCreator().setRenderer(re).setValueAxisLabel("NeutralAxisLocation (from slab top) [mm]").setDataset(dataset).create();
            CategoryPlot plot = chart.getCategoryPlot();
            plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
            plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(2.0f);
            plot.getRangeAxis().setInverted(true);
            plot.getRangeAxis().setRange(0, 350);
            plot.setDomainGridlinesVisible(true);

            if (svgfile != null) {
                try {
                    JunChartUtil.svg(svgfile, 400, 400, chart);
                } catch (IOException ex) {
                    Logger.getLogger(T233GraphNeutralAxis2.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                JunChartUtil.show(section, chart);
            }
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(T233GraphNeutralAxis2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void add(DefaultCategoryDataset dataset, Connection con, String section, boolean directionPositive) throws SQLException {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("select TESTNAME, \"NeutralAxis[mm]\" from \"" + inputTable + "\" where SECTION='" + section + "' and \"DirectionPositive\"=" + directionPositive + " order by TESTNAME");
        while (rs.next()) {
            String testname = rs.getString(1);
            double xn = rs.getDouble(2);

            String wavename = EdefenseInfo.lookForTestName(testname).getWaveName();

            dataset.addValue(xn, section + (directionPositive ? "P" : "N") + wavename.substring(0, 4), testname + "(" + wavename + ")");
        }
        return;

    }

}
