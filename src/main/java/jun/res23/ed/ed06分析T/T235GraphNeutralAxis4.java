/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

import java.awt.Color;
import java.awt.Stroke;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ChoiceFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.chart.JunXYChartCreator2;
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import jun.util.JunShapes;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * T232の結果を使う。
 *
 * @author jun
 */
public class T235GraphNeutralAxis4 {

    private static final Logger logger = Logger.getLogger(T235GraphNeutralAxis4.class.getName());
    private static final String inputTable = "T232NeutralAxis";
    static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/T235NeutralAxis4");

    public static void main(String[] args) {
        try {
            JFreeChart chart1 = main("LA3S1");
            JFreeChart chart2 = main("LA3S2");
            JFreeChart chart3 = main("LA3S3");
            JFreeChart chart4 = main("LA3S4");
            JFreeChart chart5 = main("LA3S5");

            if (svgdir != null) {

                try {
                    JunChartUtil.svg(svgdir.resolve("allv2.svg"), 250, 250, new JFreeChart[][]{{chart1,chart2,chart3,chart5}});
                } catch (IOException ex) {
                    Logger.getLogger(T235GraphNeutralAxis4.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(T235GraphNeutralAxis4.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static JFreeChart main(String section) throws SQLException {

        final BeamSectionInfo[] sections = {EdefenseInfo.LA3S1, EdefenseInfo.LA3S2, EdefenseInfo.LA3S3, EdefenseInfo.LA3S4, EdefenseInfo.LA3S5};
        final String dburl = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/res22ed06";

//            String section = "LA3S5";
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        Statement st = con.createStatement();

        boolean directionPositive = true;
        EdefenseKasinInfo tests[] = {
            EdefenseInfo.D01Q01,
            EdefenseInfo.D01Q02,
            EdefenseInfo.D01Q03,
            EdefenseInfo.D01Q09,
            EdefenseInfo.D01Q11,
            EdefenseInfo.D02Q01,
            EdefenseInfo.D02Q02,
            EdefenseInfo.D02Q05,
            EdefenseInfo.D02Q06,
            EdefenseInfo.D02Q08,
            EdefenseInfo.D03Q01,
            EdefenseInfo.D03Q02,
            EdefenseInfo.D03Q03,
            EdefenseInfo.D03Q09};
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        add(dataset, con, section, true, tests);
        add(dataset, con, section, false, tests);

        XYDataset collection = convertToXYDataset(dataset);

        XYLineAndShapeRenderer re = new XYLineAndShapeRenderer(true, true);
        re.setSeriesPaint(0, Color.RED);
        re.setSeriesPaint(1, Color.BLUE);
        re.setSeriesPaint(2, new Color(0, 0.8f, 0));
//            re.setSeriesPaint(3, Color.MAGENTA);
        re.setSeriesPaint(3, Color.RED);
        re.setSeriesPaint(4, Color.BLUE);
        re.setSeriesPaint(5, new Color(0, 0.8f, 0));
//            re.setSeriesPaint(7, Color.MAGENTA);

        re.setSeriesShapesFilled(0, true);
        re.setSeriesShapesFilled(1, true);
        re.setSeriesShapesFilled(2, true);
//            re.setSeriesShapesFilled(3, true);
        re.setSeriesShapesFilled(3, false);
        re.setSeriesShapesFilled(4, false);
        re.setSeriesShapesFilled(5, false);
//            re.setSeriesShapesFilled(7, false);

        Stroke solid = JunShapes.MEDIUM_LINE;
        Stroke dashed = JunShapes.THIN_DASHED;
        re.setSeriesStroke(0, solid);
        re.setSeriesStroke(1, solid);
        re.setSeriesStroke(2, solid);
//            re.setSeriesStroke(3, solid);
        re.setSeriesStroke(3, dashed);
        re.setSeriesStroke(4, dashed);
        re.setSeriesStroke(5, dashed);
//            re.setSeriesStroke(7, dashed);

        JFreeChart chart = new JunXYChartCreator2().setRenderer(re).setRangeAxisLabel("NeutralAxisLocation (from slab top) [mm]").setDataset(collection).create();
        XYPlot plot = chart.getXYPlot();
//            plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
//            plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(2.0f);
        plot.getRangeAxis().setInverted(true);
        plot.getRangeAxis().setRange(0, 350);
//            plot.setDomainGridlinesVisible(true);
        NumberAxis da = (NumberAxis) plot.getDomainAxis();
        da.setTickUnit(new NumberTickUnit(1));

        double[] limits = new double[tests.length];
        String[] testnames = new String[tests.length];

        for (int i = 0; i < tests.length; i++) {
            EdefenseKasinInfo t = tests[i];
            limits[i] = i + 1;
            testnames[i] = String.format("%02d", t.getTestNo()) ;

        }
        da.setVerticalTickLabels(true);
        da.setRange(0.1, limits.length + 0.9);
        da.setNumberFormatOverride(new ChoiceFormat(limits, testnames));

        con.close();
        return chart;

    }

    public static void svg(String section) throws SQLException {

        JFreeChart chart = main(section);

        if (svgdir != null) {
            try {
                if (!Files.exists(svgdir)) {
                    Files.createDirectory(svgdir);
                }
                Path svgfile = svgdir.resolve(section + ".svg");

                JunChartUtil.svg(svgfile, 300, 300, chart);
            } catch (IOException ex) {
                Logger.getLogger(T235GraphNeutralAxis4.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            JunChartUtil.show(section, chart);
        }
    }

    public static void add(DefaultCategoryDataset dataset, Connection con, String section, boolean directionPositive, EdefenseKasinInfo[] tests) throws SQLException {
        Statement st = con.createStatement();

        for (EdefenseKasinInfo test : tests) {

            ResultSet rs = st.executeQuery("select TESTNAME, \"NeutralAxis[mm]\" from \"" + inputTable + "\" "
                    + "where SECTION='" + section + "' and \"DirectionPositive\"=" + directionPositive + " and TESTNAME='" + test.getTestName() + "'");
            rs.next();

            String testname = rs.getString(1);
            double xn = rs.getDouble(2);

            String wavename = EdefenseInfo.lookForTestName(testname).getWaveName();
            if (wavename.startsWith("Kob")) {
                dataset.addValue(null, section + (directionPositive ? "P" : "N") + wavename.substring(0, 4), testname + "(" + wavename + ")");
            } else {
                dataset.addValue(xn, section + (directionPositive ? "P" : "N") + wavename.substring(0, 4), testname + "(" + wavename + ")");
            }
        }

    }

    public static XYDataset convertToXYDataset(CategoryDataset orig) {

        XYSeriesCollection c = new XYSeriesCollection();
        List rowList = orig.getRowKeys();
        List columnList = orig.getColumnKeys();
        for (int seriesNo = 0; seriesNo < rowList.size(); seriesNo++) {
            XYSeries s = new XYSeries(orig.getRowKey(seriesNo), false);
            for (int columnNo = 0; columnNo < columnList.size(); columnNo++) {
                Number value = orig.getValue(seriesNo, columnNo);
                if (value != null) {
                    s.add(columnNo + 1, value.doubleValue());
                }
            }
            c.addSeries(s);

        }
        return c;

    }

}
