/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea06.Column3F;

import sea.sea04.NeutralAxis.*;
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
import static sea.sea04.Columnshearforce.A300ColumnShearwithtimehistoryanalysisfunction.createShearForceColumnPercentage;


/**
 * modified by Iyama. Use XYdataset. THe xaxisl will be number.
 *
 *
 * @author 75496
 */
public class A800ColumnStoryDriftwithtimehistoryanalysisfunction3F {

    public static void main(String[] args) throws IOException, SQLException{
        
           createStoryDriftPercentage(EdefenseInfo.Column3FA3, "ew");
//           createStoryDriftPercentage(EdefenseInfo.Column3FA4, "ew");
//           createStoryDriftPercentage(EdefenseInfo.Column3FB3, "ew");
//           createStoryDriftPercentage(EdefenseInfo.Column3FB4, "ew");

           createStoryDriftPercentage(EdefenseInfo.Column3FA3, "ns");
//           createStoryDriftPercentage(EdefenseInfo.Column3FA4, "ns");
//           createStoryDriftPercentage(EdefenseInfo.Column3FB3, "ns");
//           createStoryDriftPercentage(EdefenseInfo.Column3FB4, "ns");
           
           


    }

    public static void createStoryDriftPercentage(ColumnInfo columnInfo, String direction) throws IOException, SQLException {

        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";
            String ed08dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/res22ed08v230815J";

            double slab = 0.11;


             // Connect to database
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();
            Connection con08 = DriverManager.getConnection(ed08dburl, "junapp", "");
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
            

            String columnName = columnInfo.getName();
            
            // Create table to store results if it doesn't exist
            st.executeUpdate("DROP TABLE IF EXISTS StoryDrift"+columnInfo.getName()+direction+"");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS StoryDrift"+columnInfo.getName()+direction+" (TestName VARCHAR(20), StoryDrift3F DOUBLE)");


            for (int i = 0; i < kasins.length; i++) {
                String testName = kasins[i].getTestName();  //D01Q01
                String waveName = kasins[i].getWaveName();  // Random

                // Execute query and get result set
                    String topSection = columnInfo.getSections()[2].getName()+direction;  // CS2FA3Tns
                    ResultSet rs = st.executeQuery("SELECT \"StoryDrift2F[mm]\" FROM \"A310SectionNM\" where TESTNAME = '" + testName + "' and SECTION = '"+topSection+"'");
                    rs.next();

                    // get results
                    double storyDrift3F = rs.getDouble(1);
                
                // Insert the result into the table
                String insertQuery = "INSERT INTO StoryDrift"+columnInfo.getName()+direction+" (TestName, StoryDrift3F) VALUES ('" + testName + "', " + storyDrift3F + ")";
                st.executeUpdate(insertQuery);
                System.out.println("Record for TestName '" + testName + "' inserted into the table.");
                
                if (waveName.equals("Random")) {
                    random.add(i + 1, storyDrift3F);
                } else if (waveName.equals("KMMH02")) {
                    random.add(i + 1, storyDrift3F);
                } else if (waveName.equals("FKS020")) {
                    random.add(i + 1, storyDrift3F);
                }
            }
            
            dataset.addSeries("Random", random.toArray());
            dataset.addSeries("tohoku", tohoku.toArray());
            dataset.addSeries("kumamoto", kumamoto.toArray());

             EdefenseKasinInfo[] kasins2 = EdefenseInfo.alltests;
            
            // Create table to store results if it doesn't exist
            st.executeUpdate("Alter table StoryDrift"+columnInfo.getName()+direction+" add positive double;");
            
            for (int i = 0; i < kasins2.length; i++) {
                String testName = kasins2[i].getTestName();  //D01Q01
                String waveName = kasins2[i].getWaveName();  // Random
                
                ResultSet rs08 = st08.executeQuery("SELECT \"3rdStoryDrift[mm]\" FROM \"B230ElementShearLocalStiffness\" where TESTNAME = '" + testName + "' and \"ElementName\" = '"+columnName+"' and \"PositiveDirection\" = TRUE and Direction = '"+direction+"';");
                rs08.next();
                    

                // get results
                double storyDrift3Fpostive = rs08.getDouble(1);
                
                String insertQuery = "UPDATE StoryDrift"+columnInfo.getName()+direction+" SET POSITIVE="+storyDrift3Fpostive+" where TESTNAME='"+testName+"'"; //, positive) VALUES ('" + testName + "'," + shearforcestiffnesspositive + ")";
                st.executeUpdate(insertQuery);
                System.out.println("Record for TestName '" + testName + "' inserted into the table.");
                
                if (waveName.equals("Random")) {
                    randomp.add(i + 1, storyDrift3Fpostive);
                } else if (waveName.equals("KMMH02")) {
                    kumamotop.add(i + 1, storyDrift3Fpostive);
                } else if (waveName.equals("FKS020")) {
                    tohokup.add(i + 1, storyDrift3Fpostive);
                }

            }
                dataset.addSeries("Random+", randomp.toArray());
                dataset.addSeries("tohoku+", tohokup.toArray());
                dataset.addSeries("kumamoto+", kumamotop.toArray());

                
           // Create table to store results if it doesn't exist
            st.executeUpdate("Alter table StoryDrift"+columnInfo.getName()+direction+" add negative double;");
                
                EdefenseKasinInfo[] kasins3 = EdefenseInfo.alltests;
            
            for (int i = 0; i < kasins3.length; i++) {
                String testName = kasins3[i].getTestName();  //D01Q01
                String waveName = kasins3[i].getWaveName();  // Random
                
                ResultSet rs08 = st08.executeQuery("SELECT \"3rdStoryDrift[mm]\" FROM \"B230ElementShearLocalStiffness\" where TESTNAME = '" + testName + "' and \"ElementName\" = '"+columnName+"' and \"PositiveDirection\" = FALSE and Direction = '"+direction+"';");
                rs08.next();
                
                                // get results
                double storyDrift3Fnegative = rs08.getDouble(1);
                
                String insertQuery = "UPDATE StoryDrift"+columnInfo.getName()+direction+" SET NEGATIVE="+storyDrift3Fnegative+" where TESTNAME='"+testName+"'"; //, positive) VALUES ('" + testName + "'," + shearforcestiffnesspositive + ")";
                st.executeUpdate(insertQuery);
                System.out.println("Record for TestName '" + testName + "' inserted into the table.");


                if (waveName.equals("Random")) {
                    randomn.add(i + 1, storyDrift3Fnegative);
                } else if (waveName.equals("KMMH02")) {
                    kumamoton.add(i + 1, storyDrift3Fnegative);
                } else if (waveName.equals("FKS020")) {
                    tohokun.add(i + 1, storyDrift3Fnegative);
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
            rangeAxis.setRange(-20, 20); // Set the y-axis range
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
            XYTitleAnnotation ta=new XYTitleAnnotation(1 ,0.95, legend, RectangleAnchor.BOTTOM_RIGHT);
            legend.setBorder(1, 1, 1, 1); // frame around legend
            plot.addAnnotation(ta);
            chart.removeLegend();
            

            // Export the chart as PNG
            int width = 650;
            int height = 500;
//            String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\sea01\\sf_C2FA3ew.png";
//            File chartFile = new File(filePath);
//            ChartUtils.saveChartAsPNG(chartFile, chart, width, height);

              String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\storydriftcolumn(f)\\allsd_"+columnInfo.getName()+direction+".svg";
              JunChartUtil.svg(filePath, width, height, chart);

//            // Display the chart in a frame
            ChartFrame frame = new ChartFrame("Shear Force Results", chart);
            frame.setPreferredSize(new Dimension(1200, 800));
            frame.pack();
            frame.setVisible(true);

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A800ColumnStoryDriftwithtimehistoryanalysisfunction3F.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
