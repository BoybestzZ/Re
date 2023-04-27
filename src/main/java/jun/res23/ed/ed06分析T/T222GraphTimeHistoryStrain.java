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
import jun.res23.ed.util.ColumnSectionInfo;
import jun.res23.ed.util.EdefenseInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class T222GraphTimeHistoryStrain {

    private static final Logger logger = Logger.getLogger(T222GraphTimeHistoryStrain.class.getName());
    private static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/T221GraphTimeHistoryStrain");
    private static final Path pngdir = null;// Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/T221GraphTimeHistoryStrain");

    public static void main(String[] args) {
        try {
            // 各データベースを読み取って計算する。
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS2FA3B);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS2FA3C);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS2FA3T);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS3FA3B);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS3FA3C);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS3FA3T);

            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS2FA4B);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS2FA4C);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS2FA4T);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS3FA4B);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS3FA4C);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS3FA4T);

            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS2FB3B);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS2FB3C);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS2FB3T);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS3FB3B);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS3FB3C);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS3FB3T);

            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS2FB4B);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS2FB4C);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS2FB4T);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS3FB4B);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS3FB4C);
            T222GraphTimeHistoryStrain.svg(svgdir, EdefenseInfo.CS3FB4T);

        } catch (SQLException ex) {
            Logger.getLogger(T222GraphTimeHistoryStrain.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void show(ColumnSectionInfo section) throws SQLException {
        svg(null, section);
    }

    private static void svg(Path svgdir, ColumnSectionInfo section) throws SQLException {
        logger.log(Level.INFO, "opened. Opening output database " + outputDb);
        Connection cono = DriverManager.getConnection(outputDb, "junapp", "");
        Statement sto = cono.createStatement();
        logger.log(Level.INFO, "opened.");

        String gaugeNames[] = {section.getNorthName(), section.getEastName(), section.getWestName(), section.getSouthName()};
        String locationNames[] = {"North", "East", "West", "South"};

        DefaultXYDataset c = new DefaultXYDataset();
        for (int i = 0; i < gaugeNames.length; i++) {
            String gaugeName = gaugeNames[i];

            ResultSet rs = sto.executeQuery("select \"TotalTime[s]\",\"Strain[με]\" from "
                    + " \"" + T220CreateTimeHistoryStrain.outputSchema + "\".\"" + gaugeName + "\" order by 1");

            double[][] ar = ResultSetUtils.createSeriesArray(rs);
            ar = Mabiki.mabiki(ar, 2000);
            c.addSeries(locationNames[i] + "(" + gaugeName + ")", ar);

        }
        NumberAxis ya = new NumberAxis("Comp. Strain[με" + "]");
        NumberAxis xa = new NumberAxis("Time [s]");
        XYPlot plot = new JunXYChartCreator2().setRangeAxis(ya).setDomainAxis(xa)
                .setRangeZeroBaselineVisible(true)
                .setDataset(c).getPlot();
//show(section.getName());

        // テスト番号を追加
        ResultSet rs = sto.executeQuery("SELECT testname,min(\"TotalTime[s]\") FROM \"T220TimeHistoryStrain\".\"a01/01\" group by testname;");

        double y = ya.getLowerBound();
        while (rs.next()) {
            String testname = rs.getString(1);
            double startTime = rs.getDouble(2);
            ValueMarker vm = new ValueMarker(startTime);
            plot.addDomainMarker(vm);
            XYTextAnnotation ta = new XYTextAnnotation(testname, startTime, y);
            ta.setTextAnchor(TextAnchor.TOP_LEFT);
            ta.setRotationAnchor(TextAnchor.TOP_LEFT);
            ta.setRotationAngle(-Math.PI * 0.5);
            plot.addAnnotation(ta);
        }
        JFreeChart chart = new JFreeChart(plot);

        if (svgdir == null) {

            if (pngdir != null) {
                Path pngfile = pngdir.resolve(section.getName() + ".png");
                try {
                    JunChartUtil.png(pngfile, 600, 300, chart);
                } catch (IOException ex) {
                    Logger.getLogger(T222GraphTimeHistoryStrain.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                JunChartUtil.show(chart);
            }
        } else {
            Path svgfile = svgdir.resolve(section.getName() + ".svg");
            try {
                JunChartUtil.svg(svgfile, 600, 300, chart);
            } catch (IOException ex) {
                Logger.getLogger(T222GraphTimeHistoryStrain.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        cono.close();
    }
}
