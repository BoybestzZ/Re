/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea09;

import sea.sea08.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
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
import jun.data.ResultSetUtils;
import static jun.raspi.reader.PayloadFileReader.formatter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.RefineryUtilities;
import static sea.sea08.CombinedResidualStrainGraph.createResidualStrain;

public class A902ResidualStrainBarGraph{ 

        public static void main(String[] args) throws IOException, SQLException{
        
        createResidualStrain("RSb02/01", "RSb02/02", "RSb02/03", "RSb02/04","RSb02/05", "RSb02/06", "RSb02/07", "RSb02/08", "fourierb2all");
        createResidualStrain("RSb03/01", "RSb03/02", "RSb03/03", "RSb03/04", "RSb03/05", "RSb03/06", "RSb03/07", "RSb03/08", "fourierb3all");
        createResidualStrain("RSd02/01", "RSd02/02", "RSd02/03", "RSd02/04","RSd02/05", "RSd02/06", "RSd02/07", "RSd02/08", "fourierd2all");
        createResidualStrain("RSd03/01", "RSd03/02", "RSd03/03", "RSd03/04","RSd03/05", "RSd03/06", "RSd03/07", "RSd03/08", "fourierd3all");
        createResidualStrain("RSf01/01", "RSf01/02", "RSf01/03", "RSf01/04", "RSf01/05", "RSf01/06", "RSf01/07", "RSf01/08", "fourierf1all");
        createResidualStrain("RSf02/01", "RSf02/02", "RSf02/03", "RSf02/04", "RSf02/05", "RSf02/06", "RSf02/07", "RSf02/08", "fourierf2all");
        createResidualStrain("RSh02/01", "RSh02/02", "RSh02/03", "RSh02/04", "RSh02/05", "RSh02/06", "RSh02/07", "RSh02/08", "fourierh2all");
        createResidualStrain("RSh03/01", "RSh03/02", "RSh03/03", "RSh03/04", "RSh03/05", "RSh03/06", "RSh03/07", "RSh03/08", "fourierh3all");
        
        
    }
        


