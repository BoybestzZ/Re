/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea01.columnbeam.beamcolumndifferentiate.columndifferentiate.ns;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.ui.TextAnchor;

public class A200BeamShearC3FB3nsdi {

    public static void main(String[] args) throws IOException {

        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";

            double distance = 1.15; // distance between Section 2 and Section 4;
            double section = 0.23;
            
            // Connect to database
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            String[][] testNamesArray = {
                {"D01Q01", "D01Q09", "D01Q11", "D02Q05", "D03Q01", "D03Q09"},
                {"D01Q02", "D02Q01", "D02Q06", "D03Q02"},
                {"D01Q03", "D02Q02", "D02Q08", "D03Q03"},
                {"D01Q04", "D01Q05", "D01Q06", "D01Q08", "D01Q10", "D02Q03", "D03Q04", "D03Q05", "D03Q06", "D03Q08"}
            };

            // Create table to store results
            st.executeUpdate("CREATE TABLE IF NOT EXISTS ShearForceResultsC3FB3nsdi(TestName VARCHAR(20), ShearForce DOUBLE)");

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            // Define line names and colors
            String[] lineNames = {"Random", "Kumamoto", "Tohoku", "Kobe"};
            Color[] lineColors = {Color.RED, Color.BLUE, Color.GREEN, Color.BLACK};

            for (int i = 0; i < testNamesArray.length; i++) {
                String[] testNames = testNamesArray[i];

                for (String testName : testNames) {
                    // Execute query and get result set
                    ResultSet rs = st.executeQuery("SELECT \"StiffnessMomentXA[Nm/m]\"*0.000001, \"StiffnessMomentXP[rad]\"*0.000001 FROM \"A310SectionNM\" where testname='" + testName + "' and section like 'CS3FB3Bns'");

                rs.next(); // goto the 1st line
                // get results
                double amplitudeS2 = rs.getDouble(1);
                double phaseS2 = rs.getDouble(2);

                // Execute query and get result set
                rs = st.executeQuery("SELECT \"StiffnessMomentXA[Nm/m]\"*0.000001, \"StiffnessMomentXP[rad]\"*0.000001 FROM \"A310SectionNM\" where testname='" + testName + "' and section like 'CS3FB3Tns'");

                rs.next();
                double amplitudeS4 = rs.getDouble(1);
                double phaseS4 = rs.getDouble(2);

                // if you want to consider phase
                Complex momentS2 = ComplexUtils.polar2Complex(amplitudeS2, phaseS2);
                Complex momentS4 = ComplexUtils.polar2Complex(amplitudeS4, phaseS4);
                Complex shearForceComplex = (momentS2.add(momentS4)).divide(distance);

                // Insert the result into the table
                String insertQuery = "INSERT INTO ShearForceResultsC3FB3nsdi (TestName, ShearForce) VALUES ('" + testName + "', " + shearForceComplex.getReal() + ")";
                st.executeUpdate(insertQuery);
                System.out.println("Record for TestName '" + testName + "' inserted into the table.");
                    // Add shear force value to the dataset with line name as series
                    dataset.addValue(shearForceComplex.getReal(), lineNames[i], testName);
                }
            }

            // Create the chart
            JFreeChart chart = ChartFactory.createLineChart("Sf_C3FB3nsdi", "Testname", "Shearforce (kN)",
                    dataset, PlotOrientation.VERTICAL, true, true, false);

            // Customize the chart
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.BLACK);
            plot.setDomainGridlinesVisible(true);
            plot.setDomainGridlinePaint(Color.BLACK);
            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont(9f));
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setRange(0, 10); // Set the y-axis range
            rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());

            LineAndShapeRenderer renderer = new LineAndShapeRenderer();
            
            // Set line colors and names
            for (int i = 0; i < lineNames.length; i++) {
                renderer.setSeriesPaint(i, lineColors[i]);
                renderer.setSeriesStroke(i, new BasicStroke(2.0f));
                renderer.setSeriesShape(i, renderer.getSeriesShape(i));
                renderer.setSeriesVisible(i, true);
                renderer.setSeriesItemLabelGenerator(i, new StandardCategoryItemLabelGenerator());
                renderer.setSeriesItemLabelsVisible(i, true);
                renderer.setSeriesToolTipGenerator(i, new StandardCategoryToolTipGenerator());
            }
            
            plot.setRenderer(renderer);

            // Export the chart as PNG
            int width = 1400;
            int height = 1000;
            String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\sea02\\sf_C3FB3nsdi.png";
            File chartFile = new File(filePath);
            ChartUtils.saveChartAsPNG(chartFile, chart, width, height);

            // Display the chart in a frame
            ChartFrame frame = new ChartFrame("Shear Force Results", chart);
            frame.setPreferredSize(new Dimension(1200, 800));
            frame.pack();
            frame.setVisible(true);

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A200BeamShearC3FB3nsdi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}













