/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea01.columnbeam.Beammomentpercentage;

import sea.sea01.columnbeam.shearforcepercentagenokobe.*;
import sea.sea01.columnbeam.beamcolumndifferentiate.beamdifferentiate.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import jun.chart.JunChartUtil;

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
import org.jfree.chart.ChartPanel;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryAnnotation; // Import the correct annotation class
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.ui.RectangleInsets;

public class A200BeamMPLA4nk {

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
                {"D01Q03", "D02Q02", "D02Q08", "D03Q03"}
            };


            DefaultCategoryDataset dataset = new DefaultCategoryDataset();


            // Define line names and colors
            String[] lineNames = {"Random", "Kumamoto", "Tohoku"};
            Color[] lineColors = {Color.RED, Color.BLUE, Color.GREEN, Color.BLACK};
            
            // Create a list to store shear force values for each line name
            List<Double> shearForceValuesRandom = new ArrayList<>();
            List<Double> shearForceValuesKumamoto = new ArrayList<>();
            List<Double> shearForceValuesTohoku = new ArrayList<>();

            

            for (int i = 0; i < testNamesArray.length; i++) {
                String[] testNames = testNamesArray[i];

                for (String testName : testNames) {
                    // Execute query and get result set
                    ResultSet rs = st.executeQuery("SELECT \"StiffnessAxialA[N/m]\"*0.000002, \"StiffnessAxialP[rad]\", \"StiffnessMomentXA[Nm/m]\"*0.000002, \"StiffnessMomentXP[rad]\" FROM \"A310SectionNM\" where TESTNAME='" + testName + "' and SECTION='LA4S2';");
                    rs.next();

                    // get results
                    double axialAmplitudeS2 = rs.getDouble(1);
                    double axialPhaseS2 = rs.getDouble(2);
                    double momentAmplitudeS2 = rs.getDouble(3);
                    double momentPhaseS2 = rs.getDouble(4);


                    Complex momentS2 = ComplexUtils.polar2Complex(momentAmplitudeS2, momentPhaseS2);
                    Complex axialMomentS2 = ComplexUtils.polar2Complex(axialAmplitudeS2, axialPhaseS2);
                    Complex allmoment2 = momentS2.add((axialMomentS2).multiply(section));



                    // Add shear force value to the dataset with line name as series
                    dataset.addValue(allmoment2.getReal(), lineNames[i], testName);

                    
                    
                    
                    
                    // Store the shear force value in the corresponding ArrayList based on line name
                    if (lineNames[i].equals("Random")){
                        shearForceValuesRandom.add(allmoment2.getReal());
                    } else if (lineNames[i].equals("Kumamoto")) {
                        shearForceValuesKumamoto.add(allmoment2.getReal());
                    } else if (lineNames[i].equals("Tohoku")) {
                        shearForceValuesTohoku.add(allmoment2.getReal());
                    } 
    }
}



                    // Create a list to store shear force values for each line name
                    List<List<Double>> momentValuesList = new ArrayList<>();
                    momentValuesList.add(shearForceValuesRandom);
                    momentValuesList.add(shearForceValuesKumamoto);
                    momentValuesList.add(shearForceValuesTohoku);


                    List<Double> firstShearForceValues = new ArrayList<>();
                    List<List<Double>> percentagesList = new ArrayList<>();

                    // Calculate the percentage values for each line name and store the first shear force value
                    for (int i = 0; i < lineNames.length; i++) {
                        List<Double> shearForceValues = momentValuesList.get(i);
                        double firstMomentValue = shearForceValues.isEmpty() ? 0 : shearForceValues.get(0);
                        firstShearForceValues.add(firstMomentValue);
                        List<Double> percentages = new ArrayList<>();

                        // Calculate the percentage values for the current line
                        for (Double value : shearForceValues) {
                            double percentage = firstMomentValue == 0 ? 0 : value / firstMomentValue * 100;
                            percentages.add(percentage);
                        }

                        percentagesList.add(percentages);
                    }
                   

                    // Print the calculated percentages
                    System.out.println(percentagesList.get(0));
                   
                    // Print all elements in the percentagesList
                    System.out.println("Percentage Values List:");
                    for (int i = 0; i < lineNames.length; i++) {
                        String lineName = lineNames[i];
                        List<Double> percentages = percentagesList.get(i);
                        System.out.println("Line: " + lineName);
                        for (int j = 0; j < testNamesArray[i].length; j++) {
                            String testName = testNamesArray[i][j];
                            double percentageValue = percentages.isEmpty() ? 0 : percentages.get(j);
                            System.out.printf("Test: %s, Percentage: %.2f%%%n", testName, percentageValue);
                        }
                        System.out.println();
                    }

                    // Create a dataset to hold the percentage values for each line name
                    DefaultCategoryDataset percentageDataset = new DefaultCategoryDataset();

                    // Add percentage values to the dataset
                    for (int i = 0; i < lineNames.length; i++) {
                        String lineName = lineNames[i];
                        List<Double> percentages = percentagesList.get(i);
                        for (int j = 0; j < testNamesArray[i].length; j++) {
                            String testName = testNamesArray[i][j];
                            double percentageValue = percentages.isEmpty() ? 0 : percentages.get(j);
                            percentageDataset.addValue(percentageValue, lineName, testName); // Use testName as the x-axis label
                        }
                    }

                    // Create the line chart
                    JFreeChart lineChart = ChartFactory.createLineChart(
                            "", // Chart title
                            "Test No.", // X-axis label
                            "Percentage (%)", // Y-axis label
                            percentageDataset, // Dataset
                            PlotOrientation.VERTICAL,
                            true, // Include legend
                            true, // Include tooltips
                            false // Include URLs
                    );

                    // Customize the appearance of the chart
                    CategoryPlot plot = lineChart.getCategoryPlot();
                    LineAndShapeRenderer renderer = new LineAndShapeRenderer();
                    for (int i = 0; i < lineColors.length; i++) {
                        renderer.setSeriesPaint(i, lineColors[i]);
                    }
                    plot.setRenderer(renderer);
                    plot.setBackgroundPaint(Color.WHITE);
                    plot.setRangeGridlinePaint(Color.BLACK);
                    plot.setDomainGridlinesVisible(true);
                    plot.setDomainGridlinePaint(Color.BLACK);
                    
