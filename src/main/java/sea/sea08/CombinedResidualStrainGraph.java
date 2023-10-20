/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea08;

import com.itextpdf.text.Rectangle;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.data.ResultSetUtils;
import org.apache.batik.svggen.SVGGraphics2D;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.DefaultXYDataset;

public class CombinedResidualStrainGraph {

    public static void main(String[] args) {
        try {
            createResidualStrain("RSb02/01", "RSb02/02", "RSb02/03", "RSb02/04","RSb02/05", "RSb02/06", "RSb02/07", "RSb02/08", "fourierb2all");
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createResidualStrain(String table1, String table2, String table3, String table4,
            String table5, String table6, String table7, String table8, String tableset) throws Exception {
        DefaultXYDataset dataset = new DefaultXYDataset();

        try (Connection con = createDatabaseConnection("jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\\\test/res22ed06test", "junapp", "")) {
            addSeriesToDataset(dataset, con, table1);
            addSeriesToDataset(dataset, con, table2);
            addSeriesToDataset(dataset, con, table3);
            addSeriesToDataset(dataset, con, table4);
            addSeriesToDataset(dataset, con, table5);
            addSeriesToDataset(dataset, con, table6);
            addSeriesToDataset(dataset, con, table7);
            addSeriesToDataset(dataset, con, table8);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            throw new Exception("Error in database connection or query.");
        }

        JFreeChart chart = createChart("Combined Residual Strain", "Time (s)", "Residual Strain (με)", dataset);
        createAndShowChart(chart);

        // Output the graph to an SVG file
        saveChartToSVG(chart, "C:\\Users\\75496\\Documents\\E-Defense\\residualstraintimehistory\\residualstraintimehistorydouble_" + tableset + ".svg");
    }

    private static Connection createDatabaseConnection(String dburl, String username, String password) throws SQLException {
        return DriverManager.getConnection(dburl, username, password);
    }

    private static void addSeriesToDataset(DefaultXYDataset dataset, Connection con, String tableName) throws SQLException, IOException {
        String sql = "SELECT TESTNAME, \"RESIDUALSTRAIN\" FROM \"" + tableName + "\"";
        ResultSet rs = con.createStatement().executeQuery(sql);
        double[][] data = ResultSetUtils.createSeriesArray(rs);

        if (data != null) {
            dataset.addSeries(tableName, data);
        } else {
            throw new IOException("Data for " + tableName + " is null. Check your SQL query or database.");
        }
    }

    private static JFreeChart createChart(String title, String xAxisLabel, String yAxisLabel, DefaultXYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
            title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false
        );

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont((int) 12f));
        domainAxis.setVerticalTickLabels(true);
        plot.setOutlinePaint(Color.BLACK);
        plot.setOutlineStroke(new BasicStroke(2f));
        plot.setAxisOffset(RectangleInsets.ZERO_INSETS);

        return chart;
    }

    private static void createAndShowChart(JFreeChart chart) {
        ChartFrame frame = new ChartFrame("Inflection Point", chart);
        frame.setPreferredSize(new Dimension(1200, 800));
        frame.pack();
        frame.setVisible(true);
    }

    private static void saveChartToSVG(JFreeChart chart, String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}