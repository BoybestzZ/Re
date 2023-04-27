/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.data.ResultSetUtils;
import jun.fourier.FFTv2;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class T250bCheckStrainTimeHistory1 {

    private static final Logger logger = Logger.getLogger(T250bCheckStrainTimeHistory1.class.getName());

    public static void main(String[] args) {
        try {
            String gauge1 = "h03/03";
            String gauge2 = "h03/07";
            String testname = "D01Q01";
            Connection con1 = DriverManager.getConnection("jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R140DatabaseQ/" + testname + "q", "junapp", "");
            Statement st1 = con1.createStatement();
            ResultSet rs1 = st1.executeQuery("select \"TIME[s]\", \"" + gauge1 + "_Strain[με]\", \"" + gauge2 + "_Strain[με]\" from \"R210Resample\"");

            double[][] ar1 = ResultSetUtils.createSeriesArray(rs1);
            ar1[1] = FFTv2.zeroAverage(ar1[1]);
            ar1[2] = FFTv2.zeroAverage(ar1[2]);

            DefaultXYDataset dataset = new DefaultXYDataset();
            dataset.addSeries(gauge1, new double[][]{ar1[0], ar1[1]});

            dataset.addSeries(gauge2, new double[][]{ar1[0], ar1[2]});

            JunChartUtil.show(dataset);

            con1.close();

        } catch (SQLException ex) {
            Logger.getLogger(T250bCheckStrainTimeHistory1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
