/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

import java.io.IOException;
import java.nio.file.Files;
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
import jun.data.ResultSetUtils;
import jun.fourier.FFTv2;
import jun.fourier.FourierUtils;
import jun.res23.ed.ed08.B100ReadCSV;
import jun.res23.ed.ed08.B101CompareToInteg2Disp;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import jun.signal.digitalfilter.DigitalFilter;
import org.apache.commons.math3.stat.StatUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * T300→T301 cutoff周波数をもう少し大きく。
 *
 * @author jun
 */
public class T301Integ2StoryDisp {

    public static final String outputSchema = "T301Integ2StoryDisp";
    public static final String dburl = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res23/ed/ed06分析T/res22ed06";
    private static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed06分析T/T301Integ2StoryDisp");


    public static void main(String[] args) {
        try {
            if (false) {
                calculate(EdefenseInfo.D03Q09);
                B101CompareToInteg2Disp.main(EdefenseInfo.D03Q09);
//                calculate(EdefenseInfo.D01Q02);
//                calculate(EdefenseInfo.D01Q03);
//                calculate(EdefenseInfo.D01Q04);
//                calculate(EdefenseInfo.D01Q05);
//                calculate(EdefenseInfo.D01Q06);
//                         calculate(EdefenseInfo.D01Q07);
//                calculate(EdefenseInfo.D01Q08);
//                calculate(EdefenseInfo.D01Q09);
//                calculate(EdefenseInfo.D01Q10);
//                calculate(EdefenseInfo.D01Q11);

//                                calculate(EdefenseInfo.D02Q01);
//                calculate(EdefenseInfo.D02Q02);
//                calculate(EdefenseInfo.D02Q03);
////            calculate(EdefenseInfo.D02Q04);
//                calculate(EdefenseInfo.D02Q05);
//                calculate(EdefenseInfo.D02Q06);
//                calculate(EdefenseInfo.D02Q07);
//                calculate(EdefenseInfo.D02Q08);
//                calculate(EdefenseInfo.D03Q01);
//                calculate(EdefenseInfo.D03Q02);
//                calculate(EdefenseInfo.D03Q03);
//                calculate(EdefenseInfo.D03Q04);
//                calculate(EdefenseInfo.D03Q05);
//                calculate(EdefenseInfo.D03Q06);
////            calculate(EdefenseInfo.D03Q07);
//                calculate(EdefenseInfo.D03Q08);
//                calculate(EdefenseInfo.D03Q09);
            }

         try {
                svg(EdefenseInfo.D01Q01);
                svg(EdefenseInfo.D01Q02);
                svg(EdefenseInfo.D01Q03);
                svg(EdefenseInfo.D01Q04);
                svg(EdefenseInfo.D01Q05);
                svg(EdefenseInfo.D01Q06);
                //          svg(EdefenseInfo.D01Q07);欠番
                svg(EdefenseInfo.D01Q08);
                svg(EdefenseInfo.D01Q09);
                svg(EdefenseInfo.D01Q10);
                svg(EdefenseInfo.D01Q11);
                svg(EdefenseInfo.D02Q01);
                svg(EdefenseInfo.D02Q02);
                svg(EdefenseInfo.D02Q03);
//            svg(EdefenseInfo.D02Q04);欠番
                svg(EdefenseInfo.D02Q05);
                svg(EdefenseInfo.D02Q06);
//                svg(EdefenseInfo.D02Q07); 失敗した福島
                svg(EdefenseInfo.D02Q08);
                svg(EdefenseInfo.D03Q01);
                svg(EdefenseInfo.D03Q02);
                svg(EdefenseInfo.D03Q03);
                svg(EdefenseInfo.D03Q04);
                svg(EdefenseInfo.D03Q05);
                svg(EdefenseInfo.D03Q06);
//            svg(EdefenseInfo.D03Q07);欠番
                svg(EdefenseInfo.D03Q08);
                svg(EdefenseInfo.D03Q09);
            } catch (IOException ex) {
                Logger.getLogger(T301Integ2StoryDisp.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(T301Integ2StoryDisp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void calculate(EdefenseKasinInfo test) throws SQLException {

//            EdefenseKasinInfo test = EdefenseInfo.D01Q01;//random
//            EdefenseKasinInfo test = EdefenseInfo.D01Q02;//kumamoto
//            EdefenseKasinInfo test = EdefenseInfo.D01Q03;//tohoku
//            EdefenseKasinInfo test = EdefenseInfo.D02Q03;//kobe100
        Connection con = DriverManager.getConnection(dburl, "junapp", "");

        Statement st = con.createStatement();

        st.executeUpdate("create schema if not exists \"" + outputSchema + "\"");
        double startTime = 0.0;
        double taperWidthSecond = 5;

        ResultSet rs = st.executeQuery("select \"TIME[s]\", \"Y[gal]\" from \"T220TimeHistoryStrain\".\"k01/01\" where TESTNAME='" + test.getTestName() + "t' and \"TIME[s]\">" + startTime + " order by \"TIME[s]\" ");
        double[][] k01 = ResultSetUtils.createSeriesArray(rs);
        rs = st.executeQuery("select \"TIME[s]\", \"Y[gal]\" from \"T220TimeHistoryStrain\".\"k02/01\" where TESTNAME='" + test.getTestName() + "t' and \"TIME[s]\">" + startTime + " order by \"TIME[s]\" ");
        double[][] k02 = ResultSetUtils.createSeriesArray(rs);
        rs = st.executeQuery("select \"TIME[s]\", \"Y[gal]\" from \"T220TimeHistoryStrain\".\"k03/01\" where TESTNAME='" + test.getTestName() + "t' and \"TIME[s]\">" + startTime + " order by \"TIME[s]\" ");
        double[][] k03 = ResultSetUtils.createSeriesArray(rs);
        rs = st.executeQuery("select \"TIME[s]\", \"Y[gal]\" from \"T220TimeHistoryStrain\".\"k04/01\" where TESTNAME='" + test.getTestName() + "t' and \"TIME[s]\">" + startTime + " order by \"TIME[s]\" ");
        double[][] k04 = ResultSetUtils.createSeriesArray(rs);

        double[] acc = new double[k01[0].length];
        double[] time = k01[0];

        for (int i = 0; i < acc.length; i++) {
            acc[i] = 0.5 * ((k01[1][i] + k02[1][i]) - (k03[1][i] + k04[1][i]));
        }
        double dt = 0.02;

        // HPF準備および積分器準備。
        double fc = 0.25;

        DigitalFilter hpf = DigitalFilter.createHPF(fc, dt);
//        DigitalFilter lpf = DigitalFilter.createLPF(20, dt);
        DigitalFilter timeshift = DigitalFilter.createTimeShift(1); //  HPFは0.02秒くらい早めちゃうので、1ステップぶん遅れるフィルタを追加しとく。

        DigitalFilter integrator = DigitalFilter.createIntegrater(dt);
//        DigitalFilter.showTransferFunction(hpf, dt); // この伝達関数を見ると、 1Hz でおよそ 0.02秒進む。2回LPFを掛けているので、

        // 加速度を基線補正
        acc = FFTv2.baseLineAdjustment(acc, 250);
        // 最初と最後にテーパーをかける。
        acc = FourierUtils.applyTaper(acc, (int) (taperWidthSecond / dt)); //5秒で、0.02秒刻みだと、 250個。
        // さらに LPF 適用
        double[] filtered = timeshift.filter3(hpf.filter3(acc));
//            double[] filtered = hpf.filter3(acc);
        // 1回目積分実行して速度を計算
        double[] vel = integrator.filter3(filtered);

      vel = FFTv2.baseLineAdjustment(vel, 250);            // 速度を基線補正はしなくてもいいらしい。
        // 得られた速度にLPF 適用
        vel = timeshift.filter3(hpf.filter3(vel));
//            vel = hpf.filter3(vel);

        // 2回目積分実行して変形を計算
        double[] disp = integrator.filter3(vel);
        //            double[] integrated = FourierUtils.performDoubleIntegrate(dt, k01[1], 0.1, 20);
        st.executeUpdate("drop table if exists \"" + outputSchema + "\".\"" + test.getTestName() + "t\"");

        st.executeUpdate("create table if not exists \"" + outputSchema + "\".\"" + test.getTestName() + "t\" (\"TimePerTest[s]\" real, \"RelAcc[gal]\" real, \"RelVel[cm/s]\" real , \"RelDisp[cm]\" real)");
        for (int i = 0; i < disp.length; i++) {
            if (Double.isNaN(disp[i])) {
                continue;
            }
            st.executeUpdate("insert into \"" + outputSchema + "\".\"" + test.getTestName() + "t\" (\"TimePerTest[s]\",\"RelAcc[gal]\",\"RelVel[cm/s]\",\"RelDisp[cm]\")"
                    + " values "
                    + "(" + time[i] + "," + acc[i] + "," + vel[i] + "," + disp[i] + ")");
        }

        con.close();

    }

    public static void show(EdefenseKasinInfo test) throws SQLException {
        JFreeChart[] charts = createChart(test);

        JunChartUtil.show(test.getTestName(), charts);
    }

    public static void svg(EdefenseKasinInfo test) throws SQLException, IOException {
        JFreeChart[] charts = createChart(test);

        if (!Files.exists(svgdir)) {
            Files.createDirectory(svgdir);
        }
        JunChartUtil.svg(svgdir.resolve(test.getTestName() + "t.svg"), 500, 250, charts);

    }

    public static JFreeChart[] createChart(EdefenseKasinInfo test) throws SQLException {

//        EdefenseKasinInfo test = EdefenseInfo.D01Q01;
        Connection con = DriverManager.getConnection(dburl, "junapp", "");

        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery("select * from \"" + outputSchema + "\".\"" + test.getTestName() + "t\"");
        double[][] ar = ResultSetUtils.createSeriesArray(rs);

        double[] time = ar[0];
        double[] acc = ar[1];
        double[] vel = ar[2];
        double[] disp = ar[3];

        JFreeChart dispChart, velChart, accChart, disp2Chart;

        {
            DefaultXYDataset dataset = new DefaultXYDataset();
            dataset.addSeries("", new double[][]{time, acc});
            accChart = new JunXYChartCreator2().setDataset(dataset).create();
        }
        {
            DefaultXYDataset dataset = new DefaultXYDataset();
            dataset.addSeries("", new double[][]{time, vel});
            velChart = new JunXYChartCreator2().setDataset(dataset).create();
        }

        {
            DefaultXYDataset dispDataset = new DefaultXYDataset();
            dispDataset.addSeries("disp", new double[][]{time, disp});
            rs = st.executeQuery("select \"TimePerTest[s]\", \"BendingMomentPerTest[kNm]\"+\"AxialForcePerTest[kN]\"*(0.11+0.350/2) from \"T231TimeHistoryNM\".\"LA3S2\" where TESTNAME='" + test.getTestName() + "t'");
            DefaultXYDataset momentDataset = ResultSetUtils.createDataset(rs);

            rs = st.executeQuery("select \"TimePerTest[s]\", \"BendingMomentPerTest[kNm]\"+\"AxialForcePerTest[kN]\"*(0.11+0.350/2) from \"T231TimeHistoryNM\".\"LA3S2\" where TESTNAME='" + test.getTestName() + "t' order by 2 desc limit 1");
            rs.next();
            double peakTime = rs.getDouble(1);

            XYPlot plot = new XYPlot();
            plot.setDataset(0, dispDataset);
            plot.setDataset(1, momentDataset);

            plot.setRangeAxis(0, new NumberAxis("Disp[cm]"));
            plot.setRangeAxis(1, new NumberAxis("Moment [kNm] LA3S1"));
            plot.mapDatasetToRangeAxis(0, 0);
            plot.mapDatasetToRangeAxis(1, 1);
            plot.setDomainAxis(new NumberAxis("Time[s]"));
            plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
            plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));

            dispChart = new JFreeChart(plot);
            JunChartUtil.matchRangeZero(plot);
            try {
                disp2Chart = new JFreeChart((XYPlot) plot.clone());

                disp2Chart.getXYPlot().getDomainAxis().setRange(peakTime - 3, peakTime + 3);
                JunChartUtil.matchRangeZero(disp2Chart.getXYPlot());
            } catch (CloneNotSupportedException ex) {

                throw new RuntimeException(ex);
            }

        }
        con.close();
        return new JFreeChart[]{accChart, velChart, dispChart, disp2Chart};

    }

}
