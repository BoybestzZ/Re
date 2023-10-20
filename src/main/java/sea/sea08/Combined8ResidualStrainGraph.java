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

public class Combined8ResidualStrainGraph{ 

        public static void main(String[] args) throws IOException, SQLException{
        
        createResidualStrain("b02/01", "b02/02", "b02/03", "b02/04", "b02/05", "b02/06", "b02/07", "b02/08", "b2all");
//        createResidualStrain("b03/01", "b03/02", "b03/03", "b03/04", "b03/05", "b03/06", "b03/07", "b03/08", "d2all");
//        createResidualStrain("d02/01", "d02/02", "d02/03", "d02/04","d02/05", "d02/06", "d02/07", "d02/08", "d2all");
//        createResidualStrain("d03/01", "d03/02", "d03/03", "d03/04","d03/05", "d03/06", "d03/07", "d03/08", "d3all");
//        createResidualStrain("f01/01", "f01/02", "f01/03", "f01/04", "f01/05", "f01/06", "f01/07", "f01/08", "f1all");
//        createResidualStrain("f02/01", "f02/02", "f02/03", "f02/04", "f02/05", "f02/06", "f02/07", "f02/08", "f2all");
//        createResidualStrain("h02/01", "h02/02", "h02/03", "h02/04", "h02/05", "h02/06", "h02/07", "h02/08", "h2all");
//        createResidualStrain("h03/01", "h03/02", "h03/03", "h03/04", "h03/05", "h03/06", "h03/07", "h03/08", "h3all");

        
    }

    public static void createResidualStrain(String table1, String table2, String table3, String table4, String table5, String table6, String table7, String table8, String tableset) {
        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\\\test/res22ed06test";
            String sql1 = "SELECT \"TotalTime[s]\", \"Strain[με]\" FROM \"T220TimeHistoryStrain\".\"" + table1 + "\"";
            String sql2 = "SELECT \"TotalTime[s]\", \"Strain[με]\" FROM \"T220TimeHistoryStrain\".\"" + table2 + "\"";
            String sql3 = "SELECT \"TotalTime[s]\", \"Strain[με]\" FROM \"T220TimeHistoryStrain\".\"" + table3 + "\"";
            String sql4 = "SELECT \"TotalTime[s]\", \"Strain[με]\" FROM \"T220TimeHistoryStrain\".\"" + table4 + "\"";
            String sql5 = "SELECT \"TotalTime[s]\", \"Strain[με]\" FROM \"T220TimeHistoryStrain\".\"" + table5 + "\"";
            String sql6 = "SELECT \"TotalTime[s]\", \"Strain[με]\" FROM \"T220TimeHistoryStrain\".\"" + table6 + "\"";
            String sql7 = "SELECT \"TotalTime[s]\", \"Strain[με]\" FROM \"T220TimeHistoryStrain\".\"" + table7 + "\"";
            String sql8 = "SELECT \"TotalTime[s]\", \"Strain[με]\" FROM \"T220TimeHistoryStrain\".\"" + table8 + "\"";

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
//            ChartFrame frame = new ChartFrame("Inflection Point", chart);
//            frame.setPreferredSize(new Dimension(1200, 800));
//            frame.pack();
//            frame.setVisible(true);

            // やらなくても大丈夫だけど、やっといた方がいい。データベースを閉じる。
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A001GraphTimeHistory111.class.getName()).log(Level.SEVERE, null, ex);
  
    }
    }
}