/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed02;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.data.ResultSetUtils;
import jun.fourier.FourierUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class R171GraphStoryDriftR {

    public static void main(String[] args) {
        try {
            Path databaseDir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R140DatabaseQ");
            String inputSchema = "R151FourierR";
            String outputTable = "R170StoryDriftR";
            main(databaseDir, "D01Q01", outputTable);
//            main(databaseDir, "D01Q02",  "R151FourierR");
//            main(databaseDir, "D01Q03",  "R151FourierR");
//            main(databaseDir, "D01Q04",  "R151FourierR");
//            main(databaseDir, "D01Q05",  "R151FourierR");
//            main(databaseDir, "D01Q06",  "R151FourierR");
//            main(databaseDir, "D01Q08",  "R151FourierR");
//            main(databaseDir, "D01Q09",  "R151FourierR");
//            main(databaseDir, "D01Q10",  "R151FourierR");
//            main(databaseDir, "D01Q11",  "R151FourierR");
//            main(databaseDir, "D02Q01",  "R151FourierR");
//            main(databaseDir, "D02Q02",  "R151FourierR");
//            main(databaseDir, "D02Q03",  "R151FourierR");
//            main(databaseDir, "D02Q05",  "R151FourierR");
//            main(databaseDir, "D02Q06",  "R151FourierR");
//            main(databaseDir, "D02Q07",  "R151FourierR");
//            main(databaseDir, "D02Q08",  "R151FourierR");
//            main(databaseDir, "D03Q01",  "R151FourierR");
//            main(databaseDir, "D03Q02",  "R151FourierR");
//            main(databaseDir, "D03Q03",  "R151FourierR");
//            main(databaseDir, "D03Q04",  "R151FourierR");
//            main(databaseDir, "D03Q05",  "R151FourierR");
//            main(databaseDir, "D03Q06",  "R151FourierR");
//            main(databaseDir, "D03Q08",  "R151FourierR");
//            main(databaseDir, "D03Q09",       "R151FourierR");
        } catch (SQLException ex) {
            Logger.getLogger(R171GraphStoryDriftR.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void main(Path databaseDir, String testname, String tablename) throws SQLException {
        ZoneId zone = ZoneId.systemDefault();
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");

        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        Statement st = con.createStatement();

        // basephase
        double[] basephaseY = readBasePhase(con, "R151FourierR", "b03/01", "Phase[rad]");
        double[] basephaseX = readBasePhase(con, "R151FourierR", "h02/01", "Phase[rad]");
        ResultSet rs = st.executeQuery("select \"Freq[Hz]\",\"RelAmpX[gal*s]\",\"RelPhaseX[rad]\",\"RelAmpY[gal*s]\",\"RelPhaseY[rad]\" from \"" + tablename + "\" order by 1");
        double[][] k01array = ResultSetUtils.createSeriesArray(rs);

        DefaultXYDataset datasetA = new DefaultXYDataset();
        DefaultXYDataset datasetP = new DefaultXYDataset();
//        DefaultXYDataset datasetY = new DefaultXYDataset();
        datasetA.addSeries("ampX", new double[][]{k01array[0], k01array[1]});
        datasetA.addSeries("ampY", new double[][]{k01array[0], k01array[3]});

        for (int i = 0; i < k01array[0].length; i++) {
            k01array[2][i] -= basephaseX[i];
            k01array[4][i] -= basephaseY[i];
        }
        FourierUtils.normalizePhase(k01array[2]);
        FourierUtils.normalizePhase(k01array[4]);

        datasetP.addSeries("phaseX", new double[][]{k01array[0], k01array[2]});
        datasetP.addSeries("phaseY", new double[][]{k01array[0], k01array[4]});

        XYPlot plotA = new XYPlot();
        plotA.setDataset(datasetA);
        XYLineAndShapeRenderer reA = new XYLineAndShapeRenderer(true, false);
        plotA.setRenderer(reA);
        NumberAxis xaxis = new NumberAxis("Freq[Hz]");
        NumberAxis yaxis = new NumberAxis("Amp[gal*s]");
        plotA.setRangeAxis(yaxis);
        plotA.setDomainAxis(xaxis);

        JFreeChart chartA = new JFreeChart(plotA);

        XYPlot plotP = new XYPlot();
        plotP.setDataset(datasetP);
        XYLineAndShapeRenderer reP = new XYLineAndShapeRenderer(false, true);
        reP.setDefaultShapesFilled(false);
        plotP.setRenderer(reP);
        NumberAxis xaxisP = new NumberAxis("Freq[Hz]");
        NumberAxis yaxisP = new NumberAxis("Amp[gal*s]");
        plotP.setRangeAxis(yaxisP);
        plotP.setDomainAxis(xaxisP);

        JFreeChart chartP = new JFreeChart(plotP);

        JunChartUtil.customizeChart(chartP);
        JunChartUtil.customizeChart(chartA);

        JunChartUtil.show(new JFreeChart[][]{{chartA, chartP}});

        con.close();

    }

    private static double[] readBasePhase(Connection con, String schema, String tablename, String columnname) {
        try {
            Statement st = con.createStatement();

            ResultSet rs;

            rs = st.executeQuery("select \"Freq[Hz]\" , \"" + columnname + "\" from \"" + schema + "\".\"" + tablename + "\" "
                    + " order by 1");

            double[][] array = ResultSetUtils.createSeriesArray(rs);
            return array[1];
        } catch (SQLException ex) {
            Logger.getLogger(R171GraphStoryDriftR.class
                    .getName()).log(Level.WARNING, "", ex);
            return null;
        }

    }

}
