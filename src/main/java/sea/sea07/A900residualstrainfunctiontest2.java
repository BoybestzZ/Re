
/**
 *
 * @author 75496
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea07;

import sea.sea04.InflectionPoint.*;
import sea.sea01.Inflectionpoint.*;
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
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import static jun.res23.ed.ed14分析UF.A310SectionNM.outputTable;
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
public class A900residualstrainfunctiontest2 {

    public static void main(String[] args) throws IOException, SQLException {
        createResidualStrain("a0101");
    }

    public static void createResidualStrain(String table) throws IOException, SQLException {
        try {
            // Define the database URL for H2
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";

            String ed06dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/res22ed06";

            
            // Connect to database
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();
            Connection con08 = DriverManager.getConnection(ed06dburl, "junapp", "");
            Statement st08 = con08.createStatement();
            Statement st09 = con.createStatement();

            // Create a table to store results if it doesn't exist
            st.executeUpdate("DROP TABLE IF EXISTS ResidualStrain" + table + "");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS ResidualStrain" + table + " (TestName VARCHAR(20), ResidualStrain DOUBLE)");

            // Define an array of tests (kasins) and datasets
            EdefenseKasinInfo[] kasins = EdefenseInfo.alltests;
            DefaultXYDataset dataset = new DefaultXYDataset();
            XYSeries random = new XYSeries("random");
            XYSeries kumamoto = new XYSeries("kumamoto");
            XYSeries tohoku = new XYSeries("tohoku");
            XYSeries kobe = new XYSeries("kobe");

            for (int i = 0; i < kasins.length; i++) {
                String testName = kasins[i].getTestName(); // D01Q01
                String waveName = kasins[i].getWaveName(); // Random

                // Execute a SQL query to retrieve data
                ResultSet rs = st.executeQuery("SELECT TESTNAME, AVG(\"Strain[με]\") FROM \"T220TimeHistoryStrain\".\"a01/01\" WHERE TESTNAME = '" + testName + "t' ORDER BY \"TotalTime[s]\" DESC LIMIT 10");
                rs.next();

                // Get the result
                double residualStrain = rs.getDouble(2);

                // Insert the result into the table
                String insertQuery = "INSERT INTO ResidualStrain" + table + " (TestName, ResidualStrain) VALUES ('" + testName + "', " + residualStrain + ")";
                st.executeUpdate(insertQuery);
                System.out.println("Record for TestName '" + testName + "' inserted into the table.");

                // Add data to the dataset based on waveName
                if (waveName.equals("Random")) {
                    random.add(i + 1, residualStrain);
                } else if (waveName.equals("KMMH02")) {
                    kumamoto.add(i + 1, residualStrain);
                } else if (waveName.equals("FKS020")) {
                    tohoku.add(i + 1, residualStrain);
                } else if (waveName.startsWith("Kobe")) {
                    kobe.add(i + 1, residualStrain);
                }
            }

            // Add series to the dataset
            dataset.addSeries("Random", random.toArray());
            dataset.addSeries("tohoku", tohoku.toArray());
            dataset.addSeries("kumamoto", kumamoto.toArray());
            dataset.addSeries("kobe", kobe.toArray());

            // Continue with any additional code as needed

//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

//            
//             EdefenseKasinInfo[] kasins2 = EdefenseInfo.alltests;
//            
//            // Create table to store results if it doesn't exist
//            st.executeUpdate("Alter table InflPoi"+beamName+" add positive double;");
//            
//            for (int i = 0; i < kasins2.length; i++) {
//                String testName = kasins2[i].getTestName();  //D01Q01
//                String waveName = kasins2[i].getWaveName();  // Random
//                
//                ResultSet rs08 = st08.executeQuery("SELECT \"InflectionPoint[m]\" FROM \"T122BeamInflectionPoint\" where TESTNAME = '" + testName + "t' and \"BEAMNAME\" = '"+beamName+"' and \"DIRECTION\" = 'POSITIVE';");
//                rs08.next();
//                    
//
//                // get results
//                double shearforcestiffnesspositive = rs08.getDouble(1);
//                
//                String insertQuery = "INSERT INTO InflPoi"+beamName+" (TestName, positive) VALUES ('" + testName + "'," + shearforcestiffnesspositive + ")";
//                st.executeUpdate(insertQuery);
//                System.out.println("Record for TestName '" + testName + "' inserted into the table.");
//                
//                if (waveName.equals("Random")) {
//                    randomp.add(i + 1, shearforcestiffnesspositive);
//                } else if (waveName.equals("KMMH02")) {
//                    kumamotop.add(i + 1, shearforcestiffnesspositive);
//                } else if (waveName.equals("FKS020")) {
//                    tohokup.add(i + 1, shearforcestiffnesspositive);
//                } else if (waveName.startsWith("Kobe")) {
//                    kobep.add(i + 1, shearforcestiffnesspositive);
//                }
//
//            }
//                dataset.addSeries("Random+", randomp.toArray());
//                dataset.addSeries("tohoku+", tohokup.toArray());
//                dataset.addSeries("kumamoto+", kumamotop.toArray());
////                dataset.addSeries("kobe+", kobep.toArray());
//                
//                
//            // Create table to store results if it doesn't exist
//            st.executeUpdate("Alter table InflPoi"+beamName+" add negative double;");
//                
//                EdefenseKasinInfo[] kasins3 = EdefenseInfo.alltests;
//            
//            for (int i = 0; i < kasins3.length; i++) {
//                String testName = kasins3[i].getTestName();  //D01Q01
//                String waveName = kasins3[i].getWaveName();  // Random
//                
//                ResultSet rs08 = st08.executeQuery("SELECT \"InflectionPoint[m]\" FROM \"T122BeamInflectionPoint\" where TESTNAME = '" + testName + "t' and \"BEAMNAME\" = '"+beamName+"' and \"DIRECTION\" = 'NEGATIVE';");
//                rs08.next();
//                    
//
//                // get results
//                double shearforcestiffnessnegative = rs08.getDouble(1);
//                
//                String insertQuery = "INSERT INTO InflPoi"+beamName+" (TestName, negative) VALUES ('" + testName + "'," + shearforcestiffnessnegative + ")";
//                st.executeUpdate(insertQuery);
//                System.out.println("Record for TestName '" + testName + "' inserted into the table.");
//
//
//                if (waveName.equals("Random")) {
//                    randomn.add(i + 1, shearforcestiffnessnegative);
//                } else if (waveName.equals("KMMH02")) {
//                    kumamoton.add(i + 1, shearforcestiffnessnegative);
//                } else if (waveName.equals("FKS020")) {
//                    tohokun.add(i + 1, shearforcestiffnessnegative);
//                } else if (waveName.startsWith("Kobe")) {
//                    koben.add(i + 1, shearforcestiffnessnegative);
//                }
//
//            }
//                dataset.addSeries("Random-", randomn.toArray());
//                dataset.addSeries("tohoku-", tohokun.toArray());
//                dataset.addSeries("kumamoto-", kumamoton.toArray());
//                dataset.addSeries("kobetime-", koben.toArray());
                
                
                
                
            
            
            
            
            // Create the chart
            JFreeChart chart = ChartFactory.createXYLineChart("", "Test No.", "Residual Strain (m)",
                    dataset, PlotOrientation.VERTICAL, true, true, false);

            // Customize the chart
            XYPlot plot = chart.getXYPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.BLACK);
            plot.setDomainGridlinesVisible(true);
            plot.setDomainGridlinePaint(Color.BLACK);
           
            NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
            domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont((int) 12f));
            domainAxis.setVerticalTickLabels(true);
            domainAxis.setLowerBound(0.1);
            domainAxis.setUpperBound(24.9);
            plot.setOutlinePaint(Color.BLACK);
            plot.setOutlineStroke(new BasicStroke(2f)); // frame around the plot
            plot.setAxisOffset(RectangleInsets.ZERO_INSETS); // remove space between frame and axis.

//            double lowerBound = 0.1; // Set the lower bound for the x-axis
//            double upperBound = kasins.length + 0.9; // Set the upper bound for the x-axis
//
//            domainAxis.setLowerBound(lowerBound);
//            domainAxis.setUpperBound(upperBound);
            
            // Prepare the mapping of test names to labels dynamically
            String[] testNames = new String[kasins.length];
            double[] testValues = new double[kasins.length];

            for (int i = 0; i < kasins.length; i++) {
                testNames[i] = kasins[i].getTestName() + kasins[i].getWaveName();
                testValues[i] = i + 1.0;
            }

            // Create the ChoiceFormat
            ChoiceFormat formatter = new ChoiceFormat(testValues, testNames);


//            NumberFormat formatter=new ChoiceFormat(
//                    new double[] {1.0,2.0,3.0}, 
//                        new String[]{"D01Q01random", "D02Q02kumamoto","D03Q03tohoku"});


            domainAxis.setNumberFormatOverride(formatter);
            
            
            
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setRange(1, 3); // Set the y-axis range
            rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());

            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesPaint(0, Color.RED);
            plot.setRenderer(renderer);
            
            // insert legend to the plot
            LegendTitle legend = chart.getLegend(); // obtain legend box
            XYTitleAnnotation ta=new XYTitleAnnotation(1.00 ,0.05, legend, RectangleAnchor.BOTTOM_RIGHT);
            legend.setBorder(1, 1, 1, 1); // frame around legend
            plot.addAnnotation(ta);
            chart.removeLegend();

            // Export the chart as PNG
            int width = 650;
            int height = 300;
//            String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\Inflectionpoint\\ip_LA3.png";
//            File chartFile = new File(filePath);
//            ChartUtils.saveChartAsPNG(chartFile, chart, width, height);

              String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\residualstrain\\residualstrain_"+table+".svg";
              JunChartUtil.svg(filePath, width, height, chart);


            // Display the chart in a frame
            ChartFrame frame = new ChartFrame("Inflection Point", chart);
            frame.setPreferredSize(new Dimension(1200, 800));
            frame.pack();
            frame.setVisible(true);

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A900residualstrainfunctiontest2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
    




//                // Execute query and get result set
//                    ResultSet rs = st.executeQuery("SELECT \"INFLECTIONPOINT\" FROM \"INFLECTIONPOINTDATA\" where TESTNAME='" + testName + "' and BEAMNAME='Beam3';");
//                    rs.next();
