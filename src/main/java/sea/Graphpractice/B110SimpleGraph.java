/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.Graphpractice;

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
public class B110SimpleGraph {

    public static void main(String[] args) {

        // Prepare storage for XY data
        XYSeries series = new XYSeries("Result");
        series.add(0,0);
        series.add(1,2);
        series.add(3,-2);
        series.add(4,5);
        // Prepare Dataset
        XYSeriesCollection dataset=new XYSeriesCollection();
        dataset.addSeries(series);
        // Prepare X adn Y axis
        NumberAxis xaxis=new NumberAxis("Section No");
        NumberAxis yaxis=new NumberAxis("Stiffness");
        // Invert Y axis.
        yaxis.setInverted(true);
        // Prepare Renderer
        XYLineAndShapeRenderer renderer=new XYLineAndShapeRenderer(true, true);
        // Prepare XYPlot
        XYPlot plot=new XYPlot(dataset, xaxis, yaxis, renderer);
        // Create CHart
        JFreeChart chart=new JFreeChart(plot);
        // Show Chart
        JunChartUtil.show(chart);

    }

}
