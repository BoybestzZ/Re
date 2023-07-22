/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea01;

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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;

public class A200BeamShearLA3connectline {

    public static void main(String[] args) throws IOException {

        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";

            double distance = 1.24; // distance between Section 2 and Section 4;
            double section = 0.23;

            // Connect to the database
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            // Define the test names and their corresponding groups
            String[] testNames = {
                    "D01Q01", "D01Q02", "D01Q03", "D01Q04", "D01Q05", "D01Q06",
                    "D01Q08", "D01Q09", "D01Q10", "D01Q11",
                    "D02Q01", "D02Q02", "D02Q03", "D02Q05",
                    "D02Q06", "D02Q08", "D03Q01", "D03Q02", "D03Q03", "D03Q04", "D03Q05",
                    "D03Q06", "D03Q08", "D03Q09"
            };

            String[] groupNames = {
                    "Random", "Kumamoto", "Tohoku", "Kobe", "Kobe", "Kobe",
                    "Kobe", "Random", "Kobe", "Random",
                    "Kumamoto", "Tohoku", "Kobe", "Random",
                    "Kumamoto", "Tohoku", "Random", "Kumamoto", "Tohoku", "Kobe", "Kobe", "Kobe", "Kobe", "Random"
            };

            // Create table to store results
            st.executeUpdate("CREATE TABLE IF NOT EXISTS ShearForceResultsLA3ConnectLine (TestName VARCHAR(20), ShearForce DOUBLE)");

            for (int i = 0; i < testNames.length; i++) {
                String testName = testNames[i];
                String groupName = groupNames[i];

                // Execute query and get result set
                ResultSet rs = st.executeQuery("SELECT \"StiffnessAxialA[N/m]\" * 0.000001, \"StiffnessAxialP[rad]\" * 0.000001, \"StiffnessMomentXA[Nm/m]\" * 0.000001, \"StiffnessMomentXP[rad]\" * 0.000001 FROM \"A310SectionNM\" WHERE TESTNAME = '" + testName + "' AND SECTION = 'LA3S2';");
                rs.next();

                // Get results
                double axialAmplitudeS2 = rs.getDouble(1);
                double axialPhaseS2 = rs.getDouble(2);
                double momentAmplitudeS2 = rs.getDouble(3);
                double momentPhaseS2 = rs.getDouble(4);

                // Execute query and get result set
                rs = st.executeQuery("SELECT \"StiffnessAxialA[N/m]\" * 0.000001, \"StiffnessAxialP[rad]\" * 0.000001, \"StiffnessMomentXA[Nm/m]\" * 0.000001, \"StiffnessMomentXP[rad]\" * 0.000001 FROM \"A310SectionNM\" WHERE TESTNAME = '" + testName + "' AND SECTION = 'LA3S4';");

                rs.next();

                double axialAmplitudeS4 = rs.getDouble(1);
                double axialPhaseS4 = rs.getDouble(2);
                double momentAmplitudeS4 = rs.getDouble(3);
                double momentPhaseS4 = rs.getDouble(4);

                Complex momentS2 = ComplexUtils.polar2Complex(momentAmplitudeS2, momentPhaseS2);
                Complex momentS4 = ComplexUtils.polar2Complex(momentAmplitudeS4, momentPhaseS4);
                Complex axialMomentS2 = ComplexUtils.polar2Complex(axialAmplitudeS2, axialPhaseS2);
                Complex axialMomentS4 = ComplexUtils.polar2Complex(axialAmplitudeS4, axialPhaseS4);
                Complex allmoment2 = momentS2.add(axialMomentS2.multiply(section));
                Complex allmoment4 = momentS4.add(axialMomentS4.multiply(section));
                Complex shearForceComplex = allmoment2.add(allmoment4).divide(distance);

                // Insert the result into the table
                st.executeUpdate("INSERT INTO ShearForceResultsLA3ConnectLine (TestName, ShearForce) VALUES ('" + testName + "', " + shearForceComplex.getReal() + ")");
            }

            // Display the results from the table
            ResultSet rs = st.executeQuery("SELECT * FROM ShearForceResultsLA3ConnectLine");
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            while (rs.next()) {
                String testName = rs.getString("TestName");
                double shearForce = rs.getDouble("ShearForce");
                String groupName = getGroupName(testName, testNames, groupNames);
                dataset.addValue(shearForce, groupName, testName);
                System.out.println("Test Name: " + testName);
                System.out.println("Shear force (considering phase) = " + shearForce);
                System.out.println(); // Add a new line for separation
            }

            // Create the chart
            JFreeChart chart = ChartFactory.createLineChart("Sf_LA3ConnectLine", "Test Name", "Shear Force (kN)",
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
            renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            renderer.setSeriesPaint(0, Color.RED);
            renderer.setSeriesStroke(1, new BasicStroke(2.0f));
            renderer.setSeriesPaint(1, Color.BLUE);
            renderer.setSeriesStroke(2, new BasicStroke(2.0f));
            renderer.setSeriesPaint(2, Color.GREEN);
            renderer.setSeriesStroke(3, new BasicStroke(2.0f));
            renderer.setSeriesPaint(3, Color.ORANGE);
            plot.setRenderer(renderer);

            // Export the chart as PNG
            int width = 1400;
            int height = 1000;
            String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\sea01\\sf_LA3ConnectLine.png";
            File chartFile = new File(filePath);
            ChartUtils.saveChartAsPNG(chartFile, chart, width, height);

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A200BeamShearLA3ConnectLine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String getGroupName(String testName, String[] testNames, String[] groupNames) {
        for (String groupName : groupNames) {
            String[] tests = getTestsInGroup(groupName, testNames);
            if (containsTest(testName, tests)) {
                return groupName;
            }
        }
        return "";
    }

    private static boolean containsTest(String testName, String[] tests) {
        for (String test : tests) {
            if (test.equals(testName)) {
                return true;
            }
        }
        return false;
    }

    private static String[] getTestsInGroup(String groupName, String[] testNames) {
        if (groupName.equals("Random")) {
            return new String[]{"D01Q01", "D01Q09", "D01Q11", "D02Q05", "D03Q01", "D03Q09"};
        } else if (groupName.equals("Kumamoto")) {
            return new String[]{"D01Q02", "D02Q01", "D02Q06", "D03Q02"};
        } else if (groupName.equals("Tohoku")) {
            return new String[]{"D01Q03", "D02Q02", "D02Q08", "D03Q03"};
        } else if (groupName.equals("Kobe")) {
            return new String[]{"D01Q04", "D01Q05", "D01Q06", "D01Q08", "D01Q10", "D02Q03", "D03Q04", "D03Q05", "D03Q06", "D03Q08"};
        }
        return new String[]{};
    }
}









