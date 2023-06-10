/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.chart.JunXYChartCreator2;
import jun.data.ResultSetUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class T231GraphTimeHistoryBeamColumnNM {

    private static final Logger logger = Logger.getLogger(T231CreateTimeHistoryBeamColumnNM.class.getName());
    public static final String outputSchema = "T231TimeHistoryNM";
    public static final String outputDb = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res23/ed/ed06分析T/res22ed06";

    public static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed06分析T/T231GraphTimeHistoryBeamNM");

    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection(outputDb, "junapp", "");
            Statement st = con.createStatement();

            String beamName = "4";

            String[] sections = {"LA" + beamName + "S1", "LA" + beamName + "S2", "LA" + beamName + "S3", "LA" + beamName + "S4", "LA" + beamName + "S5"};

            String test = "D01Q02";
            ResultSet rs = st.executeQuery("select \"TimePerTest[s]\" from \"T122BeamNMDistribution\" where TESTNAME='" + test + "t' and BEAMNAME='Beam" + beamName + "'");

            rs.next();
            double time1
                    = rs.getDouble(1);
            rs.next();
            double time2 = rs.getDouble(1);

            DefaultXYDataset dataset = new DefaultXYDataset();
            for (String sec : sections) {
                rs = st.executeQuery("select \"TimePerTest[s]\", \"BendingMomentPerTest[kNm]\" from \"T231TimeHistoryNM\"." + sec
                        + " where TESTNAME='" + test + "t' order by 1");

                double[][] ar = ResultSetUtils.createSeriesArray(rs);

                dataset.addSeries(sec, ar);
            }

            XYLineAndShapeRenderer re = new XYLineAndShapeRenderer(true, false);
//            re.setSeriesPaint(3,Color.CYAN);
            Path path = svgdir.resolve(test+"_Beam" + beamName + ".svg");

            XYPlot plot = new JunXYChartCreator2().setDataset(dataset).setRenderer(re)
                    .setDomainAxisRange(5, 15)
                    .setDomainAxisLabel("Time [s]")
                    .setRangeAxisLabel("Bending moment [kNm]").getPlot();

            ValueMarker marker1 = new ValueMarker(time1);
            marker1.setPaint(Color.BLACK);
            plot.addDomainMarker(marker1);
            ValueMarker marker2 = new ValueMarker(time2);
            marker2.setPaint(Color.BLACK);
            plot.addDomainMarker(marker2);

            JFreeChart chart = new JFreeChart(plot);
            JunChartUtil.customizeChart(chart);
            try {
                JunChartUtil.svg(path, 500, 250, chart);
            } catch (IOException ex) {
                Logger.getLogger(T231GraphTimeHistoryBeamColumnNM.class.getName()).log(Level.SEVERE, null, ex);
            }

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(T231GraphTimeHistoryBeamColumnNM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
