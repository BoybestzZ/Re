/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jun.res23.ed.ed02;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.lang.reflect.Field;
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
import jun.fourier.FourierUtils;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.GaugeGroup;

import jun.util.SVGWriter;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * 独自作成。 StrainGaugeInfo を用いてすべての
 *
 * @author jun
 */
public class Q041GraphSpec {
    private static final Logger logger = Logger.getLogger(Q041GraphSpec.class.getName());
    public static void main(String[] args) {

        String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215Day1/database_HE/D01Q01_20230215_123406";

        Path svgdir=Path.of("/home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215Day1/database_HE/Q041GraphSpec");
        if (svgdir!=null && !Files.exists(svgdir)) {
            try {
                Files.createDirectory(svgdir);
            } catch (IOException ex) {
                Logger.getLogger(Q041GraphSpec.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        Connection con;
        try {
            con = DriverManager.getConnection(dburl, "junapp", "");

            Statement st = con.createStatement();

            // ここで全部のStrainGaugeInfo をまとめてリスト（かArray）を作成する。
            GaugeGroup[] ggs = EdefenseInfo.allGaugeGroup;

            //ひずみスペクトル全図を出力
            int ix = 0, iy = 0;
            for (GaugeGroup gg : ggs) {
                
                logger.log(Level.INFO, ""+gg.getName());
                SVGGraphics2D g = null;
                int w = 400, h = 200;

                Path svgStrainSpectrum=null;
                if (svgdir!=null) 
                    svgStrainSpectrum=svgdir.resolve(gg.getName()+".svg");
                if (svgStrainSpectrum != null) {
                    g = SVGWriter.prepareGraphics2D(w * 2, 2 * h);

                }

                DefaultXYDataset amp = new DefaultXYDataset();
                DefaultXYDataset phase = new DefaultXYDataset();

                double[][] a01specx = null;
                int ggnumch = gg.getNumgerOfChannels();

                for (int i = 0; i < ggnumch; i++) {
                    String mac = gg.getMacAddress(i);
                    int unitch = gg.getUnitChannel(i);
                    String unitname = gg.getUnitName(i);

                    ResultSet rs = st.executeQuery("select \"FREQ[Hz]\", \"AMP[LSB*s]\",\"PHASE[rad]\" from "
                            + " SPEC.\"" + mac + "/0" + (unitch) + "/str01\" order by \"FREQ[Hz]\" ");
                    double[][] ar = ResultSetUtils.createSeriesArray(rs);
                    if (a01specx == null) {
                        a01specx = ar;
                    } else {
                        for (int j = 0; j < ar[0].length; j++) {
                            ar[2][j] = FourierUtils.normalizePhase(ar[2][j] - a01specx[2][j], -0.5 * Math.PI, 1.5 * Math.PI);
                        }
                        phase.addSeries(unitname + "/0" + unitch, new double[][]{ar[0], ar[2]});
                    }
                    amp.addSeries(unitname + "/0" + unitch, new double[][]{ar[0], ar[1]});
                }

                NumberAxis freqAxis = new NumberAxis("Freq[Hz]");
                JFreeChart chartSX = new JunXYChartCreator2().setDataset(amp).setDomainAxis(freqAxis).create();//"Spectrum X");
                JFreeChart chartPX = new JunXYChartCreator2().setDataset(phase).setDomainAxis(freqAxis).create();//"Phase X");
                if (svgStrainSpectrum != null) {
                    chartSX.draw(g, new Rectangle2D.Double(0, 0, w, h));
                    chartPX.draw(g, new Rectangle2D.Double(0, h, w, h));

                    freqAxis.setRange(1.5, 5.0);
                    chartSX.draw(g, new Rectangle2D.Double(w, 0, w, h));
                    chartPX.draw(g, new Rectangle2D.Double(w, h, w, h));

                } else {
                    JunChartUtil.show(new JFreeChart[][]{{chartSX}, {chartPX}});
                }

                if (svgStrainSpectrum != null) {
                    try {
                        SVGWriter.outputSVG(g, svgStrainSpectrum);
                    } catch (IOException ex) {
                        Logger.getLogger(Q041GraphSpec.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            }

        } catch (SQLException ex) {
            Logger.getLogger(Q041GraphSpec.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