    public static void createResidualStrain(String table1, String table2, String table3, String table4, String table5,
            String table6, String table7, String table8, String tableset) {
        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\\\test/res22ed06test3";

//            String sqlQuery = "SELECT 1 AS NUMBER, RESIDUALSTRAIN FROM \"" + table1 + "\" WHERE TESTNAME = 'D02Q03' "
//                    + "UNION SELECT 2 AS NUMBER, RESIDUALSTRAIN FROM \"" + table2 + "\" WHERE TESTNAME = 'D02Q03' "
//                    + "UNION SELECT 3 AS NUMBER, RESIDUALSTRAIN FROM \"" + table3 + "\" WHERE TESTNAME = 'D02Q03' "
//                    + "UNION SELECT 4 AS NUMBER, RESIDUALSTRAIN FROM \"" + table4 + "\" WHERE TESTNAME = 'D02Q03' "
//                    + "UNION SELECT 1.5 AS NUMBER, RESIDUALSTRAIN FROM \"" + table5 + "\" WHERE TESTNAME = 'D02Q03' "
//                    + "UNION SELECT 2.5 AS NUMBER, RESIDUALSTRAIN FROM \"" + table6 + "\" WHERE TESTNAME = 'D02Q03' "
//                    + "UNION SELECT 3.5 AS NUMBER, RESIDUALSTRAIN FROM \"" + table7 + "\" WHERE TESTNAME = 'D02Q03' "
//                    + "UNION SELECT 4.5 AS NUMBER, RESIDUALSTRAIN FROM \"" + table8 + "\" WHERE TESTNAME = 'D02Q03'";
            
//            String sqlQuery = "SELECT 1 AS NUMBER, RESIDUALSTRAIN FROM \"" + table1 + "\" WHERE TESTNAME = 'D01Q10' "
//                    + "UNION SELECT 2 AS NUMBER, RESIDUALSTRAIN FROM \"" + table2 + "\" WHERE TESTNAME = 'D01Q10' "
//                    + "UNION SELECT 3 AS NUMBER, RESIDUALSTRAIN FROM \"" + table3 + "\" WHERE TESTNAME = 'D01Q10' "
//                    + "UNION SELECT 4 AS NUMBER, RESIDUALSTRAIN FROM \"" + table4 + "\" WHERE TESTNAME = 'D01Q10' "
//                    + "UNION SELECT 1.5 AS NUMBER, RESIDUALSTRAIN FROM \"" + table5 + "\" WHERE TESTNAME = 'D01Q10' "
//                    + "UNION SELECT 2.5 AS NUMBER, RESIDUALSTRAIN FROM \"" + table6 + "\" WHERE TESTNAME = 'D01Q10' "
//                    + "UNION SELECT 3.5 AS NUMBER, RESIDUALSTRAIN FROM \"" + table7 + "\" WHERE TESTNAME = 'D01Q10' "
//                    + "UNION SELECT 4.5 AS NUMBER, RESIDUALSTRAIN FROM \"" + table8 + "\" WHERE TESTNAME = 'D01Q10'";
            
            String sqlQuery = "SELECT 1 AS NUMBER, RESIDUALSTRAIN FROM \"" + table1 + "\" WHERE TESTNAME = 'D01Q08' "
                    + "UNION SELECT 2 AS NUMBER, RESIDUALSTRAIN FROM \"" + table2 + "\" WHERE TESTNAME = 'D01Q08' "
                    + "UNION SELECT 3 AS NUMBER, RESIDUALSTRAIN FROM \"" + table3 + "\" WHERE TESTNAME = 'D01Q08' "
                    + "UNION SELECT 4 AS NUMBER, RESIDUALSTRAIN FROM \"" + table4 + "\" WHERE TESTNAME = 'D01Q08' "
                    + "UNION SELECT 1.5 AS NUMBER, RESIDUALSTRAIN FROM \"" + table5 + "\" WHERE TESTNAME = 'D01Q08' "
                    + "UNION SELECT 2.5 AS NUMBER, RESIDUALSTRAIN FROM \"" + table6 + "\" WHERE TESTNAME = 'D01Q08' "
                    + "UNION SELECT 3.5 AS NUMBER, RESIDUALSTRAIN FROM \"" + table7 + "\" WHERE TESTNAME = 'D01Q08' "
                    + "UNION SELECT 4.5 AS NUMBER, RESIDUALSTRAIN FROM \"" + table8 + "\" WHERE TESTNAME = 'D01Q08'";
            
            //please don't forget to change the title of output bar graph...

            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            ResultSet rs = con.createStatement().executeQuery(sqlQuery);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            while (rs.next()) {
                float number = rs.getFloat("NUMBER");
                double residualStrain = rs.getDouble("RESIDUALSTRAIN");
                dataset.addValue(residualStrain, "Residual Strain", String.valueOf(number));
            }

            JFreeChart chart = ChartFactory.createBarChart(
                    "Combined Residual Strain",
                    "Number",
                    "Residual Strain (με)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.BLACK);
            plot.setDomainGridlinesVisible(true);
            plot.setDomainGridlinePaint(Color.BLACK);

            CategoryAxis domainAxis = plot.getDomainAxis(); // Use CategoryAxis instead of NumberAxis
            domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont(12f));
            domainAxis.setTickLabelsVisible(false);

            plot.setOutlinePaint(Color.BLACK);
            plot.setOutlineStroke(new BasicStroke(2f));
            plot.setDomainGridlinesVisible(true);
            plot.setRangeGridlinesVisible(true);

            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setRange(-10000, 10000);
            rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());

            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            
            // Set width of the bars (adjust the value as needed)
            renderer.setMaximumBarWidth(0.2); // You can adjust this value as needed

            for (int i = 0; i < dataset.getRowCount(); i++) {
                Comparable<?> category = dataset.getRowKey(i);

                if (String.valueOf(category).equals("1") || String.valueOf(category).equals("2") ||
                    String.valueOf(category).equals("3") || String.valueOf(category).equals("4")) {
                    renderer.setSeriesPaint(i, Color.RED);
                } else {
                    renderer.setSeriesPaint(i, Color.RED);
                }
            }
            
            
            
            
            
            
            

            JunChartUtil.show(chart);

            // Output the graph to SVG file
            Path svgfile = Path.of("C:\\Users\\75496\\Documents\\E-Defense\\residualstrainbarchart\\residualstrainfourier(1stkobe50)_" + tableset + ".svg");
            try {
                JunChartUtil.svg(svgfile, 600, 300, chart);
            } catch (IOException ex) {
                Logger.getLogger(A001GraphTimeHistory111.class.getName()).log(Level.SEVERE, null, ex);
            }

            ChartFrame frame = new ChartFrame("Residual Strain", chart);
            frame.setPreferredSize(new Dimension(1200, 800));
            frame.pack();
            frame.setVisible(true);

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A001GraphTimeHistory111.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}