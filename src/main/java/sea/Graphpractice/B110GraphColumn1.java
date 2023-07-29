/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.Graphpractice;

import java.io.File;
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
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author 75496
 */
public class B110GraphColumn1 {

    public static void main(String[] args) throws IOException {
        String[] testnames = {"D01Q01", "D01Q02", "D01Q03", "D01Q04", "D01Q05", "D01Q06", "D01Q08", "D01Q09", 
                                  "D01Q10", "D01Q11", "D02Q01", "D02Q02", "D02Q03", "D02Q05", 
                                  "D02Q06", "D02Q08", "D03Q01", "D03Q02", "D03Q03", "D03Q04", "D03Q05", 
                                  "D03Q06", "D03Q08", "D03Q09"};

        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";

            // Connect to database
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            // Prepare Dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            for (String testname : testnames) {

                String sql = "SELECT TESTNAME, CASE ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) WHEN 1 THEN 1 WHEN 2 THEN 2 WHEN 3 THEN 3 END AS NewColumn, \"StiffnessMomentXA[Nm/m]\"*0.000001, \"StiffnessMomentXP[rad]\" FROM \"A310SectionNM\" WHERE TESTNAME = '" + testname + "' AND SECTION LIKE 'CS2FA4%ew'";
                
                    

                // Execute query.
                ResultSet rs = st.executeQuery(sql);

                // Prepare storage for XY data
                XYSeries series = new XYSeries(testname);

                // Extract data from ResultSet and store the data to the XYseries
                while (rs.next()) {
                    //  String testname=rs.getString(1);
                    double sectionNo = rs.getDouble(2);
                    double amplitude = rs.getDouble(3);
                    double phase = rs.getDouble(4);
                    Complex stiffness = ComplexUtils.polar2Complex(amplitude, phase);
                    double stiffnessReal = stiffness.getReal();
                    series.add(stiffnessReal,sectionNo); // Store X,Y data.
                    // Change the key of XYSeries
                    // series.setKey(testname);
                }

                // Add the series to the dataset               
                dataset.addSeries(series);

            }
            // Prepare X adn Y axis
            NumberAxis xaxis = new NumberAxis("Stiffness");
            NumberAxis yaxis = new NumberAxis("Section No");

            // Prepare Renderer
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);

            // Prepare XYPlot
            XYPlot plot = new XYPlot(dataset, xaxis, yaxis, renderer);

            // Create CHart
            JFreeChart chart = new JFreeChart(plot);

            // Show Chart
            JunChartUtil.show(chart);

            // close connection
            con.close();
            
            
            

        } catch (SQLException ex) {
            Logger.getLogger(B110GraphColumn1.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
