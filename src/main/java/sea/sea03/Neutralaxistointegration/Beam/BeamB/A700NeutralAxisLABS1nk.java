/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea03.Neutralaxistointegration.Beam.BeamB;

import sea.sea03.Neutralaxistointegration.Beam.Beam3.*;
import sea.sea03.Neutralaxistointegration.Beam.BeamA.*;
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
public class A700NeutralAxisLABS1nk {

    public static void main(String[] args) throws IOException {

        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";
            
            double distance = 0.222; // distance between inner web (Beam3 = 350 - 2*12)
            double slab = 0.11;
            double section = 0.177;
            double EIs = 12028223.57;
//            double EIeq = 42389172.29;
            



            // Connect to database
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();
            Statement st2 = con.createStatement();

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
            
                 // Create 'momentLA3' table if it doesn't exist
                st2.executeUpdate("DROP TABLE IF EXISTS EIBS1");
                String createTableQuery = "CREATE TABLE IF NOT EXISTS EIBS1 (TestName VARCHAR(20), EI DOUBLE)";
                st2.executeUpdate(createTableQuery);

            for (int i = 0; i < kasins.length; i++) {
                String testName = kasins[i].getTestName();  //D01Q01
                String waveName = kasins[i].getWaveName();  // Random

                // Execute query and get result set
                    ResultSet rs = st.executeQuery("SELECT \"Strain1A[με*s]\", \"Strain1P[rad]\", \"Strain2A[με*s]\", \"Strain2P[rad]\",  \"Strain3A[με*s]\", \"Strain3P[rad]\",  \"Strain4A[με*s]\", \"Strain4P[rad]\", FROM \"A310SectionNM\" where TESTNAME = '" + testName + "' and SECTION = 'LABS1'");
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

                    Complex strainUL = ComplexUtils.polar2Complex(UL, phaseUL);
                    Complex strainUR = ComplexUtils.polar2Complex(UR, phaseUR);
                    Complex strainDL = ComplexUtils.polar2Complex(DL, phaseDL);
                    Complex strainDR = ComplexUtils.polar2Complex(DR, phaseDR);
                    
                    Complex strainU = (strainUL.add(strainUR)).multiply(0.5);
                    Complex strainD = (strainDL.add(strainDR)).multiply(0.5);
                    Complex strainUD = strainU.subtract(strainD);
                    Complex neutralAxis = ((strainU.multiply(distance)).divide(strainUD));
                    
                    Complex phi1 = ((strainU.divide(neutralAxis))).multiply(1e-6);     //curvature
                    
                    
                    ResultSet rs2 = st.executeQuery("SELECT TESTNAME, CASE ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) WHEN 1 THEN 0.435 WHEN 2 THEN 0.955 WHEN 3 THEN 1.575 WHEN 4 THEN 2.195 WHEN 5 THEN 2.715 END AS NewColumn,"
                        + "\"AxialA[N*s]\", \"AxialP[rad]\", \"MomentXA[Nm*s]\", \"MomentXP[rad]\" "
                        + "FROM \"A310SectionNM\" WHERE TESTNAME = '" +testName+ "' AND SECTION LIKE 'LABS1'");
                    rs2.next();
                    
                    //  String testname=rs.getString(1);
                    double sectionNo = rs2.getDouble(2);
                    double axialamplitude = rs2.getDouble(3);
                    double axialphase = rs2.getDouble(4);
                    double momentamplitude = rs2.getDouble(5);
                    double momentphase = rs2.getDouble(6);
                    Complex axialMoment = ComplexUtils.polar2Complex(axialamplitude, axialphase);
                    Complex moment = ComplexUtils.polar2Complex(momentamplitude, momentphase);

                    Complex allMoment = moment.add((axialMoment).multiply(section));
                            

                    //Calculate EI
                    Complex EI = allMoment.divide(phi1);
                    double EI2 = (EI.getReal());
                    
                    double EIEIs = EI2/EIs; // EI/EIs
//                      double EIEIeq = EI2/EIeq; // EI/EIs
                              
//                    double phi2 = (phi1.getReal());                         //phi2
//                    
//                    System.out.println(neutralAxis.getReal());
                    

                    // Insert data into 'EI3' table
                    String insertQuery = "INSERT INTO EIBS1 (TestName, EI) VALUES ('" + testName + "', '" + EIEIs + "')";
                    st2.executeUpdate(insertQuery);
                    System.out.println("Record for TestName '" + testName + "' inserted into the table.");
                
//                System.out.println(strainU.getReal());
//                System.out.println(phi1);

                if (waveName.equals("Random")) {
                    random.add(i + 1, EIEIs);
                } else if (waveName.equals("KMMH02")) {
                    kumamoto.add(i + 1, EIEIs);
                } else if (waveName.equals("FKS020")) {
                    tohoku.add(i + 1, EIEIs);
                }

            }
            
            dataset.addSeries("Random", random.toArray());
            dataset.addSeries("tohoku", tohoku.toArray());
            dataset.addSeries("kumamoto", kumamoto.toArray());


            // Create the chart
            JFreeChart chart = ChartFactory.createXYLineChart("", "Test No.", "EI/EsIs",
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
            rangeAxis.setRange(0,4); // Set the y-axis range
            rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
            plot.setOutlineStroke(new BasicStroke(2f)); // frame around the plot
            plot.setAxisOffset(RectangleInsets.ZERO_INSETS); // remove space between frame and axis.
//            rangeAxis.setInverted(true);
            
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
            XYTitleAnnotation ta=new XYTitleAnnotation(0.95 ,0.05, legend, RectangleAnchor.BOTTOM_RIGHT);
            legend.setBorder(1, 1, 1, 1); // frame around legend
            plot.addAnnotation(ta);
            chart.removeLegend();
            

            // Export the chart as PNG
            int width = 650;
            int height = 300;
//            String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\sea01\\sf_C2FA3ew.png";
//            File chartFile = new File(filePath);
//            ChartUtils.saveChartAsPNG(chartFile, chart, width, height);

              String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\flexuralstiffness\\fs_LABS1.svg";
              JunChartUtil.svg(filePath, width, height, chart);

//            // Display the chart in a frame
            ChartFrame frame = new ChartFrame("Phi", chart);
            frame.setPreferredSize(new Dimension(1200, 800));
            frame.pack();
            frame.setVisible(true);

            con.close();
            

        } catch (SQLException ex) {
            Logger.getLogger(A700NeutralAxisLABS1nk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
