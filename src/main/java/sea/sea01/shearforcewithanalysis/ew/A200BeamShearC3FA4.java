/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea01.shearforcewithanalysis.ew;

import java.awt.BasicStroke;
import sea.sea01.columnbeam.oldcolumnEW.columnEWJ.*;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ChoiceFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import jun.util.JunShapes;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;

/**
 * modified by Iyama. Use XYdataset. THe xaxisl will be number.
 *
 *
 * @author 75496
 */
public class A200BeamShearC3FA4 {

    public static void main(String[] args) throws IOException {

        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";

            double distance = 1.15; // distance between Section 2 and Section 4;

            // Connect to database
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

//            String[] testNames = {"D01Q01", "D01Q02", "D01Q03", "D01Q04", "D01Q05", "D01Q06", "D01Q08", "D01Q09",
//                "D01Q10", "D01Q11", "D02Q01", "D02Q01", "D02Q02", "D02Q03", "D02Q05",
//                "D02Q06", "D02Q07", "D02Q08", "D03Q01", "D03Q02", "D03Q03", "D03Q04", "D03Q05",
//                "D03Q06", "D03Q08", "D03Q09"};
//            
            EdefenseKasinInfo[] kasins = EdefenseInfo.alltests;

            // Prepare dataset
            DefaultXYDataset dataset = new DefaultXYDataset();
            XYSeries random = new XYSeries("random");
            XYSeries kumamoto = new XYSeries("kumamoto");
            XYSeries tohoku = new XYSeries("tohoku");
            XYSeries kobe = new XYSeries("kobe");
            
            // Create series for y = 6 black line
            XYSeries blackLine = new XYSeries("Analysis");
            for (int i = 0; i < kasins.length; i++) {
                blackLine.add(i + 1, 5.024);
            }

            // Create table to store results if it doesn't exist
            st.executeUpdate("CREATE TABLE IF NOT EXISTS ShearForceResultsC3FA4ewJ (TestName VARCHAR(20), ShearForce DOUBLE)");

            for (int i = 0; i < kasins.length; i++) {
                String testName = kasins[i].getTestName();  //D01Q01
                String waveName = kasins[i].getWaveName();  // Random

                // Execute query and get result set
                ResultSet rs = st.executeQuery("SELECT \"StiffnessMomentXA[Nm/m]\"*0.000002, \"StiffnessMomentXP[rad]\" FROM \"A310SectionNM\" where testname='" + testName + "' and section like 'CS3FA4Bew'");

                rs.next(); // goto the 1st line
                // get results
                double amplitudeS2 = rs.getDouble(1);
                double phaseS2 = rs.getDouble(2);

                // Execute query and get result set
                rs = st.executeQuery("SELECT \"StiffnessMomentXA[Nm/m]\"*0.000002, \"StiffnessMomentXP[rad]\" FROM \"A310SectionNM\" where testname='" + testName + "' and section like 'CS3FA4Tew'");

                rs.next();
                double amplitudeS4 = rs.getDouble(1);
                double phaseS4 = rs.getDouble(2);

                // if you want to consider phase
                Complex momentS2 = ComplexUtils.polar2Complex(amplitudeS2, phaseS2);
                Complex momentS4 = ComplexUtils.polar2Complex(amplitudeS4, phaseS4);
                Complex shearForceComplex = (momentS2.subtract(momentS4)).divide(distance);

                // Insert the result into the table
                String insertQuery = "INSERT INTO ShearForceResultsC3FA4ewJ (TestName, ShearForce) VALUES ('" + testName + "', " + shearForceComplex.getReal() + ")";
                st.executeUpdate(insertQuery);
                System.out.println("Record for TestName '" + testName + "' inserted into the table.");

                if (waveName.equals("Random")) {
                    random.add(i + 1, shearForceComplex.getReal());
                } else if (waveName.equals("KMMH02")) {
                    kumamoto.add(i + 1, shearForceComplex.getReal());
                } else if (waveName.equals("FKS020")) {
                    tohoku.add(i + 1, shearForceComplex.getReal());
                } else if (waveName.startsWith("Kobe")) {
                    kobe.add(i + 1, shearForceComplex.getReal());

                }

            }

          
            dataset.addSeries("Random", random.toArray());
            dataset.addSeries("tohoku", tohoku.toArray());
            dataset.addSeries("kumamoto", kumamoto.toArray());
            dataset.addSeries("kobe", kobe.toArray());
            
                // Add the black line series to the dataset
            dataset.addSeries("Analysis", blackLine.toArray());
            
            // Create the chart
            JFreeChart chart = ChartFactory.createXYLineChart("", "Test No.", "Shear local stiffness (kN/mm)",
                    dataset, PlotOrientation.VERTICAL, true, true, false);

            // Customize the chart
            XYPlot plot = chart.getXYPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.BLACK);
            plot.setDomainGridlinesVisible(true);
            plot.setDomainGridlinePaint(Color.BLACK);
            NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
            domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont(12f));
            domainAxis.setVerticalTickLabels(true);
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setRange(0, 15); // Set the y-axis range
            domainAxis.setLowerBound(0.1);
            domainAxis.setUpperBound(24.9);
            rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
            plot.setOutlineStroke(new BasicStroke(2f)); // frame around the plot
            plot.setAxisOffset(RectangleInsets.ZERO_INSETS); // remove space between frame and axis.

            
            // Prepare the mapping of test names to labels dynamically
            String[] testNames = new String[kasins.length];
            double[] testValues = new double[kasins.length];

            for (int i = 0; i < kasins.length; i++) {
                testNames[i] = kasins[i].getTestName() + kasins[i].getWaveName();
                testValues[i] = i + 1.0;
            }

            // Create the ChoiceFormat
            ChoiceFormat formatter = new ChoiceFormat(testValues, testNames);
            domainAxis.setNumberFormatOverride(formatter);
            
            
