/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea01.columnbeam.oldcolumnNS.columnNSJ;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;

/**
 * modified by Iyama. Use XYdataset. THe xaxisl will be number.
 *
 *
 * @author 75496
 */
public class A200BeamShearC2FA4nsJ {

    public static void main(String[] args) throws IOException {

        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";

            double distance = 1.15; // distance between Section 2 and Section 4;

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
            st.executeUpdate("CREATE TABLE IF NOT EXISTS ShearForceResultsC2FA4nsJ (TestName VARCHAR(20), ShearForce DOUBLE)");

            for (int i = 0; i < kasins.length; i++) {
                String testName = kasins[i].getTestName();  //D01Q01
                String waveName = kasins[i].getWaveName();  // Random

                // Execute query and get result set
                ResultSet rs = st.executeQuery("SELECT \"StiffnessMomentXA[Nm/m]\"*0.000002, \"StiffnessMomentXP[rad]\" FROM \"A310SectionNM\" where testname='" + testName + "' and section like 'CS2FA4Bns'");

                rs.next(); // goto the 1st line
                // get results
                double amplitudeS2 = rs.getDouble(1);
                double phaseS2 = rs.getDouble(2);

                // Execute query and get result set
                rs = st.executeQuery("SELECT \"StiffnessMomentXA[Nm/m]\"*0.000002, \"StiffnessMomentXP[rad]\" FROM \"A310SectionNM\" where testname='" + testName + "' and section like 'CS2FA4Tns'");

                rs.next();
                double amplitudeS4 = rs.getDouble(1);
                double phaseS4 = rs.getDouble(2);

                // if you want to consider phase
                Complex momentS2 = ComplexUtils.polar2Complex(amplitudeS2, phaseS2);
                Complex momentS4 = ComplexUtils.polar2Complex(amplitudeS4, phaseS4);
                Complex shearForceComplex = (momentS2.subtract(momentS4)).divide(distance);

                // Insert the result into the table
                String insertQuery = "INSERT INTO ShearForceResultsC2FA4nsJ (TestName, ShearForce) VALUES ('" + testName + "', " + shearForceComplex.getReal() + ")";
                st.executeUpdate(insertQuery);
                System.out.println("Record for TestName '" + testName + "' inserted into the table.");

                if (waveName.equals("Random")) {
                    random.add(i + 1, shearForceComplex.getReal());
                } else if (waveName.equals("KMMH02")) {
                    kumamoto.add(i + 1, shearForceComplex.getReal());
                } else if (waveName.equals("FKS020")) {
                    tohoku.add(i + 1, shearForceComplex.getReal());
                } else if (waveName.startsWith("Kobe")) {
                    kobe.add(i + 1, shearForceComplex.getReal());

                }

            }

          
            dataset.addSeries("Random", random.toArray());
            dataset.addSeries("tohoku", tohoku.toArray());
            dataset.addSeries("kumamoto", kumamoto.toArray());
            dataset.addSeries("kobe", kobe.toArray());
            // Create the chart
            JFreeChart chart = ChartFactory.createXYLineChart("Sf_C2FA4nsJ", "Testname", "Shearforce (kN)",
                    dataset, PlotOrientation.VERTICAL, true, true, false);

            // Customize the chart
            XYPlot plot = chart.getXYPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.BLACK);
            plot.setDomainGridlinesVisible(true);
            plot.setDomainGridlinePaint(Color.BLACK);
            NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
            domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont(13f));
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setRange(0, 20); // Set the y-axis range
            rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());

            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesPaint(0, Color.RED);
            plot.setRenderer(renderer);

            // Export the chart as PNG
            int width = 1400;
            int height = 1000;
//            String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\sea01\\sf_C2FA3ns.png";
//            File chartFile = new File(filePath);
//            ChartUtils.saveChartAsPNG(chartFile, chart, width, height);

//            // Display the chart in a frame
            ChartFrame frame = new ChartFrame("Shear Force Results", chart);
            frame.setPreferredSize(new Dimension(1200, 800));
            frame.pack();
            frame.setVisible(true);

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A200BeamShearC2FA4nsJ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