//                    // Set line colors and names
//                    for (int i = 0; i < lineNames.length; i++) {
//                        renderer.setSeriesPaint(i, lineColors[i]);
//                        renderer.setSeriesStroke(i, new BasicStroke(2.0f));
//                        renderer.setSeriesShape(i, renderer.getSeriesShape(i));
//                        renderer.setSeriesVisible(i, true);
//                        renderer.setSeriesItemLabelGenerator(i, new StandardCategoryItemLabelGenerator());
//                        renderer.setSeriesItemLabelsVisible(i, true);
//                        renderer.setSeriesToolTipGenerator(i, new StandardCategoryToolTipGenerator());
//                    }
                    

                    // Set custom x-axis labels to match the test names
                    CategoryAxis domainAxis = (CategoryAxis) plot.getDomainAxis();
                    domainAxis.setTickLabelsVisible(true);
                    domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
                    domainAxis.setCategoryMargin(0.1);
                    domainAxis.setMaximumCategoryLabelLines(3);
                   
                    // Rotate the x-axis labels vertically
                    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                    
                    plot.setOutlinePaint(Color.BLACK);
                    plot.setOutlineStroke(new BasicStroke(2f)); // frame around the plot
                    plot.setAxisOffset(RectangleInsets.ZERO_INSETS); // remove space between frame and axis.
                    
                     NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
                    rangeAxis.setRange(0, 110); // Set the y-axis range
                    rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
                    
//                    // insert legend to the plot(does not work)
//                    LegendTitle legend = lineChart.getLegend(); // obtain legend box
//                    XYTitleAnnotation ta=new XYTitleAnnotation(0.95 ,0.05, legend, RectangleAnchor.BOTTOM_RIGHT);
//                    legend.setBorder(1, 1, 1, 1); // frame around legend
//                    plot.addAnnotation((CategoryAnnotation) ta);
//                    lineChart.removeLegend();
                    
                    
                  

                    // Display the chart using a ChartPanel in a JFrame
                    ChartPanel chartPanel = new ChartPanel(lineChart);
                    chartPanel.setPreferredSize(new Dimension(800, 600));
                    JFrame frame = new JFrame("");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.add(chartPanel);
                    frame.pack();
                    frame.setVisible(true);
                    
                    // Export the chart as PNG
                    int width = 350;
                    int height = 250;
        //            String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\sea01\\sf_C2FA3ew.png";
        //            File chartFile = new File(filePath);
        //            ChartUtils.saveChartAsPNG(chartFile, chart, width, height);

                      String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\sea01\\momentpercentage\\momentpernokobe_LA4.svg";
                      JunChartUtil.svg(filePath, width, height, lineChart);
                    
                    

        } catch (SQLException ex) {
            Logger.getLogger(A200BeamMPLA4nk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}













