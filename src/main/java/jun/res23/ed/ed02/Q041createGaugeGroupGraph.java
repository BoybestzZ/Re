/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jun.res23.ed.ed02;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.chart.JunXYChartCreator2;
import jun.chart.Mabiki;
import jun.data.ResultSetUtils;
import jun.fourier.FFTv2;
import jun.fourier.FourierTransformV2;
import jun.fourier.FourierUtils;
import jun.raspi.alive.UnitInfo;
import jun.raspi.realtime.h2.PayloadFileToH2;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.GaugeGroup;
import jun.util.SVGWriter;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.math3.stat.StatUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * createAllGraphから作成。一つのユニットのグラフを表示する。
 *
 * @author jun
 */
public class Q041createGaugeGroupGraph {

    private static final Logger logger = Logger.getLogger(Q041createGaugeGroupGraph.class.getName());

    // ひずみについてはここで係数を決めちゃってるので注意。
    public static final double gain = 128.0;
    public static final double gaugeFactor = 2.10;
    public static final double factor = 1.0 / gain / Math.pow(2, 24) * 4 / gaugeFactor * 1e6;
//    public static final String[] unit_a03 = {"a03", "00:0e:c6:47:16:4b"};

    public static UnitInfo unit = EdefenseInfo.a01;

    public static void main(String[] args) {
        ZoneId zone = ZoneId.systemDefault();
        //
        Path datafile = Paths.get("/home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230203E-Defenseテスト計測/20230125_0100.dat");
        Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230203E-Defenseテスト計測/220202GaugeGroupGraph");
//     String dburl = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res21/kk/kk63建研振動台実験2021年度/220121準備/20220121_1300";
        String dburl = "jdbc:h2:file:/home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230203E-Defenseテスト計測/db";

        ZonedDateTime h2start = ZonedDateTime.of(2023, 1, 25, 1, 5, 0, 0, zone); // 2023/01/25 01:00 からのデータだがこれは時間が設定されていなかったため。実際は2023/2/2 16時ごろ。

        ZonedDateTime h2end = h2start.plusMinutes(5);

        try {
            if (true) {
                // 2023/01/22 datafile のファイル名（フォルダ名は見ない）を見て、すでに読み込んでいれば、読み込まないように修正
                PayloadFileToH2.read(datafile, dburl, h2start, h2end);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Q041createGaugeGroupGraph.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Q041createGaugeGroupGraph.class.getName()).log(Level.SEVERE, null, ex);
        }

        Q041createGaugeGroupGraph d = new Q041createGaugeGroupGraph(dburl, null/*start*/, /*end*/ null);
        try {
            //d.graphSingleGaugeGroup(EdefenseInfo.GSLA_3_L, /*svgfile*/ null, /*f0*/ 1.0, /*f1*/ 3.0, /*df*/ 0.01, /*basephase*/ null);
            d.graphGaugeGroups(EdefenseInfo.allGaugeGroup, /*svgdir*/ svgdir, /*f0*/ 1.0, /*f1*/ 3.0, /*df*/ 0.01, /*basephase*/ null);
            //          d.setBasePhase("b8:27:eb:97:87:39/01/acc02", "X[gal]");
            //            d.graphall(svgdir, 1.0, 3.0, 0.01);
        } catch (SQLException ex) {
            Logger.getLogger(Q041createGaugeGroupGraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private final ZonedDateTime end;
    private final ZonedDateTime start;
    private final String dburl; // これは外部から与えられたもの。 dburl または con が外部から与えられる。
    private final Connection con; // これは外部から与えられたもの。
    private final long endMillis;
    private final long startMillis;
    private String baseColumnName;
    private String baseTableName;
    public boolean showStrTimeHistory = true;
    public boolean showStrSpecturm = true;
    public boolean showStrClock = true;
    public boolean showStrPhase = true;

    public Q041createGaugeGroupGraph(String dburl, ZonedDateTime start, ZonedDateTime end) {
        this.start = start;
        this.end = end;
        this.dburl = dburl; // con か dburlのどちらかが与えられる。
        this.con = null;
        if (start != null) {
            this.startMillis = start.toEpochSecond() * 1000;
        } else {
            this.startMillis = 0;
        }
        if (end != null) {
            this.endMillis = end.toEpochSecond() * 1000;
        } else {
            this.endMillis = Long.MAX_VALUE;
        }
        baseTableName = baseColumnName = null;
    }

    private Q041createGaugeGroupGraph(Connection con, ZonedDateTime start, ZonedDateTime end) {
        this.start = start;
        this.end = end;
        this.dburl = null;
        this.con = con;
        this.startMillis = start.toEpochSecond() * 1000;
        this.endMillis = end.toEpochSecond() * 1000;
        baseTableName = baseColumnName = null;
    }

    public void graphall(Path svgdir, double f0, double f1, double df) throws SQLException {
        graphall(svgdir, f0, f1, df, "%s.svg");
    }

    public void graphall(Path svgdir, double f0, double f1, double df, String filenameformat) throws SQLException {
        Connection con;
        if (dburl != null) { // つまりこれは con がnullってこと。
            con = DriverManager.getConnection(dburl, "junapp", "");
        } else {
            con = this.con;
        }
        HashMap<String, String> map = new HashMap<>();

        Statement st = con.createStatement();
        DatabaseMetaData md = con.getMetaData();
        ResultSet mdrs = md.getTables(null, null, "%/info", null);
        while (mdrs.next()) {
            String s = mdrs.getString("TABLE_NAME");
            String mac = s.substring(0, 17);
            ResultSet rs = st.executeQuery("select MESSAGE from \"" + s + "\" order by servertimemillis desc limit 1");
            if (rs.next()) {
                String message = rs.getString(1);
                JSONParser parser = new JSONParser();
                JSONObject o;
                try {
                    o = (JSONObject) parser.parse(message);
                    String name = (String) o.get("name");
                    logger.log(Level.INFO, "name=" + name);
                    map.put(mac, name);
                } catch (ParseException ex) {
                    Logger.getLogger(Q041createGaugeGroupGraph.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        double[] basephase = null;
        if (baseColumnName != null) {
            logger.log(Level.INFO, "reading base phase");
            basephase = readBasePhase(con, baseTableName, baseColumnName, f0, f1, df);
        }

        Set<Map.Entry<String, String>> entryset = map.entrySet();
        for (Map.Entry<String, String> s : entryset) {
            String macaddress = s.getKey();
            String name = s.getValue();
            graphSingleUnit(con, macaddress, name, svgdir.resolve(String.format(filenameformat, name)), f0, f1, df, basephase);
        }
        if (this.dburl != null) { // con のほうがnullだったので、 dburlからconを作成した場合。
            con.close();
        }
    }

    public void graphGaugeGroups(GaugeGroup[] gg, Path svgdir, double f0, double f1, double df, double[] basephase) throws SQLException {
        for (GaugeGroup ggg : gg) {

            Path svgfile = null;
            if (svgdir != null) {
                svgfile = svgdir.resolve(ggg.getName() + ".svg");
            }
            graphSingleGaugeGroup(ggg, svgfile, f0, f1, df, basephase);
        }
    }

    public void graphSingleGaugeGroup(GaugeGroup gg, Path svgfile, double f0, double f1, double df, double[] basephase) throws SQLException {
        Connection con;
        if (dburl != null) { // つまりこれは con がnullってこと。
            con = DriverManager.getConnection(dburl, "junapp", "");
        } else {
            con = this.con;
        }
        graphSingleGaugeGroup(con, gg, svgfile, f0, f1, df, basephase);

        if (dburl != null) {
            con.close();
        }
    }

    /**
     * 外部からの入口。 conを作成して、 privateのgraphSingleUnitを実行する。
     *
     * @param macaddress
     * @param name
     * @param svgfile
     * @param f0
     * @param f1
     * @param df
     * @param basephase
     * @throws SQLException
     */
    public void graphSingleUnit(String macaddress, String name, Path svgfile, double f0, double f1, double df, double[] basephase) throws SQLException {
        Connection con;
        if (dburl != null) { // つまりこれは con がnullってこと。
            con = DriverManager.getConnection(dburl, "junapp", "");
        } else {
            con = this.con;
        }
        graphSingleUnit(con, macaddress, name, svgfile, f0, f1, df, basephase);

        if (dburl != null) {
            con.close();
        }
    }

    /**
     *
     * @param con
     * @param macaddress
     * @param name
     * @param svgfile
     * @param f0
     * @param f1
     * @param df
     * @param basephase
     * @throws SQLException
     */
    private void graphSingleUnit(Connection con, String macaddress, String name, Path svgfile, double f0, double f1, double df, double[] basephase) throws SQLException {
        DatabaseMetaData md = con.getMetaData();
        // まずは acc02を探す
        {
            ResultSet tables = md.getTables(null, null, macaddress + "%/acc02", null);
            if (tables.next()) { // もし acc02が見つかったら
                graphSingleAcc02(con, macaddress, name, svgfile, f0, f1, df, basephase);
            }
        }
        //次にstr01を探す
        {
            ResultSet tables = md.getTables(null, null, macaddress + "%/str01", null);
            if (tables.next()) { // もし str01が見つかったら
                graphSingleStr01(con, macaddress, name, svgfile, f0, f1, df, basephase);
            }
        }
    }

    public void setBasePhase(String tablename, String columnname) {
        this.baseTableName = tablename;
        this.baseColumnName = columnname;
    }

    private double[] readBasePhase(Connection con, String tablename, String columnname, double f0, double f1, double df) {
        try {
            Statement st = con.createStatement();

            ResultSet rs;

            rs = st.executeQuery("select \"T[ms]\" , \"" + columnname + "\" from \"" + tablename + "\" "
                    + " where \"T[ms]\" between " + startMillis + " and " + endMillis);

            double[][] array = ResultSetUtils.createSeriesArray(rs);
            array[1] = FFTv2.zeroAverage(array[1]);
            double dt = (array[0][1] - array[0][0]) / 1000.0; //ms->s
            FourierTransformV2 ftX = new FourierTransformV2(dt, array[1]);
            double[][] specX = ftX.getAmplitudeSpectrum(f0, f1, df, /*coeff*/ false, /*phase*/ true);
            return specX[2];
        } catch (SQLException ex) {
            Logger.getLogger(Q041createGaugeGroupGraph.class.getName()).log(Level.WARNING, "", ex);
            return null;
        }

    }

    private void graphSingleAcc02(Connection con, String macaddress, String name, Path svgfile, double f0, double f1, double df, double[] basephase) {
        try {
            Statement st = con.createStatement();
            DefaultXYDataset dataset = new DefaultXYDataset();
            DefaultXYDataset specDataset = new DefaultXYDataset();
            DefaultXYDataset phaseDataset = new DefaultXYDataset();

            ResultSet rs;

            rs = st.executeQuery("select \"T[ms]\" , \"X[gal]\",\"Y[gal]\",\"Z[gal]\" from \"" + macaddress + "/01/acc02\" "
                    + " where \"T[ms]\" between " + startMillis + " and " + endMillis);

            double[][] array = ResultSetUtils.createSeriesArray(rs);
            array[1] = FFTv2.zeroAverage(array[1]);
            array[2] = FFTv2.zeroAverage(array[2]);
            array[3] = FFTv2.zeroAverage(array[3]);
            double[][] thx = Mabiki.mabiki(new double[][]{array[0], array[1]}, 10000);
            double[][] thy = Mabiki.mabiki(new double[][]{array[0], array[2]}, 10000);
            double[][] thz = Mabiki.mabiki(new double[][]{array[0], array[3]}, 10000);
            dataset.addSeries("X", thx);
            dataset.addSeries("Y", thy);
            dataset.addSeries("Z", thz);
            double dt = (array[0][1] - array[0][0]) / 1000.0; //ms->s
            FourierTransformV2 ftX = new FourierTransformV2(dt, array[1]);
            double[][] specX = ftX.getAmplitudeSpectrum(f0, f1, df, /*coeff*/ false, /*phase*/ true);
            FourierTransformV2 ftY = new FourierTransformV2(dt, array[2]);
            double[][] specY = ftY.getAmplitudeSpectrum(f0, f1, df, /*coeff*/ false, /*phase*/ true);
            FourierTransformV2 ftZ = new FourierTransformV2(dt, array[3]);
            double[][] specZ = ftZ.getAmplitudeSpectrum(f0, f1, df, /*coeff*/ false, /*phase*/ true);
            specDataset.addSeries("X", new double[][]{specX[0], specX[1]});
            specDataset.addSeries("Y", new double[][]{specY[0], specY[1]});
            specDataset.addSeries("Z", new double[][]{specZ[0], specZ[1]});

            if (basephase != null) {
                for (int i = 0; i < specX[0].length; i++) {
                    double dphase = specX[2][i] - basephase[i];
                    dphase = FourierUtils.normalizePhase(dphase, -0.5 * Math.PI, 1.5 * Math.PI);
                    specX[2][i] = dphase;
                    dphase = specY[2][i] - basephase[i];
                    dphase = FourierUtils.normalizePhase(dphase, -0.5 * Math.PI, 1.5 * Math.PI);
                    specY[2][i] = dphase;
                    dphase = specZ[2][i] - basephase[i];
                    dphase = FourierUtils.normalizePhase(dphase, -0.5 * Math.PI, 1.5 * Math.PI);
                    specZ[2][i] = dphase;
                }
            }

            phaseDataset.addSeries("X", new double[][]{specX[0], specX[2]});
            phaseDataset.addSeries("Y", new double[][]{specY[0], specY[2]});
            phaseDataset.addSeries("Z", new double[][]{specZ[0], specZ[2]});

            DateAxis da = new DateAxis("Time");
            JFreeChart timehistoryChart = new JunXYChartCreator2().setDomainAxis(da)
                    .setRangeAxisLabel("Strain[με]")
                    .setDataset(dataset).setTitle("TimeHistory " + macaddress + " [" + name + "]")
                    .setRangeAxisFixedDimension(40)
                    .create();

            JFreeChart spectrumChart = new JunXYChartCreator2().setDomainAxisLabel("Freq[Hz]")
                    .setRangeAxisLabel("Amplitude[με*s]")
                    .setDataset(specDataset).setTitle("Amplitude Spectrum " + macaddress + " [" + name + "]")
                    .setRangeAxisFixedDimension(40)
                    .setDomainAxisAutoRangeIncludesZero(false)
                    .create();
            JFreeChart phaseChart = new JunXYChartCreator2().setDomainAxisLabel("Freq[Hz]")
                    .setRangeAxisLabel("Phase [rad]")
                    .setDataset(phaseDataset).setTitle("Phase Spectrum " + macaddress + " [" + name + "]")
                    .setRangeAxisFixedDimension(40)
                    .setDomainAxisAutoRangeIncludesZero(false)
                    .setRangeAxisRange(-0.5 * Math.PI, 1.5 * Math.PI)
                    .setLinesAndShapesVisible(false, true)
                    .create();
            if (svgfile != null) {
                int w = 400, h = 250;
                SVGGraphics2D g2d = SVGWriter.prepareGraphics2D(w, h * 3);
                timehistoryChart.draw(g2d, new Rectangle2D.Double(0, 0, w, h));
                spectrumChart.draw(g2d, new Rectangle2D.Double(0, h, w, h));
                phaseChart.draw(g2d, new Rectangle2D.Double(0, h * 2, w, h));

                try {
                    SVGWriter.outputSVG(g2d, svgfile);
                } catch (IOException ex) {
                    Logger.getLogger(Q041createGaugeGroupGraph.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JunChartUtil.show(name + "[" + macaddress + "]", timehistoryChart, spectrumChart, phaseChart);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Q041createGaugeGroupGraph.class.getName()).log(Level.WARNING, "", ex);
            return;
        }

    }

    public static Paint colors[] = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.pink, Color.CYAN, Color.LIGHT_GRAY, Color.ORANGE, Color.DARK_GRAY, Color.BLACK};

    private void graphSingleStr01(Connection con, String macaddress, String name, Path svgfile, double f0, double f1, double df, double[] basephase) throws SQLException {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT max( distinct cast(substring(topic,19,2) as int)) FROM MESSAGE where topic like '" + macaddress + "/%/str01'");
        rs.next();
        int maxch = rs.getInt(1);

        DefaultXYDataset[] thDatasets = new DefaultXYDataset[maxch];
        DefaultXYDataset[] specDatasets = new DefaultXYDataset[maxch];
        DefaultXYDataset phaseDataset = new DefaultXYDataset();
        DefaultXYDataset clockDataset = new DefaultXYDataset();

        DateAxis thDomainAxis = new DateAxis("Time");

        XYPlot thPlot = new XYPlot();

        thPlot.setDomainAxis(thDomainAxis);

        NumberAxis specDomainAxis = new NumberAxis("Freq [Hz]");
        specDomainAxis.setAutoRangeIncludesZero(false);
        XYPlot specPlot = new XYPlot();
        specPlot.setDomainAxis(specDomainAxis);

        double thmin[] = new double[maxch];
        double thmax[] = new double[maxch];
        double specmax[] = new double[maxch];

        double[] basephase1 = null; //=basephase;
        for (int ch = 1; ch <= maxch; ch++) {
            DefaultXYDataset thDataset = new DefaultXYDataset();
            DefaultXYDataset specDataset = new DefaultXYDataset();
            thDatasets[ch - 1] = thDataset;
            specDatasets[ch - 1] = specDataset;
            try {
                rs = st.executeQuery("select \"T[ms]\" , \"STRAIN[LSB]\" , \"FLAG\" from \"" + macaddress + "/0" + ch + "/str01\" "
                        + " where \"T[ms]\" between " + startMillis + " and " + endMillis);
                double[][] array = ResultSetUtils.createSeriesArray(rs);

                for (int i = 0; i < array[0].length; i++) {
                    // ひずみLSBには係数をかけてμεにする。
                    array[1][i] *= factor;
                    // FLAG は INTに変換して下から 7ビット目を抽出する。 (
                    array[2][i] = ((int) array[2][i]) & 0x40;  // 1になっていれば、128になるはず？
                }
                array[1] = FFTv2.zeroAverage(array[1]);
                double[][] clockmabiki = Mabiki.mabiki(new double[][]{array[0], array[2]}, 5000);
                clockDataset.addSeries("0" + ch, clockmabiki);
                double[][] thx = Mabiki.mabiki(new double[][]{array[0], array[1]}, 5000);
                thDataset.addSeries("0" + ch, thx);
                thmax[ch - 1] = StatUtils.max(thx[1]);
                thmin[ch - 1] = StatUtils.min(thx[1]);

                double dt = (array[0][1] - array[0][0]) / 1000.0; //ms->s
                FourierTransformV2 ft = new FourierTransformV2(dt, array[1]);
//                double f0 = 0.2;
//                double f1 = 5;
//                double df = 0.01;
                double[][] spec = ft.getAmplitudeSpectrum(f0, f1, df, /*coeff*/ false, /*phase*/ true);
                specDataset.addSeries("0" + ch, new double[][]{spec[0], spec[1]});
                specmax[ch - 1] = StatUtils.max(spec[1]);
                if (basephase1 == null) {
                    basephase1 = new double[spec[0].length];
                }

                double dphase[] = new double[spec[0].length];
                for (int i = 0; i < spec[0].length; i++) {
                    double dp;
                    if (ch == 1 && basephase == null) {
                        dp = 0;
                        basephase1[i] = spec[2][i];
                    } else {
                        dp = spec[2][i] - basephase1[i];
                    }
                    dphase[i] = FourierUtils.normalizePhase(dp, -0.5 * Math.PI, 1.5 * Math.PI);
                }
                phaseDataset.addSeries("0" + ch, new double[][]{spec[0], dphase});

                thPlot.setDataset(ch - 1, thDataset);
                specPlot.setDataset(ch - 1, specDataset);
                XYLineAndShapeRenderer thRenderer = new XYLineAndShapeRenderer(true, false);
                thRenderer.setSeriesPaint(0, colors[ch - 1]);
                thPlot.setRenderer(ch - 1, thRenderer);
                XYLineAndShapeRenderer specRenderer = new XYLineAndShapeRenderer(true, false);
                specRenderer.setSeriesPaint(0, colors[ch - 1]);
                specPlot.setRenderer(ch - 1, specRenderer);

                NumberAxis thRangeAxis = new NumberAxis("Strain " + ch + " [με*s]");
                NumberAxis specRangeAxis = new NumberAxis("Amplitude " + ch + " [με*s]");
                thPlot.setRangeAxis(ch - 1, thRangeAxis);
                specPlot.setRangeAxis(ch - 1, specRangeAxis);
                specPlot.mapDatasetToRangeAxis(ch - 1, ch - 1);
                thPlot.mapDatasetToRangeAxis(ch - 1, ch - 1);

            } catch (SQLException sqle) {
                continue;
            }
        }
        // thPlotのRangeAxisの範囲を設定する。
        // 設定の根拠は thmax[]とthmin[]を用いる。

        int grp[] = groupingMax(thmax);
        for (int i = 0; i < maxch; i++) {
            logger.log(Level.INFO, "grp[i]" + grp[i] + " value= " + thmax[grp[i]]);
            thPlot.getRangeAxis(i).setUpperBound(thmax[grp[i]]);
        }

        grp = groupingMin(thmin);
        for (int i = 0; i < maxch; i++) {
            thPlot.getRangeAxis(i).setLowerBound(thmin[grp[i]]);
        }
        grp = groupingMax(specmax);
        for (int i = 0; i < maxch; i++) {
            logger.log(Level.INFO, "grp[i]" + grp[i] + " value= " + specmax[grp[i]]);
            specPlot.getRangeAxis(i).setUpperBound(specmax[grp[i]]);
        }

        JFreeChart timehistoryChart = new JFreeChart(thPlot);
        timehistoryChart.setTitle("TimeHistory " + macaddress + " [" + name + "]");
        timehistoryChart.setBackgroundPaint(Color.WHITE);
        JFreeChart clockChart = new JunXYChartCreator2().setDomainAxis(thDomainAxis)
                .setRangeAxisLabel("ClockData")
                .setDataset(clockDataset).setTitle("Clock " + macaddress + " [" + name + "]")
                .setRangeAxisFixedDimension(40)
                .create();

        JFreeChart spectrumChart = new JFreeChart(specPlot);
        spectrumChart.setTitle("Amplitude Spectrum " + macaddress + " [" + name + "]");
        spectrumChart.setBackgroundPaint(Color.WHITE);

        JFreeChart phaseChart = new JunXYChartCreator2().setDomainAxisLabel("Freq[Hz]")
                .setRangeAxisLabel("PhaseDiff [rad]")
                .setDataset(phaseDataset).setTitle("Phase Spectrum " + macaddress + " [" + name + "]")
                .setRangeAxisFixedDimension(40)
                .setDomainAxisAutoRangeIncludesZero(false)
                .setRangeAxisRange(-0.5 * Math.PI, 1.5 * Math.PI)
                .setLinesAndShapesVisible(false, true)
                .create();
        boolean[] shows = {showStrClock, showStrPhase, showStrSpecturm, showStrTimeHistory};
        int graphCount = 0;
        for (boolean show : shows) {
            if (show) {
                graphCount++;
            }
        }
        if (svgfile != null) {
            int w = 800, h = 250;

            SVGGraphics2D g2d = SVGWriter.prepareGraphics2D(w, h * graphCount);

            int gi = 0;
            if (showStrTimeHistory) {
                timehistoryChart.draw(g2d, new Rectangle2D.Double(0, h * gi, w, h));
                gi++;
            }
            if (showStrClock) {
                clockChart.draw(g2d, new Rectangle2D.Double(0, h * gi, w, h));
                gi++;
            }
            if (showStrSpecturm) {
                spectrumChart.draw(g2d, new Rectangle2D.Double(0, h * gi, w, h));
                gi++;
            }
            if (showStrPhase) {
                phaseChart.draw(g2d, new Rectangle2D.Double(0, h * gi, w, h));
                gi++;
            }
//        Path svgpath = svgdir.resolve(name + ".svg");
            try {
                SVGWriter.outputSVG(g2d, svgfile);
            } catch (IOException ex) {
                Logger.getLogger(Q041createGaugeGroupGraph.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JFreeChart[] charts = new JFreeChart[graphCount];
            int gi = 0;
            if (showStrTimeHistory) {
                charts[gi] = timehistoryChart;
                gi++;
            }
            if (showStrClock) {
                charts[gi] = clockChart;
                gi++;
            }
            if (showStrSpecturm) {
                charts[gi] = spectrumChart;
                gi++;
            }
            if (showStrPhase) {
                charts[gi] = phaseChart;
                gi++;
            }
            JunChartUtil.show(name + "[" + macaddress + "]", charts);
        }

    }

    private void graphSingleGaugeGroup(Connection con, GaugeGroup gaugeGroup, Path svgfile, double f0, double f1, double df, double[] basephase) throws SQLException {
        Path pngfile = svgfile;
        svgfile = null;

        Statement st = con.createStatement();
        int maxch = gaugeGroup.getNumgerOfChannels();
        int mabikiNumber = 5000;
        DefaultXYDataset[] thDatasets = new DefaultXYDataset[maxch];
        DefaultXYDataset[] specDatasets = new DefaultXYDataset[maxch];
        DefaultXYDataset phaseDataset = new DefaultXYDataset();
        DefaultXYDataset clockDataset = new DefaultXYDataset();

        DateAxis thDomainAxis = new DateAxis("Time");

        XYPlot thPlot = new XYPlot();

        thPlot.setDomainAxis(thDomainAxis);

        NumberAxis specDomainAxis = new NumberAxis("Freq [Hz]");
        specDomainAxis.setAutoRangeIncludesZero(false);
        XYPlot specPlot = new XYPlot();
        specPlot.setDomainAxis(specDomainAxis);

        double thmin[] = new double[maxch];
        double thmax[] = new double[maxch];
        double specmax[] = new double[maxch];

        double[] basephase1 = null; //=basephase; 
        for (int ch = 0; ch < maxch; ch++) {
            DefaultXYDataset thDataset = new DefaultXYDataset();
            DefaultXYDataset specDataset = new DefaultXYDataset();
            thDatasets[ch] = thDataset;
            specDatasets[ch] = specDataset;
            String macaddress = gaugeGroup.getMacAddress(ch);
            int unitch = gaugeGroup.getUnitChannel(ch);
            String chname = gaugeGroup.getUnitName(ch);
            try {
                ResultSet rs = st.executeQuery("select \"T[ms]\" , \"STRAIN[LSB]\" , \"FLAG\" from \"" + macaddress + "/0" + unitch + "/str01\" "
                        + " where \"T[ms]\" between " + startMillis + " and " + endMillis);
                double[][] array = ResultSetUtils.createSeriesArray(rs);

                for (int i = 0; i < array[0].length; i++) {
                    // ひずみLSBには係数をかけてμεにする。
                    array[1][i] *= factor;
                    // FLAG は INTに変換して下から 7ビット目を抽出する。 (
                    array[2][i] = ((int) array[2][i]) & 0x40;  // 1になっていれば、128になるはず？
                }
                array[1] = FFTv2.zeroAverage(array[1]);
                double[][] clockmabiki = Mabiki.mabiki(new double[][]{array[0], array[2]}, mabikiNumber);
                clockDataset.addSeries(chname, clockmabiki); // 凡例の番号は単に 0 から の数字を示す。
                double[][] thx = Mabiki.mabiki(new double[][]{array[0], array[1]}, mabikiNumber);
                thDataset.addSeries(chname, thx);
                thmax[ch] = StatUtils.max(thx[1]);
                thmin[ch] = StatUtils.min(thx[1]);

                double dt = (array[0][1] - array[0][0]) / 1000.0; //ms->s
                FourierTransformV2 ft = new FourierTransformV2(dt, array[1]);
//                double f0 = 0.2;
//                double f1 = 5;
//                double df = 0.01;
                double[][] spec = ft.getAmplitudeSpectrum(f0, f1, df, /*coeff*/ false, /*phase*/ true);
                specDataset.addSeries(chname, new double[][]{spec[0], spec[1]});
                specmax[ch] = StatUtils.max(spec[1]);
                if (basephase1 == null) {
                    basephase1 = new double[spec[0].length];
                }

                double dphase[] = new double[spec[0].length];
                for (int i = 0; i < spec[0].length; i++) {
                    double dp;
                    if (ch == 0 && basephase == null) {
                        dp = 0;
                        basephase1[i] = spec[2][i];
                    } else {
                        dp = spec[2][i] - basephase1[i];
                    }
                    dphase[i] = FourierUtils.normalizePhase(dp, -0.5 * Math.PI, 1.5 * Math.PI);
                }
                phaseDataset.addSeries(chname, new double[][]{spec[0], dphase});

                thPlot.setDataset(ch, thDataset);
                specPlot.setDataset(ch, specDataset);
                XYLineAndShapeRenderer thRenderer = new XYLineAndShapeRenderer(true, false);
                thRenderer.setSeriesPaint(0, colors[ch]);
                thPlot.setRenderer(ch, thRenderer);
                XYLineAndShapeRenderer specRenderer = new XYLineAndShapeRenderer(true, false);
                specRenderer.setSeriesPaint(0, colors[ch]);
                specPlot.setRenderer(ch, specRenderer);

                NumberAxis thRangeAxis = new NumberAxis("Strain " + ch + " [με*s]");
                NumberAxis specRangeAxis = new NumberAxis("Amplitude " + ch + " [με*s]");
                thPlot.setRangeAxis(ch, thRangeAxis);
                specPlot.setRangeAxis(ch, specRangeAxis);
                specPlot.mapDatasetToRangeAxis(ch, ch);
                thPlot.mapDatasetToRangeAxis(ch, ch);

            } catch (SQLException sqle) {
                Logger.getLogger(Q041createGaugeGroupGraph.class.getName()).log(Level.SEVERE, "", sqle);
                return;
            }
        }
        // thPlotのRangeAxisの範囲を設定する。
        // 設定の根拠は thmax[]とthmin[]を用いる。

        int grp[] = groupingMax(thmax);
        logger.log(Level.INFO, "maxch=" + maxch + ", thplot=" + thPlot.getRangeAxisCount());
        for (int i = 0; i < maxch; i++) {
            logger.log(Level.INFO, "grp[" + i + "]=" + grp[i] + " value= " + thmax[grp[i]]);
            thPlot.getRangeAxis(i).setUpperBound(thmax[grp[i]]);
        }

        grp = groupingMin(thmin);
        for (int i = 0; i < maxch; i++) {
            thPlot.getRangeAxis(i).setLowerBound(thmin[grp[i]]);
        }
        grp = groupingMax(specmax);
        for (int i = 0; i < maxch; i++) {
            logger.log(Level.INFO, "grp[i]" + grp[i] + " value= " + specmax[grp[i]]);
            specPlot.getRangeAxis(i).setUpperBound(specmax[grp[i]]);
        }

        JFreeChart timehistoryChart = new JFreeChart(thPlot);
        timehistoryChart.setTitle("TimeHistory " + gaugeGroup.getName());
        timehistoryChart.setBackgroundPaint(Color.WHITE);
        JFreeChart clockChart = new JunXYChartCreator2().setDomainAxis(thDomainAxis)
                .setRangeAxisLabel("ClockData")
                .setDataset(clockDataset).setTitle("Clock " + gaugeGroup.getName())
                .setRangeAxisFixedDimension(40)
                .create();

        JFreeChart spectrumChart = new JFreeChart(specPlot);
        spectrumChart.setTitle("Amplitude Spectrum " + gaugeGroup.getName());
        spectrumChart.setBackgroundPaint(Color.WHITE);

        JFreeChart phaseChart = new JunXYChartCreator2().setDomainAxisLabel("Freq[Hz]")
                .setRangeAxisLabel("PhaseDiff [rad]")
                .setDataset(phaseDataset).setTitle("Phase Spectrum " + gaugeGroup.getName())
                .setRangeAxisFixedDimension(40)
                .setDomainAxisAutoRangeIncludesZero(false)
                .setRangeAxisRange(-0.5 * Math.PI, 1.5 * Math.PI)
                .setLinesAndShapesVisible(false, true)
                .create();
        boolean[] shows = {showStrClock, showStrPhase, showStrSpecturm, showStrTimeHistory};
        int graphCount = 0;
        for (boolean show : shows) {
            if (show) {
                graphCount++;
            }
        }
        if (svgfile != null) {
            int w = 1000, h = 250;

            SVGGraphics2D g2d = SVGWriter.prepareGraphics2D(w, h * graphCount);

            int gi = 0;
            if (showStrTimeHistory) {
                timehistoryChart.draw(g2d, new Rectangle2D.Double(0, h * gi, w, h));
                gi++;
            }
            if (showStrClock) {
                clockChart.draw(g2d, new Rectangle2D.Double(0, h * gi, w, h));
                gi++;
            }
            if (showStrSpecturm) {
                spectrumChart.draw(g2d, new Rectangle2D.Double(0, h * gi, w, h));
                gi++;
            }
            if (showStrPhase) {
                phaseChart.draw(g2d, new Rectangle2D.Double(0, h * gi, w, h));
                gi++;
            }
//        Path svgpath = svgdir.resolve(name + ".svg");
            try {
                SVGWriter.outputSVG(g2d, svgfile);
            } catch (IOException ex) {
                Logger.getLogger(Q041createGaugeGroupGraph.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            JFreeChart[] charts = new JFreeChart[graphCount];
            int gi = 0;
            if (showStrTimeHistory) {
                charts[gi] = timehistoryChart;
                gi++;
            }
            if (showStrClock) {
                charts[gi] = clockChart;
                gi++;
            }
            if (showStrSpecturm) {
                charts[gi] = spectrumChart;
                gi++;
            }
            if (showStrPhase) {
                charts[gi] = phaseChart;
                gi++;
            }
            if (pngfile != null) {
                try {
                    JunChartUtil.png(svgfile, 1000, 1200, charts);
                } catch (IOException ex) {
                    Logger.getLogger(Q041createGaugeGroupGraph.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JunChartUtil.show(gaugeGroup.getName(), charts);
            }
        }

    }

    public static int[] groupingMax(double[] value) {
        int[] grp = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            grp[i] = i;
        }
        for (int i = 0; i < value.length; i++) {
            for (int j = 0; j < value.length; j++) {
                if (grp[i] == grp[j]) {
                    continue;
                }
                double avg = (value[grp[i]] + value[grp[j]]) * 0.5;
                double diff = (value[grp[i]] - value[grp[j]]);
                if (Math.abs(diff / avg) < 0.5) {
                    if (diff > 0) {
                        grp[j] = grp[i];  // iのほうが大きい
                    } else {
                        grp[i] = grp[j]; // jのほうが大きい
                    }
                }
            }
        }
        return grp;
    }

    public static int[] groupingMin(double[] value) {
        int[] grp = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            grp[i] = i;
        }
        for (int i = 0; i < value.length; i++) {
            for (int j = 0; j < value.length; j++) {
                if (grp[i] == grp[j]) {
                    continue;
                }
                double avg = (value[grp[i]] + value[grp[j]]) * 0.5;
                double diff = (value[grp[i]] - value[grp[j]]);
                if (Math.abs(diff / avg) < 0.5) { // 0.5 ということは、 0.75と1.25なので、1.6倍になっている。倍半分にしたいなら、0.66にする。
                    if (diff > 0) {
                        grp[i] = grp[j];  // iのほうが大きい
                    } else {
                        grp[j] = grp[i]; // jのほうが大きい
                    }
                }
            }
        }
        return grp;
    }

    public static void main1(String[] args) {
        try {
            Path datfilepath = Path.of("/home/jun/Dropbox (SSLUoT)/res21/el/el05sync1での確認/20210628_1400.dat");
            String dburl = "jdbc:h2:/home/jun/Dropbox (SSLUoT)/res21/el/el05sync1での確認/E20210628_1400";
            ZoneId zone = ZoneId.systemDefault();
            ZonedDateTime start = null;// ZonedDateTime.of(2021,6,26,11,5,0,0, zone);
            ZonedDateTime end = null;// start.plusMinutes(10);
            String topicRegex = "b8:27:eb:0e:37:55/01/.+|b8:27:eb:f6:00:c7/01/.+";

            PayloadFileToH2.read(datfilepath, dburl, topicRegex, start, end);
        } catch (SQLException ex) {
            Logger.getLogger(Q041createGaugeGroupGraph.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Q041createGaugeGroupGraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
