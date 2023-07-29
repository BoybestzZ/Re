/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea01.columnbeam.Columnmomentdiagramew;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import jun.util.JunShapes;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author 75496
 */
public class C110GraphColumnCS2FA3 {

    public static void main(String[] args) throws IOException {
        String[] testnames = {"D01Q01", "D01Q02", "D01Q03", "D01Q04", "D01Q05", "D01Q06", "D01Q08", "D01Q09", 
                                  "D01Q10", "D01Q11", "D02Q01", "D02Q02", "D02Q03", "D02Q05", 
                                  "D02Q06", "D02Q08", "D03Q01", "D03Q02", "D03Q03", "D03Q04", "D03Q05", 
                                  "D03Q06", "D03Q08", "D03Q09"};
        
        String[] newLegendNames = {"D01Q01Random", "D01Q02KMMH02", "D01Q03FKS020", "D01Q04Kobe25", "D01Q05Kobe25X", "D01Q06Kobe25Y",
                              "D01Q08Kobe50", "D01Q09Random", "D01Q10Kobe75", "D01Q11Random", "D02Q01KMMH02", "D02Q02FKS020", 
                              "D02Q03Kobe100", "D02Q05Random", "D02Q06KMMH02", "D02Q08FKS020","D03Q01Random", "D03Q02KMMH02", 
                              "D03Q03FKS020", "D03Q04Kobe25", "D03Q05Kobe25X", "D03Q06Kobe25Y", "D03Q08Kobe75", "D03Q09Random"
        };


        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";

            // Connect to database
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();
            
            

            // Prepare Dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            for (int i = 0; i < testnames.length; i++) {
                String testname = testnames[i];
                String newLegendName = newLegendNames[i];

                String sql = "SELECT TESTNAME, CASE ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) WHEN 1 THEN 0.465 WHEN 2 THEN 1.04 WHEN 3 THEN 1.615 END AS NewColumn, \"StiffnessMomentXA[Nm/m]\"*0.000002, \"StiffnessMomentXP[rad]\" FROM \"A310SectionNM\" WHERE TESTNAME = '" + testname + "' AND SECTION LIKE 'CS2FA3%ew'";

                // Execute query.
                ResultSet rs = st.executeQuery(sql);

                // Prepare storage for XY data
                XYSeries series = new XYSeries(newLegendName);

                // Extract data from ResultSet and store the data to the XYseries
                while (rs.next()) {
                    //  String testname=rs.getString(1);
                    double sectionNo = rs.getDouble(2);
                    double amplitude = rs.getDouble(3);
                    double phase = rs.getDouble(4);
                    Complex stiffness = ComplexUtils.polar2Complex(amplitude, phase);
                    double stiffnessReal = stiffness.getReal();
                    series.add(stiffnessReal, sectionNo); // Store X,Y data.
                    // Change the key of XYSeries
                    // series.setKey(testname);
                }

                // Add the series to the dataset               
                dataset.addSeries(series);

            }
            // Prepare X adn Y axis
            NumberAxis xaxis = new NumberAxis("Bending Local Stiffness (kNm/mm)");
            NumberAxis yaxis = new NumberAxis("Section hight (m)");

