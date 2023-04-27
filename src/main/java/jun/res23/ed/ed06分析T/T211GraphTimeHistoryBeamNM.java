/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

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
import jun.chart.Mabiki;
import jun.data.ResultSetUtils;
import static jun.res23.ed.ed06分析T.T200CreateTimeHistoryNM.outputDb;
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class T211GraphTimeHistoryBeamNM {

    private static final Logger logger = Logger.getLogger(T211GraphTimeHistoryBeamNM.class.getName());

    public static void main(String[] args) {
        try {
            Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/T211GraphTimeHisotryNM");
            // 各データベースを読み取って計算する。
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LA3S1);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LA3S2);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LA3S3);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LA3S4);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LA3S5);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LA4S1);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LA4S2);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LA4S3);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LA4S4);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LA4S5);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LAAS1);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LAAS2);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LAAS3);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LAAS4);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LAAS5);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LABS1);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LABS2);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LABS3);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LABS4);
            T211GraphTimeHistoryBeamNM.svg(svgdir, EdefenseInfo.LABS5);

        } catch (SQLException ex) {
            Logger.getLogger(T211GraphTimeHistoryBeamNM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(T211GraphTimeHistoryBeamNM.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void svg(Path svgdir, BeamSectionInfo section) throws SQLException, IOException {
        logger.log(Level.INFO, "opened. Opening output database " + outputDb);
        Connection cono = DriverManager.getConnection(outputDb, "junapp", "");
        Statement sto = cono.createStatement();
        logger.log(Level.INFO, "opened.");

        ResultSet rs = sto.executeQuery("select \"TotalTime[s]\",\"AxialForce[kN]\",\"BendingMoment[kNm]\" from \"" + T210CreateTimeHistoryBeamNM.outputSchema + "\".\"" + section.getName() + "\" order by 1");

        double[][] ar = ResultSetUtils.createSeriesArray(rs);

        double[][] arm = Mabiki.mabiki(new double[][]{ar[0], ar[2]}, 2000);
        double[][] ara = Mabiki.mabiki(new double[][]{ar[0], ar[1]}, 2000);

        XYPlot plot = new XYPlot();
        DefaultXYDataset moment = new DefaultXYDataset();

        moment.addSeries(
                "Moment", arm);
        DefaultXYDataset axial = new DefaultXYDataset();

        axial.addSeries(
                "Axial", ara);

        plot.setDataset(0, moment);
        plot.setDataset(1, axial);
        plot.setDomainAxis(new NumberAxis("Time[s]"));
        NumberAxis ya, ym;

        plot.setRangeAxis(
                0, ym = new NumberAxis("Moment[kNm]"));
        plot.setRangeAxis(
                1, ya = new NumberAxis("Axial[kN]"));
        XYLineAndShapeRenderer rem = new XYLineAndShapeRenderer(true, false);
        XYLineAndShapeRenderer rea = new XYLineAndShapeRenderer(true, false);

        plot.setRenderer(
                0, rem);
        plot.setRenderer(
                1, rea);
        plot.mapDatasetToRangeAxis(
                0, 0);
        plot.mapDatasetToRangeAxis(
                1, 1);

        JFreeChart chart = new JFreeChart(plot);

        cono.close();

        JunChartUtil.matchRangeZero(plot);
        if (svgdir
                == null) {
            JunChartUtil.show(chart);
        } else {
            Path svgfile = svgdir.resolve(section.getName() + ".svg");
            JunChartUtil.svg(svgfile, 700, 300, chart);
        }

    }

    private static void show(BeamSectionInfo section) throws SQLException, IOException {
        svg(null, section);
    }

}
