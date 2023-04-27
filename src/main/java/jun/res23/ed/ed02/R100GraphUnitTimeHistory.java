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
import java.nio.file.Files;
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
import jun.fourier.FourierTransform;
import jun.fourier.FourierTransformV2;
import jun.fourier.FourierUtils;
import jun.raspi.alive.UnitInfo;
import jun.raspi.reader.PayloadFileReader;
import jun.raspi.realtime.h2.PayloadFileToH2;
import jun.res23.ed.util.EdefenseInfo;
import jun.util.SVGWriter;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.math3.stat.StatUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * datfileviewer から作成。とにかく全部グラフを書く。
 *
 * @deprecated Use R102 then R103
 * @author jun
 */
@Deprecated
public class R100GraphUnitTimeHistory {

    private static final Logger logger = Logger.getLogger(R100GraphUnitTimeHistory.class.getName());

    // ひずみについてはここで係数を決めちゃってるので注意。
    public static final double gain = 128.0;
    public static final double gaugeFactor = -105;
    public static final double factor = 1.0 / gain / Math.pow(2, 24) * 4 / gaugeFactor * 1e6;

    public static void main(String[] args) {
        ZoneId zone = ZoneId.systemDefault();

        // dat ファイルを h2 に読み込む部分。 datafile を null にしとけば読み込まない。
        Path datafile = null;// Paths.get("/home/jun/Dropbox (SSLUoT)/res22/nk/nk13k阪和データ確認_230207/20221216_1900.dat");
        ZonedDateTime h2start = null; //ZonedDateTime.of(2023, 1, 25, 1, 5, 0, 0, zone); // 2023/01/25 01:00 からのデータだがこれは時間が設定されていなかったため。実際は2023/2/2 16時ごろ。
        ZonedDateTime h2end = null; //h2start.plusMinutes(5);
// データベースの名前
        //     String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q01_20230215_123406";
//        String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q02_20230215_125245";
//        String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q03_20230215_130954";
//        String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q04_20230215_132603";
//        String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q05_20230215_133656";
//        String dburl = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q06_20230215_134900";
//        String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q08_20230215_140021";
//        String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q09_20230215_141133";
//        String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q10_20230215_165846";
        String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q11_20230215_171757";

        // たくさんの図を出力するフォルダ。 null は許容されない。
        // 自動的に上の dburlから実験番号 (D01Q03とか）を抽出する。
        int slashindex = dburl.lastIndexOf("/");
        String testname = dburl.substring(slashindex + 1, slashindex + 7);
        Path svgdir = Paths.get("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R100" + testname);

        double f0 = 0.1;
        double f1 = 10.0;
        double df = 0.01;

        ZonedDateTime svgStart = null;// ZonedDateTime.of(2022, 12, 16, 19, 53, 3, 0, zone);
        ZonedDateTime svgEnd = null;// svgStart.plusSeconds(10);

        if (datafile != null)
        try {
            if (false) {
                // 2023/01/22 datafile のファイル名（フォルダ名は見ない）を見て、すでに読み込んでいれば、読み込まないように修正
                PayloadFileToH2.read(datafile, dburl, h2start, h2end);
            }
        } catch (SQLException ex) {
            Logger.getLogger(R100GraphUnitTimeHistory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(R100GraphUnitTimeHistory.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!Files.exists(svgdir)) {
            try {
                Files.createDirectory(svgdir);
            } catch (IOException ex) {
                Logger.getLogger(R100GraphUnitTimeHistory.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }

        R100GraphUnitTimeHistory d = new R100GraphUnitTimeHistory(dburl, svgStart, svgEnd);
        try {
            //          d.setBasePhase("b8:27:eb:97:87:39/01/acc02", "X[gal]");
            d.graphall(svgdir, f0, f1, df);
        } catch (SQLException ex) {
            Logger.getLogger(R100GraphUnitTimeHistory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private final ZonedDateTime end;
    private final ZonedDateTime start;
    private final String dburl;
    private final long endMillis;
    private final long startMillis;
    private String baseColumnName;
    private String baseTableName;
    private final Connection con;
    public boolean showStrTimeHistory = true;
    public boolean showStrSpecturm = true;
    public boolean showStrClock = true;
    public boolean showStrPhase = true;
    
    ZonedDateTime latestStartTime=null;
    ZonedDateTime earliestEndTime=null;

    public R100GraphUnitTimeHistory(String dburl, ZonedDateTime start, ZonedDateTime end) {
        this.start = start;
        this.end = end;
        this.dburl = dburl;
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

    public R100GraphUnitTimeHistory(Connection con, ZonedDateTime start, ZonedDateTime end) {
        this.start = start;
        this.end = end;
        this.dburl = null;
        this.con = con;
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

    public void graphall(Path svgdir, double f0, double f1, double df) throws SQLException {
        graphall(svgdir, f0, f1, df, "%s.svg");
    }

    public void graphall(Path svgdir, double f0, double f1, double df, String filenameformat) throws SQLException {
        Connection con;
        if (dburl != null) {
            con = DriverManager.getConnection(dburl, "junapp", "");
        } else {
            con = this.con;
        }
        HashMap<String, String> map = new HashMap<>();
        for (UnitInfo uni : EdefenseInfo.allunits) {
            map.put(uni.getHardwareAddress(), uni.getName());
        }

        // dbからinfoを読む。dbのなかにinfoが入っていればいいが、入っていないと検知できないので、
        // 上記に変更。つまりEdefense.allunit
//        Statement st = con.createStatement();
//        DatabaseMetaData md = con.getMetaData();
//        ResultSet mdrs = md.getTables(null, null, "%/info", null);
//        while (mdrs.next()) {
//            String s = mdrs.getString("TABLE_NAME");
//            String mac = s.substring(0, 17);
//            ResultSet rs = st.executeQuery("select MESSAGE from \"" + s + "\" order by servertimemillis desc limit 1");
//            if (rs.next()) {
//                String message = rs.getString(1);
//                JSONParser parser = new JSONParser();
//                JSONObject o;
//                try {
//                    o = (JSONObject) parser.parse(message);
//                    String name = (String) o.get("name");
//                    logger.log(Level.INFO, "name=" + name);
//                    map.put(mac, name);
//                } catch (ParseException ex) {
//                    Logger.getLogger(R100GraphAllTimeHistory.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//
//        }
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
        if (this.dburl != null) {
            con.close();
        }
    }

    public void graphSingleUnit(Connection con, String macaddress, String name, Path svgfile, double f0, double f1, double df, double[] basephase) throws SQLException {
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
            Logger.getLogger(R100GraphUnitTimeHistory.class.getName()).log(Level.WARNING, "", ex);
            return null;
        }

    }

    public void graphSingleAcc02(Connection con, String macaddress, String name, Path svgfile, double f0, double f1, double df, double[] basephase) {
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

            int w = 400, h = 250;
            SVGGraphics2D g2d = SVGWriter.prepareGraphics2D(w, h * 3);
            timehistoryChart.draw(g2d, new Rectangle2D.Double(0, 0, w, h));
            spectrumChart.draw(g2d, new Rectangle2D.Double(0, h, w, h));
            phaseChart.draw(g2d, new Rectangle2D.Double(0, h * 2, w, h));
            try {
                SVGWriter.outputSVG(g2d, svgfile);
            } catch (IOException ex) {
                Logger.getLogger(R100GraphUnitTimeHistory.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(R100GraphUnitTimeHistory.class.getName()).log(Level.WARNING, "", ex);
            return;
        }

    }

    public static Paint colors[] = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.pink, Color.CYAN, Color.LIGHT_GRAY, Color.ORANGE};

    public void graphSingleStr01(Connection con, String macaddress, String name, Path svgfile, double f0, double f1, double df, double[] basephase) throws SQLException {
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

        int w = 800, h = 250;

        boolean[] shows = {showStrClock, showStrPhase, showStrSpecturm, showStrTimeHistory};
        int graphCount = 0;
        for (boolean show : shows) {
            if (show) {
                graphCount++;
            }
        }

        if (svgfile != null) {
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
                Logger.getLogger(R100GraphUnitTimeHistory.class.getName()).log(Level.SEVERE, null, ex);
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
            logger.log(Level.INFO, "showing graph");
            JunChartUtil.show(charts);

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
            Logger.getLogger(R100GraphUnitTimeHistory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(R100GraphUnitTimeHistory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