            // Prepare Renderer
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
            // set Color
            renderer.setSeriesPaint(0, Color.BLACK);
            renderer.setSeriesPaint(1, Color.BLUE);
            renderer.setSeriesPaint(2, Color.GREEN);
            renderer.setSeriesPaint(3, Color.RED);
            renderer.setSeriesPaint(4, Color.RED);
            renderer.setSeriesPaint(5, Color.RED);
            renderer.setSeriesPaint(6, Color.RED);
            renderer.setSeriesPaint(7, Color.BLACK);
            renderer.setSeriesPaint(8, Color.RED);
            renderer.setSeriesPaint(9, Color.BLACK);
            renderer.setSeriesPaint(10, Color.BLUE);
            renderer.setSeriesPaint(11, Color.GREEN);
            renderer.setSeriesPaint(12, Color.RED);
            renderer.setSeriesPaint(13, Color.BLACK);
            renderer.setSeriesPaint(14, Color.BLUE);
            renderer.setSeriesPaint(15, Color.GREEN);
            renderer.setSeriesPaint(16, Color.BLACK);
            renderer.setSeriesPaint(17, Color.BLUE);
            renderer.setSeriesPaint(18, Color.GREEN);
            renderer.setSeriesPaint(19, Color.RED);
            renderer.setSeriesPaint(20, Color.RED);
            renderer.setSeriesPaint(21, Color.RED);
            renderer.setSeriesPaint(22, Color.RED);
            renderer.setSeriesPaint(23, Color.BLACK);


            
            
//            renderer.setSeriesShape(0, JunShapes.UPTRIANGLE);
//            renderer.setSeriesShape(1, JunShapes.SQUARE);
//            renderer.setSeriesShape(2, JunShapes.UPTRIANGLE);
//            renderer.setSeriesShape(3, JunShapes.SQUARE);
//            renderer.setSeriesShape(4, JunShapes.UPTRIANGLE);
//            renderer.setSeriesShape(5, JunShapes.SQUARE);
//            renderer.setSeriesShape(6, JunShapes.UPTRIANGLE);
//            renderer.setSeriesShape(7, JunShapes.RIGHTTRIANGLE);
//            renderer.setSeriesShape(8, JunShapes.UPTRIANGLE);
//            renderer.setSeriesShape(9, JunShapes.SQUARE);
//            renderer.setSeriesShape(10, JunShapes.UPTRIANGLE);
//            renderer.setSeriesShape(11, JunShapes.SQUARE);
//            renderer.setSeriesShape(12, JunShapes.UPTRIANGLE);
//            renderer.setSeriesShape(13, JunShapes.SQUARE);
//            renderer.setSeriesShape(14, JunShapes.UPTRIANGLE);
//            renderer.setSeriesShape(15, JunShapes.SQUARE);
//            renderer.setSeriesShape(16, JunShapes.UPTRIANGLE);
//            renderer.setSeriesShape(17, JunShapes.SQUARE);
//            renderer.setSeriesShape(18, JunShapes.UPTRIANGLE);
//            renderer.setSeriesShape(19, JunShapes.SQUARE);
//            renderer.setSeriesShape(20, JunShapes.UPTRIANGLE);
//            renderer.setSeriesShape(21, JunShapes.SQUARE);
//            renderer.setSeriesShape(22, JunShapes.UPTRIANGLE);
//            renderer.setSeriesShape(23, JunShapes.SQUARE);
            
            
            renderer.setSeriesShape(0, JunShapes.createUpTriangle(4));
            renderer.setSeriesShape(1, JunShapes.createUpTriangle(4));
            renderer.setSeriesShape(2, JunShapes.createUpTriangle(4));
            renderer.setSeriesShape(3, JunShapes.createUpTriangle(4));
            renderer.setSeriesShape(4, JunShapes.createRightTriangle(4));
            renderer.setSeriesShape(5, JunShapes.createDownTriangle(4));
            renderer.setSeriesShape(6, JunShapes.createLeftTriangle(4));
            renderer.setSeriesShape(7, JunShapes.createRightTriangle(4));
            renderer.setSeriesShape(8, JunShapes.createUpTriangle(4));
            renderer.setSeriesShape(9, JunShapes.createDownTriangle(4));
            renderer.setSeriesShape(10, JunShapes.createRightTriangle(4));
            renderer.setSeriesShape(11, JunShapes.createRightTriangle(4));
            renderer.setSeriesShape(12, JunShapes.createRightTriangle(4));
            renderer.setSeriesShape(13, JunShapes.createLeftTriangle(4));
            renderer.setSeriesShape(14, JunShapes.createDownTriangle(4));
            renderer.setSeriesShape(15, JunShapes.createDownTriangle(4));
            renderer.setSeriesShape(16, JunShapes.createUpTriangle(4));
            renderer.setSeriesShape(17, JunShapes.createLeftTriangle(4));
            renderer.setSeriesShape(18, JunShapes.createLeftTriangle(4));
            renderer.setSeriesShape(19, JunShapes.createDownTriangle(4));
            renderer.setSeriesShape(20, JunShapes.createLeftTriangle(4));
            renderer.setSeriesShape(21, JunShapes.createCircle(4));
            renderer.setSeriesShape(22, JunShapes.createCircle(4));
            renderer.setSeriesShape(23, JunShapes.createRightTriangle(4));
            
            
            
