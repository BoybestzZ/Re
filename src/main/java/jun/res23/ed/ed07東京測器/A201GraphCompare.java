/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed07東京測器;

import java.awt.Font;
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
import jun.res23.ed.ed06分析T.T220CreateTimeHistoryStrain;
import static jun.res23.ed.ed07東京測器.A200Compare.tmrdb;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import jun.res23.ed.util.StrainGaugeInfo;
import jun.util.JunShapes;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class A201GraphCompare {

    public static final Path svgdir =  Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed07TMR/A201Compare");
    public static final Path pngdir = null;// Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed07TMR/A201Compare");

    public static void main(String[] args) {
//        main(EdefenseInfo.D01Q01);
//        main(EdefenseInfo.D01Q02);
//        main(EdefenseInfo.D01Q03);
//        main(EdefenseInfo.D01Q04);
//        main(EdefenseInfo.D01Q05);
//        main(EdefenseInfo.D01Q06);
//        main(EdefenseInfo.D01Q08);
//        main(EdefenseInfo.D01Q09);
//        main(EdefenseInfo.D01Q10);
//        main(EdefenseInfo.D01Q11);
//
//        main(EdefenseInfo.D02Q01);
//        main(EdefenseInfo.D02Q02);
//        main(EdefenseInfo.D02Q03);
//        main(EdefenseInfo.D02Q05);
//        main(EdefenseInfo.D02Q06);
//        main(EdefenseInfo.D02Q07);
//        main(EdefenseInfo.D02Q08);
//
//        main(EdefenseInfo.D03Q01);
//        main(EdefenseInfo.D03Q02);
//        main(EdefenseInfo.D03Q03);
//        main(EdefenseInfo.D03Q05);
//        main(EdefenseInfo.D03Q06);
//        main(EdefenseInfo.D03Q08);
//        main(EdefenseInfo.D03Q09);

        main(EdefenseInfo.D01Q01, 25,30);
//        main(EdefenseInfo.D01Q02, 0, 30);
//        main(EdefenseInfo.D02Q08, 50, 53);
//        main(EdefenseInfo.D03Q01,40 ,43);
//        main(EdefenseInfo.D03Q08, 10, 13);
//        main(EdefenseInfo.D03Q08, 40, 43);

    }

    public static void main(EdefenseKasinInfo kasin) {
        main(kasin, 0, Double.POSITIVE_INFINITY);
    }

    public static void main(EdefenseKasinInfo kasin, double startSec, double endSec) {
        try {
            Connection contmr = DriverManager.getConnection(tmrdb, "junapp", "");

            Statement sttmr = contmr.createStatement();

//            EdefenseKasinInfo kasin = EdefenseInfo.D03Q09;
            ResultSet rs = sttmr.executeQuery("select \"TIME[s]\"-(" + kasin.getTmrTimeDiffSeconds() + "), \"CK-LA-A-L-L-4μStrain\" "
                    + "from \"" + kasin.getTestName() + "\" order by 1");

            double[][] ar = ResultSetUtils.createSeriesArray(rs);
            if (Double.isInfinite(endSec)) {
                ar = Mabiki.mabiki(ar, 1000);
            }
            DefaultXYDataset dataset = new DefaultXYDataset();
            dataset.addSeries("TMR", ar);

            contmr.close();

            Connection con = DriverManager.getConnection(T220CreateTimeHistoryStrain.outputDb, "junapp", "");
            StrainGaugeInfo gauge = StrainGaugeInfo.GSLA_A_L_L_4;

            Statement st = con.createStatement();
            rs = st.executeQuery("select \"TIME[s]\",-\"Strain[με]\" from "
                    + "\"" + T220CreateTimeHistoryStrain.outputSchema + "\".\"" + gauge.getShortName() + "\""
                    + " where TESTNAME='" + kasin.getTestName() + "t' order by 1");
            ar = ResultSetUtils.createSeriesArray(rs);
            if (Double.isInfinite(endSec)) {
                ar = Mabiki.mabiki(ar, 1000);
            }

            dataset.addSeries("Raspi", ar);

            con.close();

            XYLineAndShapeRenderer re = new XYLineAndShapeRenderer(true, false);
            re.setSeriesStroke(0, JunShapes.NORMAL_LINE);
            re.setSeriesStroke(1, JunShapes.MEDIUM_LINE);

            NumberAxis xaxis = new NumberAxis("Time[s]");
            xaxis.setLowerBound(startSec);
            if (Double.isInfinite(endSec)) {
                xaxis.setUpperBound(ar[0][ar[0].length - 1]);
            } else {
                xaxis.setUpperBound(endSec);
            }

            JFreeChart chart = new JunXYChartCreator2()
                    .setRenderer(re)
                    .setRangeAxisAutoRangeIncludesZero(false)
                    .setDomainAxis(xaxis)
                    .setRangeAxisLabel("Strain[με]")
                    .setDataset(dataset).create();

            LegendTitle legend = chart.getLegend();
            legend.setBorder(1, 1, 1, 1);

            chart.removeLegend();
            XYTitleAnnotation xyta = new XYTitleAnnotation(0.98, 0.98, legend, RectangleAnchor.TOP_RIGHT);
            chart.getXYPlot().addAnnotation(xyta);

            if (svgdir != null) {
                Path svgfile = svgdir.resolve(kasin.getTestName() + (Double.isInfinite(endSec) ? "" : "Z") + ".svg");

                try {
                    JunChartUtil.svg(svgfile, 360, 180, chart);
                } catch (IOException ex) {
                    Logger.getLogger(A201GraphCompare.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (pngdir != null) {

                Font basicFont = new Font(Font.SANS_SERIF, Font.PLAIN, 40);
                JunChartUtil.customizeChart(chart, basicFont);
                legend.setItemFont(basicFont);

                

                Path pngfile = pngdir.resolve(kasin.getTestName() + (Double.isInfinite(endSec) ? "" : "Z") + ".png");
                try {
                    JunChartUtil.png(pngfile, 1000, 500, chart);
                } catch (IOException ex) {
                    Logger.getLogger(A201GraphCompare.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(A201GraphCompare.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
