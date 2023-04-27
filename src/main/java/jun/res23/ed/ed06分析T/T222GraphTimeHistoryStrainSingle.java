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
import jun.res23.ed.util.EdefenseKasinInfo;
import jun.res23.ed.util.StrainGaugeInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author jun
 */
public class T222GraphTimeHistoryStrainSingle {

    private static final Logger logger = Logger.getLogger(T222GraphTimeHistoryStrainSingle.class.getName());
//    private static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/T221GraphTimeHistoryStrain");
//    private static final Path pngdir = null;// Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/T221GraphTimeHistoryStrain");

    public static void main(String[] args) {
        try {
            // 各データベースを読み取って計算する。

            EdefenseKasinInfo[] kasins = {EdefenseInfo.D01Q02};
            T222GraphTimeHistoryStrainSingle.show("2F_A3_T", kasins,
                    StrainGaugeInfo.CS2F_A3_T_N, StrainGaugeInfo.CS2F_A3_T_S,
                    StrainGaugeInfo.CS2F_A3_T_E, StrainGaugeInfo.CS2F_A3_T_W
            );

            T222GraphTimeHistoryStrainSingle.show("3F_A3_B", kasins,
                    StrainGaugeInfo.CS3F_A3_B_N, StrainGaugeInfo.CS3F_A3_B_S,
                    StrainGaugeInfo.CS3F_A3_B_E, StrainGaugeInfo.CS3F_A3_B_W
            );

            T222GraphTimeHistoryStrainSingle.show("2F_A3_B", kasins,
                    StrainGaugeInfo.CS2F_A3_B_N, StrainGaugeInfo.CS2F_A3_B_S,
                    StrainGaugeInfo.CS2F_A3_B_E, StrainGaugeInfo.CS2F_A3_B_W
            );

        } catch (SQLException ex) {
            Logger.getLogger(T222GraphTimeHistoryStrainSingle.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void show(String title, EdefenseKasinInfo[] kasin, StrainGaugeInfo... gauges) throws SQLException {
        svg(null, title, kasin, gauges);
    }

    private static void svg(Path svgfile, String title, EdefenseKasinInfo[] kasin, StrainGaugeInfo... gauges) throws SQLException {
        logger.log(Level.INFO, "opened. Opening output database " + outputDb);
        Connection cono = DriverManager.getConnection(outputDb, "junapp", "");
        Statement sto = cono.createStatement();
        logger.log(Level.INFO, "opened.");

        XYSeriesCollection c = new XYSeriesCollection();
        for (int i = 0; i < gauges.length; i++) {
            String gaugeName = gauges[i].getShortName();
            XYSeries s = new XYSeries(gauges[i].getLocation(), false);

            for (int j = 0; j < kasin.length; j++) {
                ResultSet rs = sto.executeQuery("select \"TIME[s]\",\"Strain[με]\" from "
                        + " \"" + T220CreateTimeHistoryStrain.outputSchema + "\".\"" + gaugeName + "\" "
                        + " where TESTNAME='" + kasin[j].getTestName() + "t' "
                        + "order by 1");
                while (rs.next()) {
                    s.add(rs.getDouble(1), rs.getDouble(2));
                }

//                double[][] ar = ResultSetUtils.createSeriesArray(rs);
//            ar = Mabiki.mabiki(ar, 2000);
            }
            c.addSeries(s);
        }
        NumberAxis ya = new NumberAxis("Comp. Strain[με" + "]");
        NumberAxis xa = new NumberAxis("Time [s]");
        XYPlot plot = new JunXYChartCreator2().setRangeAxis(ya).setDomainAxis(xa)
                .setRangeZeroBaselineVisible(true)
                .setDataset(c).getPlot();
//show(section.getName());

        // テスト番号を追加
        if (false) {
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
        }
        JFreeChart chart = new JFreeChart(plot);
        chart.setTitle(title);
        if (svgfile == null) {

            JunChartUtil.show(chart);

        } else {
            try {
                JunChartUtil.svg(svgfile, 600, 300, chart);
            } catch (IOException ex) {
                Logger.getLogger(T222GraphTimeHistoryStrainSingle.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        cono.close();
    }
}