            renderer.setSeriesShapesFilled(0, Boolean.TRUE);
            renderer.setSeriesShapesFilled(1, Boolean.TRUE);
            renderer.setSeriesShapesFilled(2, Boolean.TRUE);
            renderer.setSeriesShapesFilled(3, Boolean.TRUE);
            renderer.setSeriesShapesFilled(4, Boolean.TRUE);
            renderer.setSeriesShapesFilled(5, Boolean.TRUE);
            renderer.setSeriesShapesFilled(6, Boolean.TRUE);
            renderer.setSeriesShapesFilled(7, Boolean.TRUE);
            renderer.setSeriesShapesFilled(8, Boolean.FALSE);
            renderer.setSeriesShapesFilled(9, Boolean.TRUE);
            renderer.setSeriesShapesFilled(10, Boolean.TRUE);
            renderer.setSeriesShapesFilled(11, Boolean.TRUE);
            renderer.setSeriesShapesFilled(12, Boolean.FALSE);
            renderer.setSeriesShapesFilled(13, Boolean.TRUE);
            renderer.setSeriesShapesFilled(14, Boolean.TRUE);
            renderer.setSeriesShapesFilled(15, Boolean.TRUE);
            renderer.setSeriesShapesFilled(16, Boolean.FALSE);
            renderer.setSeriesShapesFilled(17, Boolean.TRUE);
            renderer.setSeriesShapesFilled(18, Boolean.TRUE);
            renderer.setSeriesShapesFilled(19, Boolean.FALSE);
            renderer.setSeriesShapesFilled(20, Boolean.FALSE);
            renderer.setSeriesShapesFilled(21, Boolean.TRUE);
            renderer.setSeriesShapesFilled(22, Boolean.FALSE);
            renderer.setSeriesShapesFilled(23, Boolean.FALSE);


            
//            renderer.setSeriesShape(2,JunShapes.createButterfly(5));
            

            // Prepare XYPlot
            XYPlot plot = new XYPlot(dataset, xaxis, yaxis, renderer);

            // Create CHart
            JFreeChart chart = new JFreeChart(plot);
            
            
            // Customize the chart

            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.BLACK);
            plot.setDomainGridlinesVisible(true);
            plot.setDomainGridlinePaint(Color.BLACK);
            
            plot.setOutlinePaint(Color.BLACK);
            plot.setOutlineStroke(new BasicStroke(2f)); // frame around the plot
            plot.setAxisOffset(RectangleInsets.ZERO_INSETS); // remove space between frame and axis.
            
            
//            NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
//            domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont((int) 12f));
            

            // insert legend to the plot
            LegendTitle legend = chart.getLegend(); // obtain legend box
            XYTitleAnnotation ta=new XYTitleAnnotation(0 ,0.13, legend, RectangleAnchor.LEFT);
            legend.setBorder(1, 1, 1, 1); // frame around legend
            plot.addAnnotation(ta);
            chart.removeLegend();

            // Show Chart
//            JunChartUtil.show("2FA3ew", chart);
            JunChartUtil.show(chart);
            
            int width = 550;
            int height = 580;
            String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\Columnmoment\\EW\\CM_C2FA3ew.svg";
            JunChartUtil.svg(filePath, width, height, chart);
            
            


            // close connection
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(C110GraphColumnCS2FA3.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
