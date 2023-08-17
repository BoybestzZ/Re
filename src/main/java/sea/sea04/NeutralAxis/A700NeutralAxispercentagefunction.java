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
import jun.res23.ed.util.ColumnInfo;
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
import static sea.sea04.NeutralAxis.A700NeutralAxiswithtimehistoryanalysisfunction.createNeutralAxisPercentage;

/**
 * modified by Iyama. Use XYdataset. THe xaxisl will be number.
 *
 *
 * @author 75496
 */
public class A700NeutralAxispercentagefunction {
            public static void main(String[] args) throws IOException, SQLException{
           createNeutralAxisPercentage(EdefenseInfo.Beam3, 0);
           createNeutralAxisPercentage(EdefenseInfo.Beam3, 1);
           createNeutralAxisPercentage(EdefenseInfo.Beam3, 2);
           createNeutralAxisPercentage(EdefenseInfo.Beam3, 3);
           createNeutralAxisPercentage(EdefenseInfo.Beam3, 4);
           
           createNeutralAxisPercentage(EdefenseInfo.Beam4, 0);
           createNeutralAxisPercentage(EdefenseInfo.Beam4, 1);
           createNeutralAxisPercentage(EdefenseInfo.Beam4, 2);
           createNeutralAxisPercentage(EdefenseInfo.Beam4, 3);
           createNeutralAxisPercentage(EdefenseInfo.Beam4, 4);
           
           createNeutralAxisPercentage(EdefenseInfo.BeamA, 0);
           createNeutralAxisPercentage(EdefenseInfo.BeamA, 1);
           createNeutralAxisPercentage(EdefenseInfo.BeamA, 2);
           createNeutralAxisPercentage(EdefenseInfo.BeamA, 3);
           createNeutralAxisPercentage(EdefenseInfo.BeamA,4);
           
           createNeutralAxisPercentage(EdefenseInfo.BeamB, 0);
           createNeutralAxisPercentage(EdefenseInfo.BeamB, 1);
           createNeutralAxisPercentage(EdefenseInfo.BeamB, 2);
           createNeutralAxisPercentage(EdefenseInfo.BeamB, 3);
           createNeutralAxisPercentage(EdefenseInfo.BeamB, 4);
//           createShearForceBeamTable(EdefenseInfo.BeamB);

    }

    public static void createNeutralAxisPercentage(BeamInfo beamInfo, int section) throws IOException, SQLException {

        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";
            
//            double distance = 0.326; // distance between inner web (Beam3 = 350 - 2*12)
//            double slab = 0.11;
//            double section = 0.23;
//            double EIs = 33442650.81;           //unit: Nm2
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
            
            XYSeries randomp = new XYSeries("random+");
            XYSeries kumamotop = new XYSeries("kumamoto+");
            XYSeries tohokup = new XYSeries("tohoku+");
            
            XYSeries randomn = new XYSeries("random-");
            XYSeries kumamoton = new XYSeries("kumamoto-");
            XYSeries tohokun = new XYSeries("tohoku-");
            
            String sectionNB = beamInfo.getSections()[section].getName();
            
//                 // Create 'momentLA3' table if it doesn't exist
//                st2.executeUpdate("DROP TABLE IF EXISTS EI3S1");
//                String createTableQuery = "CREATE TABLE IF NOT EXISTS EI3S1 (TestName VARCHAR(20), EI DOUBLE)";
//                st2.executeUpdate(createTableQuery);

            for (int i = 0; i < kasins.length; i++) {
                String testName = kasins[i].getTestName();  //D01Q01
                String waveName = kasins[i].getWaveName();  // Random

                // Execute query and get result set
                    ResultSet rs = st.executeQuery("SELECT TESTNAME, MAX(neutralaxis) AS neutralaxis, MAX(POSITIVE) AS POSITIVE, MAX(NEGATIVE) AS NEGATIVE FROM NeuAxis"+sectionNB+" WHERE TESTNAME = '" +testName+ "' GROUP BY TESTNAME;");
                    rs.next();
                    
                    // get results
                    double shearforce = rs.getDouble(2);
                    double positive = rs.getDouble(3);
                    double negative = rs.getDouble(4);
                    
//                    double shearpositive = positive/shearforce;
//                    double shearnegative = negative/shearforce;

                    double shearpositive = shearforce/positive;
                    double shearnegative = shearforce/negative;
                    
                    


//                            
//
//                    //Calculate EI
//                    Complex EI = allMoment.divide(phi1);
//                    double EI2 = (EI.getReal());
//                    
//                    double EIEIs = EI2/EIs; // EI/EIs
////                      double EIEIeq = EI2/EIeq; // EI/EIs
//                              
////                    double phi2 = (phi1.getReal());                         //phi2
////                    
////                    System.out.println(neutralAxis.getReal());
//                    
//
//                    // Insert data into 'EI3' table
//                    String insertQuery = "INSERT INTO EI3S1 (TestName, EI) VALUES ('" + testName + "', '" + EIEIs + "')";
//                    st2.executeUpdate(insertQuery);
//                    System.out.println("Record for TestName '" + testName + "' inserted into the table.");
                
//                System.out.println(strainU.getReal());
//                System.out.println(phi1);

                if (waveName.equals("Random")) {
                    randomp.add(i + 1, shearpositive);
                } else if (waveName.equals("KMMH02")) {
                    kumamotop.add(i + 1, shearpositive);
                } else if (waveName.equals("FKS020")) {
                    tohokup.add(i + 1, shearpositive);
                }
                
                if (waveName.equals("Random")) {
                    randomn.add(i + 1, shearnegative);
                } else if (waveName.equals("KMMH02")) {
                    kumamoton.add(i + 1, shearnegative);
                } else if (waveName.equals("FKS020")) {
                    tohokun.add(i + 1, shearnegative);
                }
                

            }
            
            dataset.addSeries("Random+", randomp.toArray());
            dataset.addSeries("tohoku+", tohokup.toArray());
            dataset.addSeries("kumamoto+", kumamotop.toArray());
            
            dataset.addSeries("Random-", randomn.toArray());
            dataset.addSeries("tohoku-", tohokun.toArray());
            dataset.addSeries("kumamoto-", kumamoton.toArray());


            // Create the chart
            JFreeChart chart = ChartFactory.createXYLineChart("", "Test No.", "NeuAxis(F/+ or F/-)",
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
            rangeAxis.setRange(-4,4); // Set the y-axis range
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
            XYTitleAnnotation ta=new XYTitleAnnotation(1 ,0.05, legend, RectangleAnchor.BOTTOM_RIGHT);
            legend.setBorder(1, 1, 1, 1); // frame around legend
            plot.addAnnotation(ta);
            chart.removeLegend();
            

            // Export the chart as PNG
            int width = 650;
            int height = 300;
//            String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\sea01\\sf_C2FA3ew.png";
//            File chartFile = new File(filePath);
//            ChartUtils.saveChartAsPNG(chartFile, chart, width, height);

              String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\neutralaxis(f)\\na%_"+sectionNB+".svg";
              JunChartUtil.svg(filePath, width, height, chart);

//            // Display the chart in a frame
            ChartFrame frame = new ChartFrame("Phi", chart);
            frame.setPreferredSize(new Dimension(1200, 800));
            frame.pack();
            frame.setVisible(true);

            con.close();
            

        } catch (SQLException ex) {
            Logger.getLogger(A700NeutralAxispercentagefunction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
