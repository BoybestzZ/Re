/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea03.NeutralAxis.Beam;

import sea.sea01.columnbeam.beamcolumnshearforcedifferentiate.beamdifferentiate.*;
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
import java.text.ChoiceFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
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
public class A700NeutralAxisLA3 {

    public static void main(String[] args) throws IOException {

        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";
            
            double distance = 0.326; // distance between inner web (Beam3 = 350 - 2*12)
            double slab = 0.11;


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

            // Create table to store results if it doesn't exist
            st.executeUpdate("CREATE TABLE IF NOT EXISTS ShearForceResultsC2FA3ew (TestName VARCHAR(20), ShearForce DOUBLE)");

            for (int i = 0; i < kasins.length; i++) {
                String testName = kasins[i].getTestName();  //D01Q01
                String waveName = kasins[i].getWaveName();  // Random

                // Execute query and get result set
                    ResultSet rs = st.executeQuery("SELECT \"Strain1A[με*s]\", \"Strain1P[rad]\", \"Strain2A[με*s]\", \"Strain2P[rad]\",  \"Strain3A[με*s]\", \"Strain3P[rad]\",  \"Strain4A[με*s]\", \"Strain4P[rad]\", FROM \"A310SectionNM\" where TESTNAME = '" + testName + "' and SECTION = 'LA3S2'");
                    rs.next();

                    // get results
                    double UL = rs.getDouble(1);
                    double phaseUL = rs.getDouble(2);
                    double UR = rs.getDouble(3);
                    double phaseUR = rs.getDouble(4);
                    double DL = rs.getDouble(5);
                    double phaseDL = rs.getDouble(6);
                    double DR = rs.getDouble(7);
                    double phaseDR = rs.getDouble(8);
                    
                    
                    
//                    // Execute query and get result set
//                    rs = st.executeQuery("SELECT \"StiffnessAxialA[N/m]\"*0.000002, \"StiffnessAxialP[rad]\", \"StiffnessMomentXA[Nm/m]\"*0.000002, \"StiffnessMomentXP[rad]\" FROM \"A310SectionNM\" where TESTNAME='" + testName + "' and SECTION='LA3S4';");
//
//                    rs.next();

                    Complex strainUL = ComplexUtils.polar2Complex(UL, phaseUL);
                    Complex strainUR = ComplexUtils.polar2Complex(UR, phaseUR);
                    Complex strainDL = ComplexUtils.polar2Complex(DL, phaseDL);
                    Complex strainDR = ComplexUtils.polar2Complex(DR, phaseDR);
                    
                    Complex strainU = (strainUL.add(strainUR)).multiply(0.5);
                    Complex strainD = (strainDL.add(strainDR)).multiply(0.5);
                    Complex strainUD = strainU.subtract(strainD);
                    Complex neutralAxis = ((strainU.multiply(distance)).divide(strainUD)).add(slab);
                   

                // Insert the result into the table
                String insertQuery = "INSERT INTO ShearForceResultsC2FA3ew (TestName, ShearForce) VALUES ('" + testName + "', " + neutralAxis.getReal() + ")";
                st.executeUpdate(insertQuery);
                System.out.println("Record for TestName '" + testName + "' inserted into the table.");

                if (waveName.equals("Random")) {
                    random.add(i + 1, neutralAxis.getReal());
                } else if (waveName.equals("KMMH02")) {
                    kumamoto.add(i + 1, neutralAxis.getReal());
                } else if (waveName.equals("FKS020")) {
                    tohoku.add(i + 1, neutralAxis.getReal());
                } else if (waveName.startsWith("Kobe")) {
                    kobe.add(i + 1, neutralAxis.getReal());
                }

            }

          
            dataset.addSeries("Random", random.toArray());
            dataset.addSeries("tohoku", tohoku.toArray());
            dataset.addSeries("kumamoto", kumamoto.toArray());
            dataset.addSeries("kobe", kobe.toArray());
            // Create the chart
            JFreeChart chart = ChartFactory.createXYLineChart("Sf_LA3diJ", "Testname", "Shearforce (kN)",
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
            rangeAxis.setRange(0, 0.35); // Set the y-axis range
            rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
            plot.setOutlineStroke(new BasicStroke(2f)); // frame around the plot
            plot.setAxisOffset(RectangleInsets.ZERO_INSETS); // remove space between frame and axis.
            rangeAxis.setInverted(true);
            
            domainAxis.setLowerBound(0.1);
            domainAxis.setUpperBound(24.9);
            

            
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
            
            
            
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesPaint(0, Color.RED);
            plot.setRenderer(renderer);
            
            
            // insert legend to the plot
            LegendTitle legend = chart.getLegend(); // obtain legend box
            XYTitleAnnotation ta=new XYTitleAnnotation(0.95 ,0.95, legend, RectangleAnchor.BOTTOM_RIGHT);
            legend.setBorder(1, 1, 1, 1); // frame around legend
            plot.addAnnotation(ta);
            chart.removeLegend();
            
            

            // Export the chart as PNG
            int width = 1400;
            int height = 1000;
//            String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\sea01\\sf_C2FA3ew.png";
//            File chartFile = new File(filePath);
//            ChartUtils.saveChartAsPNG(chartFile, chart, width, height);

//              String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\sea02\\sf_LA3.svg";
//              JunChartUtil.svg(filePath, width, height, chart);

//            // Display the chart in a frame
            ChartFrame frame = new ChartFrame("Shear Force Results", chart);
            frame.setPreferredSize(new Dimension(1200, 800));
            frame.pack();
            frame.setVisible(true);

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A700NeutralAxisLA3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}