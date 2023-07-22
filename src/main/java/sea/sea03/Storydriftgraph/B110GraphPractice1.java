/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea03.Storydriftgraph;

import sea.sea02.*;
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
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author 75496
 */
public class B110GraphPractice1 {

    public static void main(String[] args) {
        String[] testnames = {"D01Q01", "D01Q02", "D01Q03", "D01Q04", "D01Q05", "D01Q06"};

        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";

            // Connect to database
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            // Prepare Dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            for (String testname : testnames) {

                String sql = "SELECT TESTNAME,substring(SECTION,5,1), \"StiffnessMomentXA[Nm/m]\",\"StiffnessMomentXP[rad]\" FROM \"A310SectionNM\" where TESTNAME='" + testname + "' and SECTION like 'LA3S%'";

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
                    series.add(sectionNo, stiffnessReal); // Store X,Y data.
                    // Change the key of XYSeries
                    // series.setKey(testname);
                }

                // Add the series to the dataset               
                dataset.addSeries(series);

            }
            // Prepare X adn Y axis
            NumberAxis xaxis = new NumberAxis("Section No");
            NumberAxis yaxis = new NumberAxis("Stiffness");

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
            Logger.getLogger(B110GraphPractice1.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
