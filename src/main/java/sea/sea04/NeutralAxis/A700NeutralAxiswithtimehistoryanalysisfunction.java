/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea04.NeutralAxis;

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
import jun.res23.ed.util.BeamInfo;
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
public class A700NeutralAxiswithtimehistoryanalysisfunction {

    public static void main(String[] args) throws IOException, SQLException{
        
        double distance1 = 0.326; // distance between inner web (Beam3 = 350 - 2*12)
        double distance2 = 0.222;  // distance between inner web (BeamB = 244 - 2*11)
        double slab = 0.11;
        
           createNeutralAxisPercentage(EdefenseInfo.Beam3, distance1, 0);
           createNeutralAxisPercentage(EdefenseInfo.Beam3, distance1, 1);
           createNeutralAxisPercentage(EdefenseInfo.Beam3, distance1, 2);
           createNeutralAxisPercentage(EdefenseInfo.Beam3, distance1, 3);
           createNeutralAxisPercentage(EdefenseInfo.Beam3, distance1, 4);
           
           createNeutralAxisPercentage(EdefenseInfo.Beam4, distance1, 0);
           createNeutralAxisPercentage(EdefenseInfo.Beam4, distance1, 1);
           createNeutralAxisPercentage(EdefenseInfo.Beam4, distance1, 2);
           createNeutralAxisPercentage(EdefenseInfo.Beam4, distance1, 3);
           createNeutralAxisPercentage(EdefenseInfo.Beam4, distance1, 4);
           
           createNeutralAxisPercentage(EdefenseInfo.BeamA, distance1, 0);
           createNeutralAxisPercentage(EdefenseInfo.BeamA, distance1, 1);
           createNeutralAxisPercentage(EdefenseInfo.BeamA, distance1, 2);
           createNeutralAxisPercentage(EdefenseInfo.BeamA, distance1, 3);
           createNeutralAxisPercentage(EdefenseInfo.BeamA, distance1, 4);
           
           createNeutralAxisPercentage(EdefenseInfo.BeamB, distance2, 0);
           createNeutralAxisPercentage(EdefenseInfo.BeamB, distance2, 1);
           createNeutralAxisPercentage(EdefenseInfo.BeamB, distance2, 2);
           createNeutralAxisPercentage(EdefenseInfo.BeamB, distance2, 3);
           createNeutralAxisPercentage(EdefenseInfo.BeamB, distance2, 4);

    }

