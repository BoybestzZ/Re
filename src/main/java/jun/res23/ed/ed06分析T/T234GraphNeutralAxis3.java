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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ChoiceFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartCreator;
import jun.chart.JunChartUtil;
import jun.chart.JunXYChartCreator2;
import jun.res23.ed.util.BeamInfo;
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import jun.util.JunShapes;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.NumberTickUnitSource;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * T232の結果を使う。
 *
 * @author jun
 */
public class T234GraphNeutralAxis3 {

    private static final Logger logger = Logger.getLogger(T234GraphNeutralAxis3.class.getName());
    private static final String inputTable = "T232NeutralAxis";
    static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/T234NeutralAxis3");

    public static void main(String[] args) {
        main("LA3S1");
        main("LA3S2");
        main("LA3S3");
        main("LA3S4");
        main("LA3S5");
    }

    public static void main(String section) {
        try {
            final BeamSectionInfo[] sections = {EdefenseInfo.LA3S1, EdefenseInfo.LA3S2, EdefenseInfo.LA3S3, EdefenseInfo.LA3S4, EdefenseInfo.LA3S5};
            final String dburl = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/res22ed06";

//            String section = "LA3S5";
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            boolean directionPositive = true;
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            add(dataset, con, section, true);
            add(dataset, con, section, false);

            XYDataset collection = convertToXYDataset(dataset);

            XYLineAndShapeRenderer re = new XYLineAndShapeRenderer(true, true);
            re.setSeriesPaint(0, Color.RED);
            re.setSeriesPaint(1, Color.BLUE);
            re.setSeriesPaint(2, new Color(0, 0.8f, 0));
            re.setSeriesPaint(3, Color.MAGENTA);
            re.setSeriesPaint(4, Color.RED);
            re.setSeriesPaint(5, Color.BLUE);
            re.setSeriesPaint(6, new Color(0, 0.8f, 0));
            re.setSeriesPaint(7, Color.MAGENTA);

            re.setSeriesShapesFilled(0, true);
            re.setSeriesShapesFilled(1, true);
            re.setSeriesShapesFilled(2, true);
            re.setSeriesShapesFilled(3, true);
            re.setSeriesShapesFilled(4, false);
            re.setSeriesShapesFilled(5, false);
            re.setSeriesShapesFilled(6, false);
            re.setSeriesShapesFilled(7, false);

            Stroke solid = JunShapes.MEDIUM_LINE;
            Stroke dashed = JunShapes.THIN_DASHED;
            re.setSeriesStroke(0, solid);
            re.setSeriesStroke(1, solid);
            re.setSeriesStroke(2, solid);
            re.setSeriesStroke(3, solid);
            re.setSeriesStroke(4, dashed);
            re.setSeriesStroke(5, dashed);
            re.setSeriesStroke(6, dashed);
            re.setSeriesStroke(7, dashed);

            JFreeChart chart = new JunXYChartCreator2().setRenderer(re).setRangeAxisLabel("NeutralAxisLocation (from slab top) [mm]").setDataset(collection).create();
            XYPlot plot = chart.getXYPlot();
//            plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
//            plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(2.0f);
            plot.getRangeAxis().setInverted(true);
            plot.getRangeAxis().setRange(0, 350);
//            plot.setDomainGridlinesVisible(true);
            NumberAxis da = (NumberAxis) plot.getDomainAxis();
            da.setTickUnit(new NumberTickUnit(1));
            double[] limits = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24};
            String[] testnames = new String[EdefenseInfo.alltests.length];

            for (int i = 0; i < EdefenseInfo.alltests.length; i++) {
                EdefenseKasinInfo t = EdefenseInfo.alltests[i];
                testnames[i] = t.getTestName() + t.getWaveName();

            }
            da.setVerticalTickLabels(true);
            da.setRange(0.1, limits.length + 0.9);
            da.setNumberFormatOverride(new ChoiceFormat(limits, testnames));

            if (svgdir != null) {
                try {
                    if (!Files.exists(svgdir)) {
                        Files.createDirectory(svgdir);
                    }
                    Path svgfile = svgdir.resolve(section + ".svg");

                    JunChartUtil.svg(svgfile, 400, 400, chart);
                } catch (IOException ex) {
                    Logger.getLogger(T234GraphNeutralAxis3.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                JunChartUtil.show(section, chart);
            }
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(T234GraphNeutralAxis3.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void add(DefaultCategoryDataset dataset, Connection con, String section, boolean directionPositive) throws SQLException {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("select TESTNAME, \"NeutralAxis[mm]\" from \"" + inputTable + "\" where SECTION='" + section + "' and \"DirectionPositive\"=" + directionPositive + " order by TESTNAME");
        while (rs.next()) {
            String testname = rs.getString(1);
            double xn = rs.getDouble(2);

            String wavename = EdefenseInfo.lookForTestName(testname).getWaveName();
            if (wavename.startsWith("Kob")) {
                dataset.addValue(null, section + (directionPositive ? "P" : "N") + wavename.substring(0, 4), testname + "(" + wavename + ")");
            } else {
                dataset.addValue(xn, section + (directionPositive ? "P" : "N") + wavename.substring(0, 4), testname + "(" + wavename + ")");
            }
        }
        return;

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
