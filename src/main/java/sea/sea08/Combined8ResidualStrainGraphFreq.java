/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea08;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.RefineryUtilities;
import static sea.sea08.CombinedResidualStrainGraph.createResidualStrain;

public class Combined8ResidualStrainGraphFreq{ 

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

    public static void createResidualStrain(String table1, String table2, String table3, String table4, String table5, String table6, String table7, String table8, String tableset) {
        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\\\test/res22ed06test";
            String sql1 = "SELECT NUMBER, \"RESIDUALSTRAIN\" FROM \"" + table1 + "\"";
            String sql2 = "SELECT NUMBER, \"RESIDUALSTRAIN\" FROM \"" + table2 + "\"";
            String sql3 = "SELECT NUMBER, \"RESIDUALSTRAIN\" FROM \"" + table3 + "\"";
            String sql4 = "SELECT NUMBER, \"RESIDUALSTRAIN\" FROM \"" + table4 + "\"";
            String sql5 = "SELECT NUMBER, \"RESIDUALSTRAIN\" FROM \"" + table5 + "\"";
            String sql6 = "SELECT NUMBER, \"RESIDUALSTRAIN\" FROM \"" + table6 + "\"";
            String sql7 = "SELECT NUMBER, \"RESIDUALSTRAIN\" FROM \"" + table7 + "\"";
            String sql8 = "SELECT NUMBER, \"RESIDUALSTRAIN\" FROM \"" + table8 + "\"";

            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            ResultSet rs1 = con.createStatement().executeQuery(sql1);
            double[][] data1 = ResultSetUtils.createSeriesArray(rs1);

            ResultSet rs2 = con.createStatement().executeQuery(sql2);
            double[][] data2 = ResultSetUtils.createSeriesArray(rs2);
            
            ResultSet rs3 = con.createStatement().executeQuery(sql3);
            double[][] data3 = ResultSetUtils.createSeriesArray(rs3);
            
            ResultSet rs4 = con.createStatement().executeQuery(sql4);
            double[][] data4 = ResultSetUtils.createSeriesArray(rs4);
            
            ResultSet rs5 = con.createStatement().executeQuery(sql5);
            double[][] data5 = ResultSetUtils.createSeriesArray(rs5);

            ResultSet rs6 = con.createStatement().executeQuery(sql6);
            double[][] data6 = ResultSetUtils.createSeriesArray(rs6);
            
            ResultSet rs7 = con.createStatement().executeQuery(sql7);
            double[][] data7 = ResultSetUtils.createSeriesArray(rs7);
            
            ResultSet rs8 = con.createStatement().executeQuery(sql8);
            double[][] data8 = ResultSetUtils.createSeriesArray(rs8);

            DefaultXYDataset dataset = new DefaultXYDataset();
            dataset.addSeries(table1, data1);
            dataset.addSeries(table2, data2);
            dataset.addSeries(table3, data3);
            dataset.addSeries(table4, data4);
            dataset.addSeries(table5, data5);
            dataset.addSeries(table6, data6);
            dataset.addSeries(table7, data7);
            dataset.addSeries(table8, data8);

            JFreeChart chart = ChartFactory.createXYLineChart(
                "Combined Residual Strain",
                "Time (s)",
                "Residual Strain (με)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
            );
            
                        // Customize the chart
            XYPlot plot = chart.getXYPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.BLACK);
            plot.setDomainGridlinesVisible(true);
            plot.setDomainGridlinePaint(Color.BLACK);
           
            NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
            domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont((int) 12f));
            domainAxis.setVerticalTickLabels(true);
            plot.setOutlinePaint(Color.BLACK);
            plot.setOutlineStroke(new BasicStroke(2f)); // frame around the plot
            plot.setAxisOffset(RectangleInsets.ZERO_INSETS); // remove space between frame and axis.
            
            
         
            
            
            
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setRange(-30000, 30000); // Set the y-axis range
            rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());

            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesPaint(0, Color.RED);
            plot.setRenderer(renderer);

//            chart.setBackgroundPaint(Color.WHITE);

            // グラフを表示する。（これは伊山が作ったライブラリを使っている。デフォルトの方法はちょっと面倒なので。）
              JunChartUtil.show(chart);
            
            // Output the graph to SVG file
            Path svgfile = Path.of("C:\\Users\\75496\\Documents\\E-Defense\\residualstraintimehistory\\residualstraintimehistorydouble_" + tableset + ".svg");
            try {
                JunChartUtil.svg(svgfile, 900, 300, chart);
            } catch (IOException ex) {
                Logger.getLogger(A001GraphTimeHistory111.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // Display the chart in a frame
            ChartFrame frame = new ChartFrame("Residual Strain", chart);
            frame.setPreferredSize(new Dimension(1200, 800));
            frame.pack();
            frame.setVisible(true);

            // やらなくても大丈夫だけど、やっといた方がいい。データベースを閉じる。
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A001GraphTimeHistory111.class.getName()).log(Level.SEVERE, null, ex);
  
    }
    }
}