//            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesPaint(0, Color.RED);
            plot.setRenderer(renderer);
            
            
            renderer.setSeriesShape(0, JunShapes.createUpTriangle(4));
            renderer.setSeriesShape(1, JunShapes.createUpTriangle(4));
            renderer.setSeriesShape(2, JunShapes.createUpTriangle(4));
            renderer.setSeriesShape(3, JunShapes.createUpTriangle(4));
//            renderer.setSeriesShape(4,null);
            renderer.setSeriesShapesVisible(4, false);

            
            renderer.setSeriesPaint(0, Color.RED);
            renderer.setSeriesPaint(1, Color.BLUE);
            renderer.setSeriesPaint(2, Color.GREEN);
            renderer.setSeriesPaint(3, Color.ORANGE);
            renderer.setSeriesPaint(4, Color.BLACK);
           
            
            BasicStroke newStroke = new BasicStroke(1.0f); // Creating a new stroke with thickness 2.0f
            renderer.setSeriesStroke(4, newStroke); // Setting the new stroke for series at index 4

            

            
//                // Add horizontal line y = 6
//            double[] xValues = { 0, 24 }; // Adjust the range of x-values as needed
//            double[] yValues = { 6, 6 };
//            XYSeries horizontalLine = new XYSeries("Analysis");
//            horizontalLine.add(xValues[0], yValues[0]);
//            horizontalLine.add(xValues[1], yValues[1]);
//            XYSeriesCollection hLineDataset = new XYSeriesCollection();
//            hLineDataset.addSeries(horizontalLine);
//            plot.setDataset(1, hLineDataset);
//            plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));
//            plot.getRenderer(1).setSeriesPaint(0, Color.BLACK);
//            plot.getRenderer(1).setSeriesStroke(0, new BasicStroke(2));
            
            
            // insert legend to the plot
            LegendTitle legend = chart.getLegend(); // obtain legend box
            XYTitleAnnotation ta=new XYTitleAnnotation(0.95 ,0.05, legend, RectangleAnchor.BOTTOM_RIGHT);
            legend.setBorder(1, 1, 1, 1); // frame around legend
            plot.addAnnotation(ta);
            chart.removeLegend();
            
            

            // Export the chart as PNG
            int width = 650;
            int height = 350;
//            String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\sea01\\sf_C2FA3ew.png";
//            File chartFile = new File(filePath);
//            ChartUtils.saveChartAsPNG(chartFile, chart, width, height);

              String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\sea02\\sf_C3FA4ewwithanalysis.svg";
              JunChartUtil.svg(filePath, width, height, chart);

//            // Display the chart in a frame
            ChartFrame frame = new ChartFrame("Shear Force Results", chart);
            frame.setPreferredSize(new Dimension(1200, 800));
            frame.pack();
            frame.setVisible(true);

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A200BeamShearC3FA4.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
