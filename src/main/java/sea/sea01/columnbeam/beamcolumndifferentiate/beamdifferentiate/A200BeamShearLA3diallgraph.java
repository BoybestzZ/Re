

package sea.sea01.columnbeam.beamcolumndifferentiate.beamdifferentiate;

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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;

public class A200BeamShearLA3diallgraph {

    // Define the custom order for the x-axis labels
    private static final String[] customTestOrder = {
        "D01Q01", "D01Q02", "D01Q03", "D01Q04", "D01Q05", "D01Q08", "D01Q09",
        "D01Q10", "D01Q11", "D02Q01", "D02Q02", "D02Q03", "D02Q05", "D02Q06",
        "D03Q01", "D03Q02", "D03Q03", "D03Q04", "D03Q05", "D03Q08", "D03Q09"
    };

    public static void main(String[] args) throws IOException {

        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";
            double distance = 1.24; // distance between Section 2 and Section 4;
            double section = 0.23;

            // Connect to database
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            String[][] testNamesArray = {
                {"D01Q01", "D01Q09", "D01Q11", "D02Q05", "D03Q01", "D03Q09"},
                {"D01Q02", "D02Q01", "D02Q06", "D03Q02"},
                {"D01Q03", "D02Q02", "D02Q08", "D03Q03"},
                {"D01Q04", "D01Q05", "D01Q08", "D01Q10", "D02Q03", "D03Q04", "D03Q05", "D03Q08"}
            };

            // Create table to store results if it doesn't exist
            st.executeUpdate("DROP TABLE IF EXISTS ShearForceResultsLA3di");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS ShearForceResultsLA3di (TestName VARCHAR(20), ShearForce DOUBLE)");

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            // Define line names and colors
            String[] lineNames = {"Random", "Kumamoto", "Tohoku", "Kobe"};
            Color[] lineColors = {Color.RED, Color.BLUE, Color.GREEN, Color.BLACK};

            for (int i = 0; i < testNamesArray.length; i++) {
                String[] testNames = testNamesArray[i];

                for (String testName : testNames) {
                    // Execute query and get result set
                    ResultSet rs = st.executeQuery("SELECT \"StiffnessAxialA[N/m]\"*0.000002, \"StiffnessAxialP[rad]\", \"StiffnessMomentXA[Nm/m]\"*0.000002, \"StiffnessMomentXP[rad]\" FROM \"A310SectionNM\" where TESTNAME='" + testName + "' and SECTION='LA3S2';");
                    rs.next();

                    // get results
                    double axialAmplitudeS2 = rs.getDouble(1);
                    double axialPhaseS2 = rs.getDouble(2);
                    double momentAmplitudeS2 = rs.getDouble(3);
                    double momentPhaseS2 = rs.getDouble(4);

                    // Execute query and get result set
                    rs = st.executeQuery("SELECT \"StiffnessAxialA[N/m]\"*0.000002, \"StiffnessAxialP[rad]\", \"StiffnessMomentXA[Nm/m]\"*0.000002, \"StiffnessMomentXP[rad]\" FROM \"A310SectionNM\" where TESTNAME='" + testName + "' and SECTION='LA3S4';");

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
                    Complex shearForceComplex = (allmoment4.subtract(allmoment2)).divide(distance);

                    // Insert the result into the table
                    st.executeUpdate("INSERT INTO ShearForceResultsLA3di (TestName, ShearForce) VALUES ('" + testName + "', " + shearForceComplex.getReal() + ")");

                    // Add shear force value to the dataset with line name as series
                    dataset.addValue(shearForceComplex.getReal(), lineNames[i], testName);
                }
            }

            // Create the dataset and populate it with the percentage values
            DefaultCategoryDataset percentageDataset = new DefaultCategoryDataset();
            for (int i = 0; i < lineNames.length; i++) {
                String lineName = lineNames[i];
                List<Double> shearForceValues = new ArrayList<>();

                // Get shear force values for each test in the custom order
                for (String testName : customTestOrder) {
                    ResultSet rsPercentage = st.executeQuery("SELECT ShearForce FROM ShearForceResultsLA3di WHERE TestName = '" + testName + "'");
                    if (rsPercentage.next()) {
                        shearForceValues.add(rsPercentage.getDouble("ShearForce"));
                    }
                }

                // Calculate the percentage values for the current line
                double firstShearForceValue = shearForceValues.isEmpty() ? 0 : shearForceValues.get(0);
                List<Double> percentages = new ArrayList<>();
                for (Double value : shearForceValues) {
                    double percentage = firstShearForceValue == 0 ? 0 : value / firstShearForceValue * 100;
                    percentages.add(percentage);
                }

                // Add the calculated percentages to the dataset with line name as series
                for (int j = 0; j < customTestOrder.length; j++) {
                    String testName = customTestOrder[j];
                    double percentageValue = percentages.isEmpty() ? 0 : percentages.get(j);
                    percentageDataset.addValue(percentageValue, lineName, testName);
                }
            }

            // Create the line chart for shear force values
            JFreeChart shearForceChart = ChartFactory.createLineChart(
                    "Sf_LA3di", "Testname", "Shearforce (kN)",
                    dataset, PlotOrientation.VERTICAL, true, true, false);

            // Customize the shear force chart
            CategoryPlot shearForcePlot = shearForceChart.getCategoryPlot();
            shearForcePlot.setBackgroundPaint(Color.WHITE);
            shearForcePlot.setRangeGridlinePaint(Color.BLACK);
            shearForcePlot.setDomainGridlinesVisible(true);
            shearForcePlot.setDomainGridlinePaint(Color.BLACK);
            CategoryAxis domainAxisShearForce = shearForcePlot.getDomainAxis();
            domainAxisShearForce.setTickLabelFont(domainAxisShearForce.getTickLabelFont().deriveFont(9f));
            NumberAxis rangeAxisShearForce = (NumberAxis) shearForcePlot.getRangeAxis();
            rangeAxisShearForce.setRange(0, 10); // Set the y-axis range
            rangeAxisShearForce.setStandardTickUnits(NumberAxis.createStandardTickUnits());

            LineAndShapeRenderer shearForceRenderer = new LineAndShapeRenderer();
            for (int i = 0; i < lineNames.length; i++) {
                shearForceRenderer.setSeriesPaint(i, lineColors[i]);
                shearForceRenderer.setSeriesStroke(i, new BasicStroke(2.0f));
                shearForceRenderer.setSeriesVisible(i, true);
                shearForceRenderer.setSeriesItemLabelGenerator(i, new StandardCategoryItemLabelGenerator());
                shearForceRenderer.setSeriesItemLabelsVisible(i, true);
                shearForceRenderer.setSeriesToolTipGenerator(i, new StandardCategoryToolTipGenerator());
            }

            shearForcePlot.setRenderer(shearForceRenderer);

            // Export the shear force chart as PNG
            int width = 1400;
            int height = 1000;
            String shearForceFilePath = "C:\\Users\\75496\\Documents\\E-Defense\\sea01\\sf_LA3di.png";
            File shearForceChartFile = new File(shearForceFilePath);
            ChartUtils.saveChartAsPNG(shearForceChartFile, shearForceChart, width, height);

            // Create the line chart for percentage values
            JFreeChart percentageLineChart = ChartFactory.createLineChart(
                    "Percentage Line Chart", "Test Name", "Percentage",
                    percentageDataset, PlotOrientation.VERTICAL, true, true, false);

            // Customize the percentage line chart
            CategoryPlot percentagePlot = percentageLineChart.getCategoryPlot();
            LineAndShapeRenderer percentageRenderer = new LineAndShapeRenderer();
            for (int i = 0; i < lineColors.length; i++) {
                percentageRenderer.setSeriesPaint(i, lineColors[i]);
            }
            percentagePlot.setRenderer(percentageRenderer);
            percentagePlot.setBackgroundPaint(Color.WHITE);
            percentagePlot.setRangeGridlinePaint(Color.BLACK);
            percentagePlot.setDomainGridlinesVisible(true);
            percentagePlot.setDomainGridlinePaint(Color.BLACK);

            CategoryAxis domainAxisPercentage = percentagePlot.getDomainAxis();
            domainAxisPercentage.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
            domainAxisPercentage.setCategoryMargin(0.1);
            domainAxisPercentage.setMaximumCategoryLabelLines(3);
            domainAxisPercentage.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
            domainAxisPercentage.setTickLabelsVisible(true);

            NumberAxis rangeAxisPercentage = (NumberAxis) percentagePlot.getRangeAxis();
            rangeAxisPercentage.setRange(50, 110); // Set the y-axis range
            rangeAxisPercentage.setStandardTickUnits(NumberAxis.createStandardTickUnits());

            // Display the percentage line chart using a ChartPanel in a JFrame
            ChartPanel percentageChartPanel = new ChartPanel(percentageLineChart);
            percentageChartPanel.setPreferredSize(new Dimension(800, 600));
            JFrame percentageFrame = new JFrame("");
            percentageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            percentageFrame.add(percentageChartPanel);
            percentageFrame.pack();
            percentageFrame.setVisible(true);

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A200BeamShearLA3diallgraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}