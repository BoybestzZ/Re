/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea01.columnbeam.Beammomentdiagram;

import java.awt.BasicStroke;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author 75496
 */
public class B110GraphBeam3 {

    public static void main(String[] args) throws IOException {
        String[] testnames = {"D01Q09", 
//            "D01Q02", "D01Q03", "D01Q04", "D01Q05", "D01Q06", "D01Q08", "D01Q09", 
//                                  "D01Q10", "D01Q11", "D02Q01", "D02Q02", "D02Q03", "D02Q05", 
//                                  "D02Q06", "D02Q08", "D03Q01", "D03Q02", "D03Q03", "D03Q04", "D03Q05", 
//                                  "D03Q06", "D03Q08", "D03Q09"
        };      
//        String[] testnames = {"D01Q01"};

        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";

            double section = 0.23;
            
            // Connect to database
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            // Prepare Dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            for (String testname : testnames) {

                String sql = "SELECT TESTNAME, CASE ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) WHEN 1 THEN 0.435 WHEN 2 THEN 0.955 WHEN 3 THEN 1.575 WHEN 4 THEN 2.195 WHEN 5 THEN 2.715 END AS NewColumn,"
                        + "\"StiffnessAxialA[N/m]\"*0.000002, \"StiffnessAxialP[rad]\", \"StiffnessMomentXA[Nm/m]\"*0.000002, \"StiffnessMomentXP[rad]\" "
                        + "FROM \"A310SectionNM\" WHERE TESTNAME = '" +testname+ "' AND SECTION LIKE 'LA3S%'";

                // Execute query.
                ResultSet rs = st.executeQuery(sql);

                // Prepare storage for XY data
                XYSeries series = new XYSeries(testname);
//                
//                 // Create 'momentLA3' table if it doesn't exist
//                String createTableQuery = "CREATE TABLE IF NOT EXISTS momentLA3 (TestName VARCHAR(20), moment DOUBLE)";
//                st.executeUpdate(createTableQuery);

                // Extract data from ResultSet and store the data to the XYseries
                while (rs.next()) {
                    //  String testname=rs.getString(1);
                    double sectionNo = rs.getDouble(2);
                    double axialamplitude = rs.getDouble(3);
                    double axialphase = rs.getDouble(4);
                    double momentamplitude = rs.getDouble(5);
                    double momentphase = rs.getDouble(6);
                    Complex axialMoment = ComplexUtils.polar2Complex(axialamplitude, axialphase);
                    Complex moment = ComplexUtils.polar2Complex(momentamplitude, momentphase);

                    Complex allMoment = moment.add((axialMoment).multiply(section));
                    double allMomentReal = allMoment.getReal(); 
                    series.add(sectionNo, allMomentReal); // Store X,Y data.
                    // Change the key of XYSeries
                    // series.setKey(testname);
                    
//                    // Insert data into 'momentLA3' table
//                    String insertQuery = "INSERT INTO momentLA3 (TestName, moment) VALUES ('" + testname + "', '" + allMomentReal + "')";
//                    st.executeUpdate(insertQuery);
//                    System.out.println("Record for TestName '" + testname + "' inserted into the table.");
//                    System.out.println(allMomentReal);
//                    System.out.println("test");


                }

                // Add the series to the dataset               
                dataset.addSeries(series);
                


            }
            // Prepare X adn Y axis
            NumberAxis xaxis = new NumberAxis("Section No");
            NumberAxis yaxis = new NumberAxis("Stiffness");
            yaxis.setRange(-20, 10); // Set the y-axis range from 0 to 20
//            yaxis.setInverted(true);

            
            // Prepare Renderer
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);

            // Prepare XYPlot
            XYPlot plot = new XYPlot(dataset, xaxis, yaxis, renderer);
            plot.setRangeZeroBaselineVisible(true);  // Dsplay y axis.

            // Create CHart
            JFreeChart chart = new JFreeChart(plot);
            

            // Show Chart
            JunChartUtil.show(chart);
            
            int width = 400;
            int height = 400;
            String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\Beammoment\\BM_3.svg";
            JunChartUtil.svg(filePath, width, height, chart);

            // close the statement
            st.close();
            
            // close connection
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(B110GraphBeam3.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
