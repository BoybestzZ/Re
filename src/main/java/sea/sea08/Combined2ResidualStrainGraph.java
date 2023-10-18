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

public class Combined2ResidualStrainGraph{ 

        public static void main(String[] args) throws IOException, SQLException{
        createResidualStrain("d02/01", "d02/05", "d115");
    }

    public static void createResidualStrain(String table1, String table2, String tableset) {
        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\\\test/res22ed06test";
            String sql1 = "SELECT \"TotalTime[s]\", \"Strain[με]\" FROM \"T220TimeHistoryStrain\".\"" + table1 + "\"";
            String sql2 = "SELECT \"TotalTime[s]\", \"Strain[με]\" FROM \"T220TimeHistoryStrain\".\"" + table2 + "\"";

            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            ResultSet rs1 = con.createStatement().executeQuery(sql1);
            double[][] data1 = ResultSetUtils.createSeriesArray(rs1);

            ResultSet rs2 = con.createStatement().executeQuery(sql2);
            double[][] data2 = ResultSetUtils.createSeriesArray(rs2);

            DefaultXYDataset dataset = new DefaultXYDataset();
            dataset.addSeries(table1, data1);
            dataset.addSeries(table2, data2);

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
            //  JunChartUtil.show(chart);
            
            // Output the graph to SVG file
            Path svgfile = Path.of("C:\\Users\\75496\\Documents\\E-Defense\\residualstraintimehistory\\residualstraintimehistorydouble_" + tableset + ".svg");
            try {
                JunChartUtil.svg(svgfile, 900, 300, chart);
            } catch (IOException ex) {
                Logger.getLogger(A001GraphTimeHistory111.class.getName()).log(Level.SEVERE, null, ex);
            }
            
                        // Display the chart in a frame
            ChartFrame frame = new ChartFrame("Inflection Point", chart);
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