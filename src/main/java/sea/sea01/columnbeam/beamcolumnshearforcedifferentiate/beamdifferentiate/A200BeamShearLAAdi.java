/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea01.columnbeam.beamcolumnshearforcedifferentiate.beamdifferentiate;

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

public class A200BeamShearLAAdi {

    public static void main(String[] args) throws IOException {

        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";

            double distance = 1.63; // distance between Section 2 and Section 4;
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
            st.executeUpdate("CREATE TABLE IF NOT EXISTS ShearForceResultsLAAdi (TestName VARCHAR(20), ShearForce DOUBLE)");

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            // Define line names and colors
            String[] lineNames = {"Random", "Kumamoto", "Tohoku", "Kobe"};
            Color[] lineColors = {Color.RED, Color.BLUE, Color.GREEN, Color.BLACK};

            for (int i = 0; i < testNamesArray.length; i++) {
                String[] testNames = testNamesArray[i];

                for (String testName : testNames) {
                    // Execute query and get result set
                    ResultSet rs = st.executeQuery("SELECT \"StiffnessAxialA[N/m]\"*0.000001, \"StiffnessAxialP[rad]\"*0.000001, \"StiffnessMomentXA[Nm/m]\"*0.000001, \"StiffnessMomentXP[rad]\"*0.000001 FROM \"A310SectionNM\" where TESTNAME='" + testName + "' and SECTION='LAAS2';");
                    rs.next();

                    // get results
                    double axialAmplitudeS2 = rs.getDouble(1);
                    double axialPhaseS2 = rs.getDouble(2);
                    double momentAmplitudeS2 = rs.getDouble(3);
                    double momentPhaseS2 = rs.getDouble(4);

                    // Execute query and get result set
                    rs = st.executeQuery("SELECT \"StiffnessAxialA[N/m]\"*0.000001, \"StiffnessAxialP[rad]\"*0.000001, \"StiffnessMomentXA[Nm/m]\"*0.000001, \"StiffnessMomentXP[rad]\"*0.000001 FROM \"A310SectionNM\" where TESTNAME='" + testName + "' and SECTION='LAAS4';");

                    rs.next();

                    double axialAmplitudeS4 = rs.getDouble(1);
                    double axialPhaseS4 = rs.getDouble(2);
                    double momentAmplitudeS4 = rs.getDouble(3);
                    double momentPhaseS4 = rs.getDouble(4);

                    Complex momentS2 = ComplexUtils.polar2Complex(momentAmplitudeS2, momentPhaseS2);
                    Complex momentS4 = ComplexUtils.polar2Complex(momentAmplitudeS4, momentPhaseS4);
                    Complex axialMomentS2 = ComplexUtils.polar2Complex(axialAmplitudeS2, axialPhaseS2);
                    Complex axialMomentS4 = ComplexUtils.polar2Complex(axialAmplitudeS4, axialPhaseS4);
                    Complex allmoment2 = momentS2.add((axialMomentS2).multiply(section));
                    Complex allmoment4 = momentS4.add((axialMomentS4).multiply(section));
                    Complex shearForceComplex = (allmoment2.add(allmoment4)).divide(distance);

                    // Insert the result into the table
                    st.executeUpdate("INSERT INTO ShearForceResultsLAAdi (TestName, ShearForce) VALUES ('" + testName + "', " + shearForceComplex.getReal() + ")");

                    // Add shear force value to the dataset with line name as series
                    dataset.addValue(shearForceComplex.getReal(), lineNames[i], testName);
                }
            }

            // Create the chart
            JFreeChart chart = ChartFactory.createLineChart("Sf_LAAdi", "Testname", "Shearforce (kN)",
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
            String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\sea01\\sf_LAAdi.png";
            File chartFile = new File(filePath);
            ChartUtils.saveChartAsPNG(chartFile, chart, width, height);

            // Display the chart in a frame
            ChartFrame frame = new ChartFrame("Shear Force Results", chart);
            frame.setPreferredSize(new Dimension(1200, 800));
            frame.pack();
            frame.setVisible(true);

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A200BeamShearLAAdi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}













