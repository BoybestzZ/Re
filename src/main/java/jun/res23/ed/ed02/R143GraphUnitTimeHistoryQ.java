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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import jun.res23.ed.util.StrainGaugeInfo;
import jun.util.SVGWriter;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.math3.stat.StatUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * Created from R100. Use R102MinimumDuration.
 *
 * @author jun
 */
public class R143GraphUnitTimeHistoryQ {

    private static final Logger logger = Logger.getLogger(R143GraphUnitTimeHistoryQ.class.getName());

    public static void main(String[] args) {

        try {
            Path databaseDir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R140DatabaseQ");
            Path svgDir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R143UnitTimeHistoryQ");

//            main(databaseDir, svgDir, "D01Q01q");
//            main(databaseDir, svgDir, "D01Q02q");
//            main(databaseDir, svgDir, "D01Q03q");
//            main(databaseDir, svgDir, "D01Q04q");
//            main(databaseDir, svgDir, "D01Q05q");
//            main(databaseDir, svgDir, "D01Q06q");
//            main(databaseDir, svgDir, "D01Q08q");
//            main(databaseDir, svgDir, "D01Q09q");
//            main(databaseDir, svgDir, "D01Q10q");
//            main(databaseDir, svgDir, "D01Q11q");
//
//            main(databaseDir, svgDir, "D02Q01q");
//            main(databaseDir, svgDir, "D02Q02q");
//            main(databaseDir, svgDir, "D02Q03q");
//            main(databaseDir, svgDir, "D02Q05q");
//            main(databaseDir, svgDir, "D02Q06q");
//            main(databaseDir, svgDir, "D02Q07q");
//            main(databaseDir, svgDir, "D02Q08q");

            // 以下は済
//            main(databaseDir, svgDir, "D03Q01q");
//            main(databaseDir, svgDir, "D03Q02q");
//            main(databaseDir, svgDir, "D03Q03q");
//            main(databaseDir, svgDir, "D03Q04q");
//            main(databaseDir, svgDir, "D03Q05q");
//            main(databaseDir, svgDir, "D03Q06q");
//            main(databaseDir, svgDir, "D03Q08q");
            main(databaseDir, svgDir, "D03Q09q");
        } catch (IOException ex) {
            Logger.getLogger(R143GraphUnitTimeHistoryQ.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(Path databaseDir, Path svgDir, String dbname) throws IOException {
        ZoneId zone = ZoneId.systemDefault();
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(dbname);
        logger.log(Level.INFO, dburl);
        // たくさんの図を出力するフォルダ。 null は許容されない。
        // 自動的に上の dburlから実験番号 (D01Q03とか）を抽出する。
        int slashindex = dburl.lastIndexOf("/");
        String testname = dburl.substring(slashindex + 1, slashindex + 7);
        if (!Files.exists(svgDir)) {
            Files.createDirectory(svgDir);
        }
        Path svgsub = svgDir.resolve(dbname);

        double f0 = 0.1;
        double f1 = 10.0;
        double df = 0.01;

        ZonedDateTime svgStart = null;// ZonedDateTime.of(2022, 12, 16, 19, 53, 3, 0, zone);
        ZonedDateTime svgEnd = null;// svgStart.plusSeconds(10);

        if (!Files.exists(svgsub)) {
            try {
                Files.createDirectory(svgsub);
            } catch (IOException ex) {
                Logger.getLogger(R143GraphUnitTimeHistoryQ.class
                        .getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        UnitInfo[] units = EdefenseInfo.allunits;
//        UnitInfo[] units = new UnitInfo[]{EdefenseInfo.f02};
        try {
            R143GraphUnitTimeHistoryQ d = new R143GraphUnitTimeHistoryQ(dburl);
            //          d.setBasePhase("b8:27:eb:97:87:39/01/acc02", "X[gal]");
            d.graphall(units, svgsub, f0, f1, df);
        } catch (SQLException ex) {
            Logger.getLogger(R143GraphUnitTimeHistoryQ.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

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

    public R143GraphUnitTimeHistoryQ(String dburl) throws SQLException {

        this.dburl = dburl;
        this.con = null;

        baseTableName = baseColumnName = null;

        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("select \"LatestStartTimeMillis\",\"EarliestEndTimeMillis\" from \"R102MinimumDuration\"");
        rs.next();
        startMillis = rs.getLong(1);
        endMillis = rs.getLong(2);
        con.close();

    }

    public void graphall(UnitInfo[] units, Path svgdir, double f0, double f1, double df) throws SQLException {
        graphall(units, svgdir, f0, f1, df, "%s.svg");
    }

    public void graphall(UnitInfo[] units, Path svgdir, double f0, double f1, double df, String filenameformat) throws SQLException {
        Connection con;
        if (dburl != null) {
            con = DriverManager.getConnection(dburl, "junapp", "");
        } else {
            con = this.con;
        }

        double[] basephase = null;
        if (baseColumnName != null) {
            logger.log(Level.INFO, "reading base phase");
            basephase = readBasePhase(con, baseTableName, baseColumnName, f0, f1, df);
        }

        for (UnitInfo uni : units) {
            String name = uni.getName();
            Path svgfile = svgdir.resolve(String.format(filenameformat, name));
            graphSingleUnit(con, uni, svgfile, f0, f1, df, basephase);
        }
        if (this.dburl != null) {
            con.close();
        }
    }

    public void graphSingleUnit(Connection con, UnitInfo uni, Path svgfile, double f0, double f1, double df, double[] basephase) throws SQLException {
        DatabaseMetaData md = con.getMetaData();
        String macaddress = uni.getHardwareAddress();

        // まずは acc02を探す
        {
            ResultSet tables = md.getTables(null, null, macaddress + "%/acc02", null);
            if (tables.next()) { // もし acc02が見つかったら
                graphSingleAcc02(con, uni, svgfile, f0, f1, df, basephase);
            }
        }
        //次にstr01を探す
        {
            ResultSet tables = md.getTables(null, null, macaddress + "%/str01", null);
            if (tables.next()) { // もし str01が見つかったら
                graphSingleStr01(con, uni, svgfile, f0, f1, df, basephase);
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
            Logger.getLogger(R143GraphUnitTimeHistoryQ.class
                    .getName()).log(Level.WARNING, "", ex);
            return null;
        }

    }

    public void graphSingleAcc02(Connection con, UnitInfo uni, Path svgfile, double f0, double f1, double df, double[] basephase) {
        String macaddress = uni.getHardwareAddress();
        String name = uni.getName();
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
            double[][] thx = Mabiki.mabiki(new double[][]{array[0], array[1]}, 2000);
            double[][] thy = Mabiki.mabiki(new double[][]{array[0], array[2]}, 2000);
            double[][] thz = Mabiki.mabiki(new double[][]{array[0], array[3]}, 2000);
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
                Logger.getLogger(R143GraphUnitTimeHistoryQ.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(R143GraphUnitTimeHistoryQ.class
                    .getName()).log(Level.WARNING, "", ex);
            return;
        }

    }

    public static Paint colors[] = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.pink, Color.CYAN, Color.LIGHT_GRAY, Color.ORANGE};

    public void graphSingleStr01(Connection con, UnitInfo uni, Path svgfile, double f0, double f1, double df, double[] basephase) throws SQLException {

        String macaddress = uni.getHardwareAddress();
        String name = uni.getName();

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
            StrainGaugeInfo gi = StrainGaugeInfo.findStrainGaugeInfo(uni, ch);

            DefaultXYDataset thDataset = new DefaultXYDataset();
            DefaultXYDataset specDataset = new DefaultXYDataset();
            thDatasets[ch - 1] = thDataset;
            specDatasets[ch - 1] = specDataset;
            double factor = Double.NaN;
            if (gi == null) {
                logger.log(Level.INFO, "unit=" + uni.getName() + ", chno=" + ch);

            } else {
                factor = 1.0 / gi.getGain() / Math.pow(2, 24) * 4 / gi.getGaugeFactor() * 1e6;
            }
            rs = st.executeQuery("select \"T[ms]\" , \"STRAIN[LSB]\" , \"FLAG\" from \"" + macaddress + "/0" + ch + "/str01\" "
                    + " where \"T[ms]\" between " + startMillis + " and " + endMillis);
            double[][] array = ResultSetUtils.createSeriesArray(rs);
            logger.log(Level.INFO, "name=" + name + " min=" + array[0][0] + ", max=" + array[0][array[0].length - 1]);

            for (int i = 0; i < array[0].length; i++) {
                // ひずみLSBには係数をかけてμεにする。
                array[1][i] *= factor;
                // FLAG は INTに変換して下から 7ビット目を抽出する。 (
                array[2][i] = ((int) array[2][i]) & 0x40;  // 1になっていれば、128になるはず？
            }
            array[1] = FFTv2.zeroAverage(array[1]);
            double[][] clockmabiki = Mabiki.mabiki(new double[][]{array[0], array[2]}, 2000);
            clockDataset.addSeries("0" + ch, clockmabiki);
            double[][] thx = Mabiki.mabiki(new double[][]{array[0], array[1]}, 2000);
            thDataset.addSeries("0" + ch, thx);
            thmax[ch - 1] = StatUtils.max(thx[1]);
            thmin[ch - 1] = StatUtils.min(thx[1]);

            double dt = (array[0][array[0].length - 1] - array[0][0]) / (array[0].length - 1) / 1000.0; //ms->s
            FourierTransformV2 ft = new FourierTransformV2(dt, array[1]);
//                double f0 = 0.2;
//                double f1 = 5;
//                double df = 0.01;
            double[][] spec = ft.getAmplitudeSpectrum(f0, f1, df, /*coeff*/ false, /*phase*/ true);
            // 時刻歴とspecを出力しちゃう。
            if (!Double.isNaN(factor)) {
                st.executeUpdate("create schema if not exists \"R103TimeHistory\"");
                st.executeUpdate("drop table if exists  \"R103TimeHistory\".\"" + uni.getName() + "/0" + ch + "\"");
                st.executeUpdate("create table  \"R103TimeHistory\".\"" + uni.getName() + "/0" + ch + "\" "
                        + "(NO identity, \"T[ms]\" double, \"Strain[ε]\" double)");
                logger.log(Level.INFO, "chno=" + ch + ", array[0].length=" + array[0].length);
                for (int i = 0; i < array[0].length; i++) {
                    try {
                        st.executeUpdate("insert into \"R103TimeHistory\".\"" + uni.getName() + "/0" + ch + "\"  (\"T[ms]\",\"Strain[ε]\") "
                                + "values (" + array[0][i] + "," + array[1][i] + ")");
                    } catch (SQLException e) {
                        logger.log(Level.INFO, "factor=" + factor);
                        throw e;
                    }
                }
                st.executeUpdate("create schema if not exists \"R103Fourier\"");
                st.executeUpdate("drop table if exists  \"R103Fourier\".\"" + uni.getName() + "/0" + ch + "\"");
                st.executeUpdate("create table  \"R103Fourier\".\"" + uni.getName() + "/0" + ch + "\" "
                        + "(\"Freq[Hz]\" double, \"Amp[ε*s]\" double, \"Phase[rad]\" double) ");
                for (int i = 0; i < spec[0].length; i++) {
                    double freq = spec[0][i];
                    double amp = spec[1][i];
                    double phase = spec[2][i];
                    st.executeUpdate("insert into \"R103Fourier\".\"" + uni.getName() + "/0" + ch + "\" (\"Freq[Hz]\", \"Amp[ε*s]\", \"Phase[rad]\") "
                            + " values (" + freq + "," + amp + "," + phase + " )"
                            + " ");
                }
            }
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
            int datasetIndex = phaseDataset.getSeriesCount() - 1;

            thPlot.setDataset(datasetIndex, thDataset);
            specPlot.setDataset(datasetIndex, specDataset);
            XYLineAndShapeRenderer thRenderer = new XYLineAndShapeRenderer(true, false);
            thRenderer.setSeriesPaint(0, colors[ch - 1]);
            thPlot.setRenderer(datasetIndex, thRenderer);
            XYLineAndShapeRenderer specRenderer = new XYLineAndShapeRenderer(true, false);
            specRenderer.setSeriesPaint(0, colors[ch - 1]);
            specPlot.setRenderer(datasetIndex, specRenderer);

            NumberAxis thRangeAxis = new NumberAxis("Strain " + ch + " [με*s]");
            NumberAxis specRangeAxis = new NumberAxis("Amplitude " + ch + " [με*s]");
            thPlot.setRangeAxis(datasetIndex, thRangeAxis);
            specPlot.setRangeAxis(datasetIndex, specRangeAxis);
            specPlot.mapDatasetToRangeAxis(datasetIndex, datasetIndex);
            thPlot.mapDatasetToRangeAxis(datasetIndex, datasetIndex);

        }
        // thPlotのRangeAxisの範囲を設定する。
        // 設定の根拠は thmax[]とthmin[]を用いる。
        int seriesCount = phaseDataset.getSeriesCount();

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
                Logger.getLogger(R143GraphUnitTimeHistoryQ.class
                        .getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(R143GraphUnitTimeHistoryQ.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(R143GraphUnitTimeHistoryQ.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

}
