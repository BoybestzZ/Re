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
 * Read databaseQ using R150Duration data to draw R graphs.
 *
 * @author jun
 */
public class R152GraphUnitTimeHistoryS {

    private static final Logger logger = Logger.getLogger(R152GraphUnitTimeHistoryS.class.getName());
    private static Path svgDir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R152UnitTimeHistoryS");
    private static String schema = "R152FourierS";

    private static String baseTableName[] = new String[]{
        EdefenseInfo.b03.getHardwareAddress() + "/01/str01",
        EdefenseInfo.h02.getHardwareAddress() + "/01/str01",};
    private static String baseColumnName = "STRAIN[LSB]";

    public static void main(String[] args) {

        try {
            Path databaseDir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R140DatabaseQ");

            main(databaseDir, "D01Q01");
            main(databaseDir, "D01Q02");
            main(databaseDir, "D01Q03");
            main(databaseDir, "D01Q04");
            main(databaseDir, "D01Q05");
            main(databaseDir, "D01Q06");
            main(databaseDir, "D01Q08");
            main(databaseDir, "D01Q09");
            main(databaseDir, "D01Q10");
            main(databaseDir, "D01Q11");
            main(databaseDir, "D02Q01");
            main(databaseDir, "D02Q02");
            main(databaseDir, "D02Q03");
            main(databaseDir, "D02Q05");
            main(databaseDir, "D02Q06");
            main(databaseDir, "D02Q07");
            main(databaseDir, "D02Q08");
            main(databaseDir, "D03Q01");
            main(databaseDir, "D03Q02");
            main(databaseDir, "D03Q03");
            main(databaseDir, "D03Q04");
            main(databaseDir, "D03Q05");
            main(databaseDir, "D03Q06");
            main(databaseDir, "D03Q08");
            main(databaseDir, "D03Q09");

        } catch (IOException ex) {
            Logger.getLogger(R152GraphUnitTimeHistoryS.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(Path databaseDir, String dbname) throws IOException {
        ZoneId zone = ZoneId.systemDefault();
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(dbname + "q");

        // たくさんの図を出力するフォルダ。 null は許容されない。
        // 自動的に上の dburlから実験番号 (D01Q03とか）を抽出する。
        int slashindex = dburl.lastIndexOf("/");
        String testname = dburl.substring(slashindex + 1, slashindex + 7);
        if (!Files.exists(svgDir)) {
            Files.createDirectory(svgDir);
        }
        Path svgsub = svgDir.resolve(dbname + "s");

        double f0 = 0.1;
        double f1 = 10.0;
        double df = 0.01;

        ZonedDateTime svgStart = null;// ZonedDateTime.of(2022, 12, 16, 19, 53, 3, 0, zone);
        ZonedDateTime svgEnd = null;// svgStart.plusSeconds(10);

        if (!Files.exists(svgsub)) {
            try {
                Files.createDirectory(svgsub);
            } catch (IOException ex) {
                Logger.getLogger(R152GraphUnitTimeHistoryS.class
                        .getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        UnitInfo[] units = EdefenseInfo.allunits;
//        UnitInfo[] units = new UnitInfo[]{EdefenseInfo.f02};
        try {
            R152GraphUnitTimeHistoryS d = new R152GraphUnitTimeHistoryS(dburl);
            //          d.setBasePhase("b8:27:eb:97:87:39/01/acc02", "X[gal]");
            d.graphall(units, svgsub, f0, f1, df);
        } catch (SQLException ex) {
            Logger.getLogger(R152GraphUnitTimeHistoryS.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private final String dburl;
    private final long endMillis;
    private final long startMillis;
    private final Connection con;
    public boolean showStrTimeHistory = true;
    public boolean showStrSpecturm = true;
    public boolean showStrClock = true;
    public boolean showStrPhase = true;

    public R152GraphUnitTimeHistoryS(String dburl) throws SQLException {

        this.dburl = dburl;
        this.con = null;

        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("select \"STARTTIMEMILLIS\",\"ENDTIMEMILLIS\" from \"R150Duration\" where TYPE='S'");
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

        double[][] basephase = null;
        if (baseColumnName != null) {
            logger.log(Level.INFO, "reading base phase");
            basephase = new double[baseTableName.length][];
            for (int j = 0; j < baseTableName.length; j++) {
                basephase[j] = readBasePhase(con, baseTableName[j], baseColumnName, f0, f1, df);
            }
//            Statement st = con.createStatement();
//            st.executeUpdate("drop  table if exists \"R151BasePhase\"");
//            st.executeUpdate("create table \"R151BasePhase\" (no int,P double) ");
//            for (int i = 0; i < basephase.length; i++) {
//                st.executeUpdate("insert into  \"R151BasePhase\" values (" + i + "," + basephase[i] + ")");
//            }
//            st.close();
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

    public void graphSingleUnit(Connection con, UnitInfo uni, Path svgfile, double f0, double f1, double df, double[][] basephase) throws SQLException {
        DatabaseMetaData md = con.getMetaData();
        String macaddress = uni.getHardwareAddress();

        // まずは acc02を探す
        {
            ResultSet tables = md.getTables(null, null, macaddress + "%/acc02", null);
            if (tables.next()) { // もし acc02が見つかったら
                graphSingleAcc02(con, uni, svgfile, f0, f1, df, basephase, startMillis, endMillis);
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

    private double[] readBasePhase(Connection con, String tablename, String columnname, double f0, double f1, double df) {
        try {
            Statement st = con.createStatement();

            ResultSet rs;

            rs = st.executeQuery("select \"T[ms]\" , \"" + columnname + "\" from \"" + tablename + "\" "
                    + " where \"T[ms]\" between " + startMillis + " and " + endMillis);

            double[][] array = ResultSetUtils.createSeriesArray(rs);
            array[1] = FFTv2.zeroAverage(array[1]);

            double dt = (array[0][array[0].length - 1] - array[0][0]) / (array[0].length - 1) / 1000.0; //ms->s

            FourierTransformV2 ftX = new FourierTransformV2(dt, array[1]);
            double[][] specX = ftX.getAmplitudeSpectrum(f0, f1, df, /*coeff*/ false, /*phase*/ true);
            return specX[2];
        } catch (SQLException ex) {
            Logger.getLogger(R152GraphUnitTimeHistoryS.class
                    .getName()).log(Level.WARNING, "", ex);
            return null;
        }

    }

    public static void graphSingleAcc02(Connection con, UnitInfo uni, Path svgfile, double f0, double f1, double df, double[][] basephase, double startMillis, double endMillis) {
        String macaddress = uni.getHardwareAddress();
        String name = uni.getName();
        try {
            Statement st = con.createStatement();
            DefaultXYDataset dataset = new DefaultXYDataset();
            DefaultXYDataset specDataset = new DefaultXYDataset();

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
            double dt = (array[0][array[0].length - 1] - array[0][0]) / (array[0].length - 1) / 1000.0; //ms->s

            FourierTransformV2 ftX = new FourierTransformV2(dt, array[1]);
            double[][] specX = ftX.getAmplitudeSpectrum(f0, f1, df, /*coeff*/ false, /*phase*/ true);
            FourierTransformV2 ftY = new FourierTransformV2(dt, array[2]);
            double[][] specY = ftY.getAmplitudeSpectrum(f0, f1, df, /*coeff*/ false, /*phase*/ true);
            FourierTransformV2 ftZ = new FourierTransformV2(dt, array[3]);
            double[][] specZ = ftZ.getAmplitudeSpectrum(f0, f1, df, /*coeff*/ false, /*phase*/ true);
            specDataset.addSeries("X", new double[][]{specX[0], specX[1]});
            specDataset.addSeries("Y", new double[][]{specY[0], specY[1]});
            specDataset.addSeries("Z", new double[][]{specZ[0], specZ[1]});

            JFreeChart[] phaseChart;
            if (basephase != null) {
                phaseChart = new JFreeChart[basephase.length];
                for (int j = 0; j < basephase.length; j++) {
//                    logger.log(Level.INFO, "basephase[" + j + "][0]=" + basephase[j][0]);
                    DefaultXYDataset phaseDataset = new DefaultXYDataset();
                    double[] phaseX = new double[specX[0].length];
                    double[] phaseY = new double[specX[0].length];
                    double[] phaseZ = new double[specX[0].length];
                    for (int i = 0; i < specX[0].length; i++) {
                        double dphase = specX[2][i] - basephase[j][i];
                        phaseX[i] = FourierUtils.normalizePhase(dphase, -0.5 * Math.PI, 1.5 * Math.PI);
                        dphase = specY[2][i] - basephase[j][i];
                        phaseY[i] = FourierUtils.normalizePhase(dphase, -0.5 * Math.PI, 1.5 * Math.PI);
                        dphase = specZ[2][i] - basephase[j][i];
                        phaseZ[i] = FourierUtils.normalizePhase(dphase, -0.5 * Math.PI, 1.5 * Math.PI);

                    }
                    phaseDataset.addSeries("X", new double[][]{specX[0], phaseX});
                    phaseDataset.addSeries("Y", new double[][]{specY[0], phaseY});
                    phaseDataset.addSeries("Z", new double[][]{specZ[0], phaseZ});
                    XYLineAndShapeRenderer re = new XYLineAndShapeRenderer(false, true);
                    re.setDefaultShapesFilled(false);
                    phaseChart[j] = new JunXYChartCreator2().setDomainAxisLabel("Freq[Hz]")
                            .setRangeAxisLabel("Phase [rad]")
                            .setDataset(phaseDataset).setTitle("Phase Spectrum " + macaddress + " [" + name + "]")
                            .setRangeAxisFixedDimension(40)
                            .setDomainAxisAutoRangeIncludesZero(false)
                            .setRangeAxisRange(-0.5 * Math.PI, 1.5 * Math.PI)
                            .setRenderer(re)
                            .create();
                }
            } else {
//                DefaultXYDataset phaseDataset = new DefaultXYDataset();
//                for (int i = 0; i < specX[0].length; i++) {
//                    double basephaseX = specX[2][i];
//                    specX[2][i] = 0.0;
//                    double dphase = specY[2][i] - basephaseX;
//                    dphase = FourierUtils.normalizePhase(dphase, -0.5 * Math.PI, 1.5 * Math.PI);
//                    specY[2][i] = dphase;
//                    dphase = specZ[2][i] - basephaseX;
//                    dphase = FourierUtils.normalizePhase(dphase, -0.5 * Math.PI, 1.5 * Math.PI);
//                    specZ[2][i] = dphase;
//                }
//                XYLineAndShapeRenderer re = new XYLineAndShapeRenderer(false, true);
//                re.setDefaultShapesFilled(false);
//
//                phaseDataset.addSeries("X", new double[][]{specX[0], specX[2]});
//                phaseDataset.addSeries("Y", new double[][]{specY[0], specY[2]});
//                phaseDataset.addSeries("Z", new double[][]{specZ[0], specZ[2]});
//                phaseChart = new JFreeChart[]{new JunXYChartCreator2().setDomainAxisLabel("Freq[Hz]")
//                    .setRangeAxisLabel("Phase [rad]")
//                    .setDataset(phaseDataset).setTitle("Phase Spectrum " + macaddress + " [" + name + "]")
//                    .setRangeAxisFixedDimension(40)
//                    .setDomainAxisAutoRangeIncludesZero(false)
//                    .setRangeAxisRange(-0.5 * Math.PI, 1.5 * Math.PI)
//                    .setRenderer(re)
//                    .create()};
                phaseChart = null;

            }

            // specを出力しちゃう。
            if (true) {

                st.executeUpdate("create schema if not exists \"" + schema + "\"");
                st.executeUpdate("drop table if exists  \"" + schema + "\".\"" + uni.getName() + "/01\"");
                st.executeUpdate("create table  \"" + schema + "\".\"" + uni.getName() + "/01\" "
                        + "(\"Freq[Hz]\" double,"
                        + " \"AmpX[gal*s]\" double, \"PhaseX[rad]\" double, "
                        + " \"AmpY[gal*s]\" double, \"PhaseY[rad]\" double, "
                        + " \"AmpZ[gal*s]\" double, \"PhaseZ[rad]\" double "
                        + ") ");
                for (int i = 0; i < specX[0].length; i++) {
                    double freq = specX[0][i];
                    double ampx = specX[1][i];
                    double phasex = specX[2][i];
                    double ampy = specY[1][i];
                    double phasey = specY[2][i];
                    double ampz = specZ[1][i];
                    double phasez = specZ[2][i];
                    st.executeUpdate("insert into \"" + schema + "\".\"" + uni.getName() + "/01\" "
                            + " values (" + freq + "," + ampx + "," + phasex
                            + "," + ampy + "," + phasey + "," + ampz + "," + phasez
                            + " )"
                            + " ");
                }
            }

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

            int w = 400, h = 250;
            SVGGraphics2D g2d = SVGWriter.prepareGraphics2D(w, h * (2 + phaseChart.length));
            timehistoryChart.draw(g2d, new Rectangle2D.Double(0, 0, w, h));
            spectrumChart.draw(g2d, new Rectangle2D.Double(0, h, w, h));
            for (int j = 0; j < phaseChart.length; j++) {
                phaseChart[j].draw(g2d, new Rectangle2D.Double(0, h * (2 + j), w, h));
            }
            try {
                SVGWriter.outputSVG(g2d, svgfile);
            } catch (IOException ex) {
                Logger.getLogger(R152GraphUnitTimeHistoryS.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(R152GraphUnitTimeHistoryS.class
                    .getName()).log(Level.WARNING, "", ex);
            return;
        }

    }

    public static Paint colors[] = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.pink, Color.CYAN, Color.LIGHT_GRAY, Color.ORANGE};

    public void graphSingleStr01(Connection con, UnitInfo uni, Path svgfile, double f0, double f1, double df, double[][] basephase) throws SQLException {

        String macaddress = uni.getHardwareAddress();
        String name = uni.getName();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT max( distinct cast(substring(topic,19,2) as int)) FROM MESSAGE where topic like '" + macaddress + "/%/str01'");
        rs.next();
        int maxch = rs.getInt(1);

        DefaultXYDataset[] thDatasets = new DefaultXYDataset[maxch];
        DefaultXYDataset[] specDatasets = new DefaultXYDataset[maxch];
        int phasenum;
        if (basephase != null) {
            phasenum = basephase.length;
        } else {
            phasenum = 1;
        }
        DefaultXYDataset[] phaseDataset = new DefaultXYDataset[phasenum];
        for (int j = 0; j < phasenum; j++) {
            phaseDataset[j] = new DefaultXYDataset();
        }
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

        double[][] basephase1 = basephase;
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
                factor = 1.0 / gi.getGain() / Math.pow(2, 24) * 4 / gi.getGaugeFactor() * 1e6; // すみません。圧縮が正となっています。単位はμεです。
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
            double[][] clockmabiki = Mabiki.mabiki(new double[][]{array[0], array[2]}, 5000);
            clockDataset.addSeries("0" + ch, clockmabiki);
            double[][] thx = Mabiki.mabiki(new double[][]{array[0], array[1]}, 5000);
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
                if (false) { // 時刻歴は出力しない
                    st.executeUpdate("create schema if not exists \"R103TimeHistory\"");
                    st.executeUpdate("drop table if exists  \"R103TimeHistory\".\"" + uni.getName() + "/0" + ch + "\"");
                    st.executeUpdate("create table  \"R103TimeHistory\".\"" + uni.getName() + "/0" + ch + "\" "
                            + "(NO identity, \"T[ms]\" double, \"Strain[με]\" double)");
                    logger.log(Level.INFO, "chno=" + ch + ", array[0].length=" + array[0].length);
                    for (int i = 0; i < array[0].length; i++) {
                        try {
                            st.executeUpdate("insert into \"R103TimeHistory\".\"" + uni.getName() + "/0" + ch + "\"  (\"T[ms]\",\"Strain[με]\") "
                                    + "values (" + array[0][i] + "," + array[1][i] + ")");
                        } catch (SQLException e) {
                            logger.log(Level.INFO, "factor=" + factor);
                            throw e;
                        }
                    }
                }
                String schema = "R152FourierS";
                st.executeUpdate("create schema if not exists \"" + schema + "\"");
                st.executeUpdate("drop table if exists  \"" + schema + "\".\"" + uni.getName() + "/0" + ch + "\"");
                st.executeUpdate("create table  \"" + schema + "\".\"" + uni.getName() + "/0" + ch + "\" "
                        + "(\"Freq[Hz]\" double, \"Amp[με*s]\" double, \"Phase[rad]\" double) ");
                for (int i = 0; i < spec[0].length; i++) {
                    double freq = spec[0][i];
                    double amp = spec[1][i];
                    double phase = spec[2][i];
                    st.executeUpdate("insert into \"" + schema + "\".\"" + uni.getName() + "/0" + ch + "\" (\"Freq[Hz]\", \"Amp[με*s]\", \"Phase[rad]\") "
                            + " values (" + freq + "," + amp + "," + phase + " )"
                            + " ");
                }
            }
            specDataset.addSeries("0" + ch, new double[][]{spec[0], spec[1]});
            specmax[ch - 1] = StatUtils.max(spec[1]);
            if (basephase1 == null) {
                basephase1 = new double[1][spec[0].length];
                for (int i = 0; i < spec[0].length; i++) {
                    basephase1[0][i] = spec[2][i];
                }
            }

            for (int j = 0; j < basephase.length; j++) {

                double dphase[] = new double[spec[0].length];
                for (int i = 0; i < spec[0].length; i++) {
                    double dp;
                    dp = spec[2][i] - basephase1[j][i];
                    dphase[i] = FourierUtils.normalizePhase(dp, -0.5 * Math.PI, 1.5 * Math.PI);
                }
                phaseDataset[j].addSeries("0" + ch, new double[][]{spec[0], dphase});

            }
            int datasetIndex = ch - 1;// 230301これで正しい？

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
        //     int seriesCount = phaseDataset.getSeriesCount();

        int grp[] = groupingMax(thmax);
        for (int i = 0;
                i < maxch;
                i++) {
            logger.log(Level.INFO, "grp[i]" + grp[i] + " value= " + thmax[grp[i]]);
            thPlot.getRangeAxis(i).setUpperBound(thmax[grp[i]]);
        }

        grp = groupingMin(thmin);
        for (int i = 0;
                i < maxch;
                i++) {
            thPlot.getRangeAxis(i).setLowerBound(thmin[grp[i]]);
        }
        grp = groupingMax(specmax);
        for (int i = 0;
                i < maxch;
                i++) {
            logger.log(Level.INFO, "grp[i]" + grp[i] + " value= " + specmax[grp[i]]);
            specPlot.getRangeAxis(i).setUpperBound(specmax[grp[i]]);
        }

        JFreeChart timehistoryChart = new JFreeChart(thPlot);

        timehistoryChart.setTitle(
                "TimeHistory " + macaddress + " [" + name + "]");
        timehistoryChart.setBackgroundPaint(Color.WHITE);
        JFreeChart clockChart = new JunXYChartCreator2().setDomainAxis(thDomainAxis)
                .setRangeAxisLabel("ClockData")
                .setDataset(clockDataset).setTitle("Clock " + macaddress + " [" + name + "]")
                .setRangeAxisFixedDimension(40)
                .create();

        JFreeChart spectrumChart = new JFreeChart(specPlot);

        spectrumChart.setTitle(
                "Amplitude Spectrum " + macaddress + " [" + name + "]");
        spectrumChart.setBackgroundPaint(Color.WHITE);

        XYLineAndShapeRenderer phRenderer = new XYLineAndShapeRenderer(false, true);

        phRenderer.setDefaultShapesFilled(
                false);

        JFreeChart[] phaseChart = new JFreeChart[phaseDataset.length];
        for (int j = 0; j < phaseChart.length; j++) {
            phaseChart[j] = new JunXYChartCreator2().setDomainAxisLabel("Freq[Hz]")
                    .setRangeAxisLabel("PhaseDiff [rad]")
                    .setDataset(phaseDataset[j]).setTitle("Phase Spectrum " + macaddress + " [" + name + "]")
                    .setRangeAxisFixedDimension(40)
                    .setDomainAxisAutoRangeIncludesZero(false)
                    .setRangeAxisRange(-0.5 * Math.PI, 1.5 * Math.PI)
                    //                .setLinesAndShapesVisible(false, true)
                    .setRenderer(phRenderer)
                    .create();

        }
        int w = 800, h = 250;
        int graphCount = 0;
        {
            boolean[] shows = {showStrClock, showStrSpecturm, showStrTimeHistory};
            for (boolean show : shows) {
                if (show) {
                    graphCount++;
                }
            }
            if (showStrPhase) {
                graphCount += phaseChart.length;
            }
        }
        if (svgfile
                != null) {
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
                for (int j = 0; j < phaseChart.length; j++) {
                    phaseChart[j].draw(g2d, new Rectangle2D.Double(0, h * gi, w, h));
                    gi++;
                }

            }
//        Path svgpath = svgdir.resolve(name + ".svg");

            try {
                SVGWriter.outputSVG(g2d, svgfile);
            } catch (IOException ex) {
                Logger.getLogger(R152GraphUnitTimeHistoryS.class
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
                for (int j = 0; j < phaseChart.length; j++) {
                    charts[gi] = phaseChart[j];
                    gi++;
                }
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

}