    public static void createNeutralAxisPercentage(BeamInfo beamInfo, double distance, int section) throws IOException, SQLException {

        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";
            String ed06dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/res22ed06v230815J";

            double slab = 0.11;


             // Connect to database
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();
            Connection con08 = DriverManager.getConnection(ed06dburl, "junapp", "");
            Statement st08 = con08.createStatement();
            Statement st09 = con.createStatement();

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
            
            DefaultXYDataset dataset2 = new DefaultXYDataset();
            XYSeries randomp = new XYSeries("random+");
            XYSeries kumamotop = new XYSeries("kumamoto+");
            XYSeries tohokup = new XYSeries("tohoku+");

            

            XYSeries randomn = new XYSeries("random-");
            XYSeries kumamoton = new XYSeries("kumamoto-");
            XYSeries tohokun = new XYSeries("tohoku-");
            
            String sectionNB = beamInfo.getSections()[section].getName();

            
            // Create table to store results if it doesn't exist
            st.executeUpdate("DROP TABLE IF EXISTS NeuAxis"+sectionNB+"");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS NeuAxis"+sectionNB+" (TestName VARCHAR(20), NeutralAxis DOUBLE)");


            for (int i = 0; i < kasins.length; i++) {
                String testName = kasins[i].getTestName();  //D01Q01
                String waveName = kasins[i].getWaveName();  // Random

                // Execute query and get result set
                    ResultSet rs = st.executeQuery("SELECT \"Strain1A[με*s]\", \"Strain1P[rad]\", \"Strain2A[με*s]\", \"Strain2P[rad]\",  \"Strain3A[με*s]\", \"Strain3P[rad]\",  \"Strain4A[με*s]\", \"Strain4P[rad]\", FROM \"A310SectionNM\" where TESTNAME = '" + testName + "' and SECTION = '"+sectionNB+"'");
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
                    Complex neutralAxis = ((strainU.multiply(distance)).divide(strainUD)).add(slab);
                
                // Insert the result into the table
                String insertQuery = "INSERT INTO NeuAxis"+sectionNB+" (TestName, NeutralAxis) VALUES ('" + testName + "', " + neutralAxis.getReal() + ")";
                st.executeUpdate(insertQuery);
                System.out.println("Record for TestName '" + testName + "' inserted into the table.");

                if (waveName.equals("Random")) {
                    random.add(i + 1, neutralAxis.getReal());
                } else if (waveName.equals("KMMH02")) {
                    kumamoto.add(i + 1, neutralAxis.getReal());
                } else if (waveName.equals("FKS020")) {
                    tohoku.add(i + 1, neutralAxis.getReal());
                }

            }
            
            dataset.addSeries("Random", random.toArray());
            dataset.addSeries("tohoku", tohoku.toArray());
            dataset.addSeries("kumamoto", kumamoto.toArray());

             EdefenseKasinInfo[] kasins2 = EdefenseInfo.alltests;
            
            // Create table to store results if it doesn't exist
            st.executeUpdate("Alter table NeuAxis"+sectionNB+" add positive double;");
            
            for (int i = 0; i < kasins2.length; i++) {
                String testName = kasins2[i].getTestName();  //D01Q01
                String waveName = kasins2[i].getWaveName();  // Random
                
                ResultSet rs08 = st08.executeQuery("SELECT \"NeutralAxis[mm]\"*0.001 FROM \"T232NeutralAxis\" where TESTNAME = '" + testName + "' and \"SECTION\" = '"+sectionNB+"' and \"DirectionPositive\" = TRUE;");
                rs08.next();
                    

                // get results
                double shearforcestiffnesspositive = rs08.getDouble(1);
                
                String insertQuery = "INSERT INTO NeuAxis"+sectionNB+" (TestName, positive) VALUES ('" + testName + "'," + shearforcestiffnesspositive + ")";
                st.executeUpdate(insertQuery);
                System.out.println("Record for TestName '" + testName + "' inserted into the table.");
                
                if (waveName.equals("Random")) {
                    randomp.add(i + 1, shearforcestiffnesspositive);
                } else if (waveName.equals("KMMH02")) {
                    kumamotop.add(i + 1, shearforcestiffnesspositive);
                } else if (waveName.equals("FKS020")) {
                    tohokup.add(i + 1, shearforcestiffnesspositive);
                }

            }
                dataset.addSeries("Random+", randomp.toArray());
                dataset.addSeries("tohoku+", tohokup.toArray());
                dataset.addSeries("kumamoto+", kumamotop.toArray());

                
           // Create table to store results if it doesn't exist
            st.executeUpdate("Alter table NeuAxis"+sectionNB+" add negative double;");
                
                EdefenseKasinInfo[] kasins3 = EdefenseInfo.alltests;
            
            for (int i = 0; i < kasins3.length; i++) {
                String testName = kasins3[i].getTestName();  //D01Q01
                String waveName = kasins3[i].getWaveName();  // Random
                
                ResultSet rs08 = st08.executeQuery("SELECT \"NeutralAxis[mm]\"*0.001 FROM \"T232NeutralAxis\" where TESTNAME = '" + testName + "' and \"SECTION\" = '"+sectionNB+"' and \"DirectionPositive\" = FALSE;");
                rs08.next();
                
                                // get results
                double shearforcestiffnessnegative = rs08.getDouble(1);
                
                String insertQuery = "INSERT INTO NeuAxis"+sectionNB+" (TestName, negative) VALUES ('" + testName + "'," + shearforcestiffnessnegative + ")";
                st.executeUpdate(insertQuery);
                System.out.println("Record for TestName '" + testName + "' inserted into the table.");


                if (waveName.equals("Random")) {
                    randomn.add(i + 1, shearforcestiffnessnegative);
                } else if (waveName.equals("KMMH02")) {
                    kumamoton.add(i + 1, shearforcestiffnessnegative);
                } else if (waveName.equals("FKS020")) {
                    tohokun.add(i + 1, shearforcestiffnessnegative);
                } 
            }
                dataset.addSeries("Random-", randomn.toArray());
                dataset.addSeries("tohoku-", tohokun.toArray());
                dataset.addSeries("kumamoto-", kumamoton.toArray());

                
                
                

            // Create the chart
            JFreeChart chart = ChartFactory.createXYLineChart("", "Test No.", "Neutral axis location (m)",
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
            XYTitleAnnotation ta=new XYTitleAnnotation(1 ,0.95, legend, RectangleAnchor.BOTTOM_RIGHT);
            legend.setBorder(1, 1, 1, 1); // frame around legend
            plot.addAnnotation(ta);
            chart.removeLegend();
            

            // Export the chart as PNG
            int width = 650;
            int height = 600;
//            String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\sea01\\sf_C2FA3ew.png";
//            File chartFile = new File(filePath);
//            ChartUtils.saveChartAsPNG(chartFile, chart, width, height);

              String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\neutralaxis(f)\\allna_"+sectionNB+".svg";
              JunChartUtil.svg(filePath, width, height, chart);

//            // Display the chart in a frame
            ChartFrame frame = new ChartFrame("Shear Force Results", chart);
            frame.setPreferredSize(new Dimension(1200, 800));
            frame.pack();
            frame.setVisible(true);

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A700NeutralAxiswithtimehistoryanalysisfunction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
