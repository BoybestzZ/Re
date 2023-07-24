/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed14分析UF;

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
import jun.data.ResultSetUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class Z201StrainSpectrumCheck {

    private static final Logger logger = Logger.getLogger(Z201StrainSpectrumCheck.class.getName());

    public static void main(String[] args) {
        String[] testnames = {/*"D01Q01", "D01Q09", "D01Q11",*/"D02Q05"/*, "D03Q01", "D03Q09"*/};
        String[] strains = {"c01/02", "c01/04", "c01/06", "c01/08"};
        DefaultXYDataset dataset = new DefaultXYDataset();
        for (String testname : testnames) {
            logger.log(Level.INFO, testname);
            try {
                String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res23/ed/ed02/R140DatabaseQ/" + testname + "q";
                Connection con = DriverManager.getConnection(dburl, "junapp", "");
                Statement st = con.createStatement();

                //    ResultSet rs = st.executeQuery("select \"Freq[Hz]\",\"Amp[με*s]\" from \"R155FourierU\".\"a03/01\"");
                for (String strain : strains) {
                    ResultSet rs = st.executeQuery("select \"Freq[Hz]\",\"Amp[με*s]\",\"Phase[rad]\" from \"R155FourierU\".\""+strain+"\"");
                    double[][] ar = ResultSetUtils.createSeriesArray(rs);
                    dataset.addSeries(testname+"_"+strain, new double[][]{ar[0],ar[1]});
                }
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(Z201StrainSpectrumCheck.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        JFreeChart chart = new JunXYChartCreator2()
                .setDataset(dataset)
                .setDomainAxisLabel("Freq.[Hz]").setRangeAxisLabel("Strain Amp. [με*s]")
                .setDomainAxisRange(0.7, 1.7)
                .create();
        Path svgfile = null;// Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed14分析UF/A210StrainSpectrumCheckEW.svg");
        if (svgfile != null)
        try {
            JunChartUtil.svg(svgfile, 500, 250, chart);
        } catch (IOException ex) {
            Logger.getLogger(Z201StrainSpectrumCheck.class.getName()).log(Level.SEVERE, null, ex);
        } else {
            JunChartUtil.show(chart);
        }

    }

}
